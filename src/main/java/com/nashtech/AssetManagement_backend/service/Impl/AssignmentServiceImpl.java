package com.nashtech.AssetManagement_backend.service.Impl;

import com.nashtech.AssetManagement_backend.dto.AssignmentDTO;
import com.nashtech.AssetManagement_backend.dto.AssignmentDetailDTO;
import com.nashtech.AssetManagement_backend.entity.*;
import com.nashtech.AssetManagement_backend.exception.BadRequestException;
import com.nashtech.AssetManagement_backend.exception.ConflictException;
import com.nashtech.AssetManagement_backend.exception.ResourceNotFoundException;
import com.nashtech.AssetManagement_backend.repository.AssetRepository;
import com.nashtech.AssetManagement_backend.repository.AssignmentRepository;
import com.nashtech.AssetManagement_backend.repository.UserRepository;
import com.nashtech.AssetManagement_backend.service.AssignmentService;
import com.nashtech.AssetManagement_backend.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
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
    UserService userService;

    @Autowired
    JavaMailSender javaMailSender;

    @Override
    public List<AssignmentDTO> getAllByAdmimLocation(String username) {
        LocationEntity location = userService.findByUserName(username).getUserDetail().getLocation();
        List<AssignmentDTO> assignmentDTOs = assignmentRepository.findAllByAdmimLocation(location.getId())
                .stream().map(AssignmentDTO::toDTO).collect(Collectors.toList());
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
        states.add(AssignmentState.CANCELED_ASSIGN);
        List<AssignmentDTO> assignmentDTOs = assignmentRepository.findByAssignTo_StaffCodeAndAssignedDateIsLessThanEqualAndStateIn(user.getStaffCode(), new Date(), states)
                .stream().map(AssignmentDTO::toDTO).collect(Collectors.toList());
        assignmentDTOs.sort(Comparator.comparing(AssignmentDTO::getId));
        return assignmentDTOs;
    }

    @Override
    public AssignmentDTO getAssignmentById(Long assignmentId) {
        AssignmentEntity assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found!"));
        return AssignmentDTO.toDTO(assignment);
    }

    @Override
    public AssignmentDTO save(AssignmentDTO assignmentDTO) {
        AssignmentEntity assignment = AssignmentDTO.toEntity(assignmentDTO);
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

        if(assignedDate.before(todayDate)) {
            throw new ConflictException("The assigned date is current or future!");
        }

        if (assignTo.getLocation() != assignBy.getLocation()) {
            throw new ConflictException("The location of assignTo difference from admin!");
        }

        List<AssignmentDetailDTO> assignmentDetailDTOList = assignmentDTO.getAssignmentDetails();
        List<AssignmentDetail> assignmentDetails = new ArrayList<>();
        // check asset's state
        for(AssignmentDetailDTO assignmentDetailDTO : assignmentDetailDTOList) {
            AssetEntity asset = assetRepository.findById(assignmentDetailDTO.getAssetCode())
                    .orElseThrow(() -> new ResourceNotFoundException("Asset not found!"));
            if (asset.getState() != AssetState.AVAILABLE) {
                throw new ConflictException("Asset must available state!");
            }
        }

        for(AssignmentDetailDTO assignmentDetailDTO : assignmentDetailDTOList) {
            AssetEntity asset = assetRepository.getById(assignmentDetailDTO.getAssetCode());
            AssignmentDetail assignmentDetail = new AssignmentDetail();
            asset.setState(AssetState.ASSIGNED);
            assignmentDetail.setAsset(asset);
            assignmentDetail.setAssignment(assignment);
            assignmentDetails.add(assignmentDetail);
        }

        assignment.setAssignmentDetails(assignmentDetails);
        assignment.setState(AssignmentState.WAITING_FOR_ACCEPTANCE);
        assignment.setAssignTo(assignTo);
        assignment.setAssignBy(assignBy);
        if (assignmentDTO.getAssignedDate() == null)
            assignment.setAssignedDate(new Date());

//        SimpleMailMessage msg = new SimpleMailMessage();
//        msg.setTo(assignTo.getEmail());
//        msg.setSubject("New assignment assigned to you");
//        msg.setText("Your administrator has assigned you a new assignment: \nAsset " +
//                "code: "+assignment.getAssetEntity().getAssetCode()+
//                "\nAsset name: "+ assignment.getAssetEntity().getAssetName()+
//                "\nDate: "+dateFormatter.format(assignment.getAssignedDate())+
//                "\nPlease check your assignment by your account\nKind Regards,\nAdministrator");
//        javaMailSender.send(msg);

        return AssignmentDTO.toDTO(assignmentRepository.save(assignment));
    }

    @Override
    public AssignmentDTO update(AssignmentDTO assignmentDTO) {
        AssignmentEntity assignment = assignmentRepository.findById(assignmentDTO.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found!"));
        if (assignment.getState() != AssignmentState.WAITING_FOR_ACCEPTANCE) {
            throw new ConflictException("Assignment is editable while in state Waiting for acceptance!");
        }

        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        Date todayDate = null;
        Date assignedDate = null;
        try {
            todayDate = dateFormatter.parse(dateFormatter.format(new Date()));
            assignedDate = dateFormatter.parse(dateFormatter.format(assignmentDTO.getAssignedDate()));
        } catch (ParseException e) {
            throw new RuntimeException("Parse date error");
        }

        if(assignedDate.before(todayDate)) {
            throw new ConflictException("The assigned date is current or future!");
        }

        UserDetailEntity assignTo = assignment.getAssignTo();
        UserDetailEntity assignBy;

        // case: new asset
//        if (!assignmentDTO.getAssetCode().equalsIgnoreCase(assignment.getAssetEntity().getAssetCode())) {
//            AssetEntity asset = assetRepository.findById(assignmentDTO.getAssetCode())
//                    .orElseThrow(() -> new ResourceNotFoundException("Asset not found!"));
//            if (asset.getState() != AssetState.AVAILABLE) {
//                throw new ConflictException("Asset must available state!");
//            }
//            assignment.getAssetEntity().setState(AssetState.AVAILABLE); // return state for old asset
//            asset.setState(AssetState.ASSIGNED);
//            assignment.setAssetEntity(asset);
//        }

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

        assignment.setNote(assignmentDTO.getNote());
        if (assignmentDTO.getAssignedDate() != null)
            assignment.setAssignedDate(assignmentDTO.getAssignedDate());

//        SimpleMailMessage msg = new SimpleMailMessage();
//        msg.setTo(assignTo.getEmail());
//        msg.setSubject("Your assignment has been update by Administrator");
//        msg.setText("Your administrator has been update your assignment: "+
//                "\nAssignment code: "+assignment.getId()+
//                "\nAsset code: "+assignment.getAssetEntity().getAssetCode()+
//                "\nAsset name: "+ assignment.getAssetEntity().getAssetName()+
//                "\nDate: "+dateFormatter.format(assignment.getAssignedDate())+
//                "\nPlease check your assignment by your account\nKind Regards,\nAdministrator");
//        javaMailSender.send(msg);

        return AssignmentDTO.toDTO(assignmentRepository.save(assignment));
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
//        if(assignmentDTO.getState() == AssignmentState.CANCELED_ASSIGN) { // set asset's state is available when user decline assignment
//            asset.setState(AssetState.AVAILABLE);
//            assignment.setNote(assignmentDTO.getNote());
//        }
//
//        assignment.setState(assignmentDTO.getState());
//        assignment.setAssetEntity(asset);
        return AssignmentDTO.toDTO(assignmentRepository.save(assignment));
    }
}
