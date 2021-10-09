package com.nashtech.AssetManagement_backend.service.Impl;

import com.nashtech.AssetManagement_backend.dto.AssignmentDTO;
import com.nashtech.AssetManagement_backend.dto.AssignmentDetailDTO;
import com.nashtech.AssetManagement_backend.dto.RequestAssignDetailDTO;
import com.nashtech.AssetManagement_backend.entity.*;
import com.nashtech.AssetManagement_backend.exception.BadRequestException;
import com.nashtech.AssetManagement_backend.exception.ConflictException;
import com.nashtech.AssetManagement_backend.exception.ResourceNotFoundException;
import com.nashtech.AssetManagement_backend.repository.AssetRepository;
import com.nashtech.AssetManagement_backend.repository.AssignmentRepository;
import com.nashtech.AssetManagement_backend.repository.RequestAssignRepository;
import com.nashtech.AssetManagement_backend.repository.UserRepository;
import com.nashtech.AssetManagement_backend.service.AssignmentService;
import com.nashtech.AssetManagement_backend.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AssignmentServiceImpl implements AssignmentService {
    @Autowired
    AssignmentRepository assignmentRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    AssetRepository assetRepository;

    @Autowired
    RequestAssignRepository requestAssignRepository;

    @Autowired
    UserService userService;

    @Autowired
    JavaMailSender javaMailSender;

    @Override
    public List<AssignmentDTO> getAllByAdminLocation(String username) {
        LocationEntity location = userService.findByUserName(username).getUserDetail().getLocation();
        List<AssignmentDTO> assignmentDTOs = assignmentRepository.findAllByAdmimLocation(location.getId())
                .stream().map(AssignmentDTO::new).collect(Collectors.toList());
        return assignmentDTOs;
    }

    @Override
    public List<AssignmentDTO> getAssignmentsByUser(String username) {
        UsersEntity user = userRepository.findByUserName(username).get();
        List<AssignmentState> states = new ArrayList<>();
        states.add(AssignmentState.WAITING_FOR_ACCEPTANCE);
        states.add(AssignmentState.ACCEPTED);
        states.add(AssignmentState.WAITING_FOR_RETURNING);
        states.add(AssignmentState.COMPLETED);
        states.add(AssignmentState.DECLINED);
        List<AssignmentDTO> assignmentDTOs = assignmentRepository.findByAssignTo_StaffCodeAndAssignedDateIsLessThanEqualAndStateIn(user.getStaffCode(), new Date(), states)
                .stream().map(AssignmentDTO::new).collect(Collectors.toList());
        assignmentDTOs.sort(Comparator.comparing(AssignmentDTO::getId));
        return assignmentDTOs;
    }

    @Override
    public AssignmentDTO getAssignmentById(Long assignmentId) {
        AssignmentEntity assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found!"));
        return new AssignmentDTO(assignment);
    }

    @Override
    public AssignmentDTO save(AssignmentDTO assignmentDTO) {
        AssignmentEntity assignment = assignmentDTO.toEntity();
        RequestAssignEntity requestAssign = null;
        if (assignmentDTO.getRequestAssignId() != null) {
            requestAssign = requestAssignRepository.findById(assignmentDTO.getRequestAssignId())
                    .orElseThrow(() -> new ResourceNotFoundException("Request assign not found!"));
            requestAssign.setState(RequestAssignState.ACCEPTED);
        }
        UserDetailEntity assignTo = userRepository.findByUserName(assignmentDTO.getAssignedTo())
                .orElseThrow(() -> new ResourceNotFoundException("AssignTo not found!")).getUserDetail();
        UserDetailEntity assignBy = userRepository.findByUserName(assignmentDTO.getAssignedBy())
                .orElseThrow(() -> new ResourceNotFoundException("AssignBy not found!")).getUserDetail();

        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        Date todayDate = null;
        Date assignedDate = null;
        try {
            todayDate = dateFormatter.parse(dateFormatter.format(new Date()));
            assignedDate = dateFormatter.parse(dateFormatter.format(assignmentDTO.getAssignedDate()));
        } catch (ParseException e) {
            throw new RuntimeException("Parse date error");
        }

        if (assignedDate.before(todayDate)) {
            throw new ConflictException("The assigned date is current or future!");
        }

        if (assignTo.getLocation() != assignBy.getLocation()) {
            throw new ConflictException("The location of assignTo difference from admin!");
        }

        List<AssignmentDetailDTO> assignmentDetailDTOList = assignmentDTO.getAssignmentDetails();
        List<AssignmentDetailEntity> assignmentDetails = new ArrayList<>();
        // check asset's state
        for (AssignmentDetailDTO assignmentDetailDTO : assignmentDetailDTOList) {
            AssetEntity asset = assetRepository.findById(assignmentDetailDTO.getAssetCode())
                    .orElseThrow(() -> new ResourceNotFoundException("Asset not found!"));
//            if (asset.getState() != AssetState.AVAILABLE) {
//                throw new ConflictException("Asset must available state!");
//            }

            for (AssignmentDetailEntity x : asset.getAssignmentDetails()) {
                if (assignmentDTO.getAssignedDate().before(assignmentDTO.getIntendedReturnDate())) {
                    if (!x.getState().equals(AssignmentState.DECLINED)) {
                        if (!(assignmentDTO.getIntendedReturnDate().before(x.getAssignment().getAssignedDate())
                                || assignmentDTO.getAssignedDate().after(x.getAssignment().getIntendedReturnDate()))) {
                            throw new ConflictException("Asset not available in this time!");
                        }
                    }

                } else {
                    throw new ConflictException("AssignedDate and IntendedReturnDate are invalid!");
                }
            }

        }

        for (AssignmentDetailDTO assignmentDetailDTO : assignmentDetailDTOList) {
            AssetEntity asset = assetRepository.getById(assignmentDetailDTO.getAssetCode());
            AssignmentDetailEntity assignmentDetail = new AssignmentDetailEntity();
//            asset.setState(AssetState.ASSIGNED);
            assignmentDetail.setAsset(asset);
            assignmentDetail.setAssignment(assignment);
            assignmentDetail.setState(AssignmentState.WAITING_FOR_ACCEPTANCE);
            assignmentDetails.add(assignmentDetail);
        }

        assignment.setRequestAssign(requestAssign); // able null
        assignment.setAssignTo(assignTo);
        assignment.setAssignBy(assignBy);
        assignment.setCreatedDate(new Date());
        assignment.setState(AssignmentState.WAITING_FOR_ACCEPTANCE);
        assignment.setAssignmentDetails(assignmentDetails);

//        SimpleMailMessage msg = new SimpleMailMessage();
//        msg.setTo(assignTo.getEmail());
//        msg.setSubject("New assignment assigned to you");
//        msg.setText("Your administrator has assigned you a new assignment: \nAsset " +
//                "code: "+assignment.getAssetEntity().getAssetCode()+
//                "\nAsset name: "+ assignment.getAssetEntity().getAssetName()+
//                "\nDate: "+dateFormatter.format(assignment.getAssignedDate())+
//                "\nPlease check your assignment by your account\nKind Regards,\nAdministrator");
//        javaMailSender.send(msg);

        return new AssignmentDTO(assignmentRepository.save(assignment));
    }

    public AssignmentDTO updateAssignment(AssignmentDTO assignmentDTO) {
        AssignmentEntity assignment = assignmentRepository.findById(assignmentDTO.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found!"));
        if (assignment.getState() != AssignmentState.WAITING_FOR_ACCEPTANCE) {
            throw new ConflictException("Assignment is editable while in state Waiting for acceptance!");
        }

        // check assigned date must be current or future
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        Date todayDate = null;
        Date assignedDate = null;
        try {
            todayDate = dateFormatter.parse(dateFormatter.format(new Date()));
            assignedDate = dateFormatter.parse(dateFormatter.format(assignmentDTO.getAssignedDate()));
        } catch (ParseException e) {
            throw new RuntimeException("Parse date error");
        }

        if (assignedDate.before(todayDate)) {
            throw new ConflictException("The assigned date is current or future!");
        }

        List<AssignmentDetailDTO> assignmentDetailDTOList = assignmentDTO.getAssignmentDetails();
        List<AssignmentDetailEntity> assignmentDetails = assignment.getAssignmentDetails();
        // check asset's state
        for (AssignmentDetailDTO assignmentDetailDTO : assignmentDetailDTOList) {
            AssetEntity asset = assetRepository.findById(assignmentDetailDTO.getAssetCode())
                    .orElseThrow(() -> new ResourceNotFoundException("Asset not found!"));

            for (AssignmentDetailEntity x : asset.getAssignmentDetails().stream().filter(x -> !x.getAssignment().getId().equals(assignment.getId())).collect(Collectors.toList())) {
                if (assignmentDTO.getAssignedDate().before(assignmentDTO.getIntendedReturnDate())) {
                    if (!(assignmentDTO.getIntendedReturnDate().before(x.getAssignment().getAssignedDate())
                            || assignmentDTO.getAssignedDate().after(x.getAssignment().getIntendedReturnDate()))) {
                        throw new ConflictException("Asset not available in this time!");
                    }
                } else {
                    throw new ConflictException("Date is invalid!");
                }
            }

        }

        // Xóa assignment detail không còn trong assignment và xóa assignment detail dto đã có trong assignment
        for (int i = 0; i < assignmentDetails.size(); i++) {
            boolean isExists = false;
            for (int j = 0; j < assignmentDetailDTOList.size(); j++) {
                // Asset không còn trong assignment thì xóa assignment detail đó
                if (assignmentDetailDTOList.get(j).getAssetCode().equalsIgnoreCase(assignmentDetails.get(i).getAsset().getAssetCode())) {
                    isExists = true;
                    assignmentDetailDTOList.remove(j);
                    j--;
                }
            }
            if (isExists == false) {
                assignmentDetails.remove(i);
                i--;
            }
        }

        for (int i = 0; i < assignmentDetailDTOList.size(); i++) {
            AssetEntity asset = assetRepository.getById(assignmentDetailDTOList.get(i).getAssetCode());
            AssignmentDetailEntity newAssignmentDetail = new AssignmentDetailEntity();
            newAssignmentDetail.setAsset(asset);
            newAssignmentDetail.setAssignment(assignment);
            newAssignmentDetail.setState(AssignmentState.WAITING_FOR_ACCEPTANCE);
            assignmentDetails.add(newAssignmentDetail);
        }

        UserDetailEntity assignTo = assignment.getAssignTo();
        UserDetailEntity assignBy;

        // case: new assign to
        if (!assignment.getAssignTo().getUser().getUserName().equalsIgnoreCase(assignmentDTO.getAssignedTo())) {
            assignTo = userRepository.findByUserName(assignmentDTO.getAssignedTo())
                    .orElseThrow(() -> new ResourceNotFoundException("AssignTo not found!")).getUserDetail();
            assignment.setAssignTo(assignTo);
        }

        // case: new assign by
        if (!assignment.getAssignBy().getUser().getUserName().equalsIgnoreCase(assignmentDTO.getAssignedBy())) {
            assignBy = userRepository.findByUserName(assignmentDTO.getAssignedBy())
                    .orElseThrow(() -> new ResourceNotFoundException("AssignBy not found!")).getUserDetail();
            assignment.setAssignBy(assignBy);

            // check location
            if (assignTo.getLocation() != assignBy.getLocation()) {
                throw new ConflictException("The location of assignTo difference from admin!");
            }
        }

        assignment.setAssignedDate(assignmentDTO.getAssignedDate());
        assignment.setIntendedReturnDate(assignmentDTO.getIntendedReturnDate());
        assignment.setNote(assignmentDTO.getNote());
        assignment.setState(AssignmentState.WAITING_FOR_ACCEPTANCE);
//        assignment.setCreatedDate(new Date());

//        SimpleMailMessage msg = new SimpleMailMessage();
//        msg.setTo(assignTo.getEmail());
//        msg.setSubject("New assignment assigned to you");
//        msg.setText("Your administrator has assigned you a new assignment: \nAsset " +
//                "code: "+assignment.getAssetEntity().getAssetCode()+
//                "\nAsset name: "+ assignment.getAssetEntity().getAssetName()+
//                "\nDate: "+dateFormatter.format(assignment.getAssignedDate())+
//                "\nPlease check your assignment by your account\nKind Regards,\nAdministrator");
//        javaMailSender.send(msg);
        return new AssignmentDTO(assignmentRepository.save(assignment));
    }

    @Override
    public boolean deleteAssignment(Long assignmentId, LocationEntity location) {
//        AssignmentEntity assignment = assignmentRepository.findById(assignmentId)
//                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found!"));
//
//        if (!assignment.getAssignBy().getLocation().equals(location)) { // compare object !!!!
//            throw new BadRequestException("Invalid access!");
//        }
//
//        if (assignment.getState() == AssignmentState.ACCEPTED) {
//            throw new BadRequestException("Assignment delete when state is Waiting for acceptance or Declined!");
//        }
//
//        AssetEntity assetEntity = assetRepository.getById(assignment.getAssetEntity().getAssetCode());
//        if(assignment.getState() == AssignmentState.WAITING_FOR_ACCEPTANCE) // if assignment is waiting for acceptance then set asset state is available
//            assetEntity.setState(AssetState.AVAILABLE);
//        assetRepository.save(assetEntity);
//
//        assignmentRepository.deleteById(assignmentId);
        return true;
    }

    @Override
    public AssignmentDTO updateStateAssignment(AssignmentDTO assignmentDTO, String username) {
        AssignmentEntity assignment = assignmentRepository.findById(assignmentDTO.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found!"));
        if (!assignment.getAssignTo().getUser().getUserName().equals(username))
            throw new BadRequestException("Assignment updated when it assigns you!");
        if (assignment.getState() != AssignmentState.WAITING_FOR_ACCEPTANCE)
            throw new BadRequestException("Assignment updated when state is Waiting for acceptance!");

//        AssetEntity asset = assignment.getAssetEntity();
//        if (assignmentDTO.getState() == AssignmentState.DECLINED) { // set asset's state is available when user decline assignment
//            asset.setState(AssetState.AVAILABLE);
            assignment.setNote(assignmentDTO.getNote());
            for (AssignmentDetailEntity a : assignment.getAssignmentDetails()) {
                a.setState(assignmentDTO.getState());
            }

        assignment.setState(assignmentDTO.getState());
//        assignment.setAssetEntity(asset);
        return new AssignmentDTO(assignmentRepository.save(assignment));
    }
}
