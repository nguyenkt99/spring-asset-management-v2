package com.nashtech.assetmanagement.service.impl;

import com.nashtech.assetmanagement.constants.*;
import com.nashtech.assetmanagement.dto.AssignmentDTO;
import com.nashtech.assetmanagement.dto.AssignmentDetailDTO;
import com.nashtech.assetmanagement.dto.NotificationDTO;
import com.nashtech.assetmanagement.entity.*;
import com.nashtech.assetmanagement.exception.BadRequestException;
import com.nashtech.assetmanagement.exception.ConflictException;
import com.nashtech.assetmanagement.exception.ResourceNotFoundException;
import com.nashtech.assetmanagement.repository.AssetRepository;
import com.nashtech.assetmanagement.repository.AssignmentRepository;
import com.nashtech.assetmanagement.repository.RequestAssignRepository;
import com.nashtech.assetmanagement.repository.UserRepository;
import com.nashtech.assetmanagement.service.AssignmentService;
import com.nashtech.assetmanagement.service.FirebaseService;
import com.nashtech.assetmanagement.service.UserService;

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
    FirebaseService firebaseService;

    @Autowired
    JavaMailSender javaMailSender;

    @Override
    public List<AssignmentDTO> getAllByAdminLocation(String username) {
        LocationEntity location = userService.findByUserName(username).getUserDetail().getLocation();
        List<AssignmentDTO> assignmentDTOs = assignmentRepository.findAllByAdminLocation(location.getId())
                .stream().map(AssignmentDTO::new).collect(Collectors.toList());
        return assignmentDTOs;
    }

    @Override
    public List<AssignmentDTO> getAssignmentsByUser(String username) {
        UsersEntity user = userRepository.findByUserName(username).get();
        List<AssignmentDTO> assignmentDTOs = assignmentRepository.findByAssignTo_StaffCodeAndAssignedDateIsLessThanEqualOrderByIdAsc(user.getStaffCode(), new Date())
                .stream().map(AssignmentDTO::new).collect(Collectors.toList());
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

        // check assigned date and intended date must be today or future
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        Date todayDate = null;
        Date assignedDate = null;
        Date intendedDate = null;
        try {
            todayDate = dateFormatter.parse(dateFormatter.format(new Date()));
            assignedDate = dateFormatter.parse(dateFormatter.format(assignmentDTO.getAssignedDate()));
            intendedDate = dateFormatter.parse(dateFormatter.format(assignmentDTO.getIntendedReturnDate()));
        } catch (ParseException e) {
            throw new RuntimeException("Parse date error");
        }

        if (assignedDate.before(todayDate)) {
            throw new ConflictException("The assigned date is today or future!");
        }

        if (intendedDate.before(todayDate)) {
            throw new ConflictException("The intended return date is today or future!");
        }

        if (assignTo.getLocation() != assignBy.getLocation()) {
            throw new ConflictException("The location of assignTo difference from admin!");
        }

        List<AssignmentDetailDTO> assignmentDetailDTOs = assignmentDTO.getAssignmentDetails();
        List<AssignmentDetailEntity> assignmentDetails = new ArrayList<>();
        // check asset's state
        for (AssignmentDetailDTO assignmentDetailDTO : assignmentDetailDTOs) {
            AssetEntity asset = assetRepository.findById(assignmentDetailDTO.getAssetCode())
                    .orElseThrow(() -> new ResourceNotFoundException("Asset not found!"));
            for (AssignmentDetailEntity assignmentDetail : asset.getAssignmentDetails()) {
                if (assignmentDTO.getAssignedDate().before(assignmentDTO.getIntendedReturnDate())) {
                    if (assignmentDetail.getState() != AssignmentState.DECLINED && assignmentDetail.getState() != AssignmentState.COMPLETED) {
                        if (!(assignmentDTO.getIntendedReturnDate().before(assignmentDetail.getAssignment().getAssignedDate())
                                || assignmentDTO.getAssignedDate().after(assignmentDetail.getAssignment().getIntendedReturnDate()))) {
                            throw new ConflictException("Asset not available in this time!");
                        }
                    }
                } else {
                    throw new ConflictException("AssignedDate and IntendedReturnDate are invalid!");
                }
            }
        }

        for (AssignmentDetailDTO assignmentDetailDTO : assignmentDetailDTOs) {
            AssetEntity asset = assetRepository.getById(assignmentDetailDTO.getAssetCode());
            AssignmentDetailEntity assignmentDetail = new AssignmentDetailEntity();
            asset.setState(AssetState.ASSIGNED);
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

        AssignmentEntity savedAssignment = assignmentRepository.save(assignment);

        String title = "";
        String usernameReceiver = null;
        title = "Admin created the assignment with id = " + savedAssignment.getId() + " includes: ";
        for(AssignmentDetailEntity a : savedAssignment.getAssignmentDetails()) {
            title += a.getAsset().getAssetCode() + ", ";
        }
        usernameReceiver = savedAssignment.getAssignTo().getUser().getUserName();
        NotificationDTO notificationDTO = new NotificationDTO(savedAssignment.getId(), NotificationType.ASSIGNMENT, usernameReceiver, title, false);
        try {
            firebaseService.saveNotification(notificationDTO);
        } catch (Exception e) {
            System.out.println("Send Notification Error!!");
        } finally {
            return new AssignmentDTO(savedAssignment);
        }
    }

    public AssignmentDTO updateAssignment(AssignmentDTO assignmentDTO) {
        AssignmentEntity assignment = assignmentRepository.findById(assignmentDTO.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found!"));
        if (assignment.getState() != AssignmentState.WAITING_FOR_ACCEPTANCE && assignment.getState() != AssignmentState.ACCEPTED) {
            throw new ConflictException("Assignment is editable while in waiting for acceptance or accepted state!");
        }

        List<AssignmentDetailDTO> assignmentDetailDTOs = assignmentDTO.getAssignmentDetails();
        List<AssignmentDetailEntity> assignmentDetails = assignment.getAssignmentDetails();
//        // check asset's state
//        for (AssignmentDetailDTO assignmentDetailDTO : assignmentDetailDTOs) {
//            List<AssignmentDetailEntity> assetAssignmentDetails = assetRepository.findById(assignmentDetailDTO.getAssetCode())
//                    .orElseThrow(() -> new ResourceNotFoundException("Asset not found!")).getAssignmentDetails()
//                    .stream().filter(a -> a.getAssignment().getId() != assignmentDTO.getId())
//                    .collect(Collectors.toList());
//            for (AssignmentDetailEntity x : assetAssignmentDetails) {
//                if (assignmentDTO.getAssignedDate().before(assignmentDTO.getIntendedReturnDate())) {
//                    if (!(assignmentDTO.getIntendedReturnDate().before(x.getAssignment().getAssignedDate())
//                            || assignmentDTO.getAssignedDate().after(x.getAssignment().getIntendedReturnDate()))) {
//                        throw new ConflictException("Asset not available in this time!");
//                    }
//                } else {
//                    throw new ConflictException("Date is invalid!");
//                }
//            }
//        }

        UserDetailEntity assignTo = assignment.getAssignTo();
        UserDetailEntity assignBy;
        if (assignment.getAssignmentDetails().stream()
                .allMatch(a -> a.getState() == AssignmentState.WAITING_FOR_ACCEPTANCE)) {
            // check assigned date and intended date must be today or future
            SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
            Date todayDate = null;
            Date assignedDate = null;
            Date intendedDate = null;
            try {
                todayDate = dateFormatter.parse(dateFormatter.format(new Date()));
                assignedDate = dateFormatter.parse(dateFormatter.format(assignmentDTO.getAssignedDate()));
                intendedDate = dateFormatter.parse(dateFormatter.format(assignmentDTO.getIntendedReturnDate()));
            } catch (ParseException e) {
                throw new RuntimeException("Parse date error");
            }

            if (assignedDate.before(todayDate)) {
                throw new ConflictException("The assigned date is today or future!");
            }

            if (intendedDate.before(todayDate)) {
                throw new ConflictException("The intended return date is today or future!");
            }

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
        }

        for (int i = 0; i < assignmentDetails.size(); i++) {
            boolean isExists = false;
            if (assignmentDetails.get(i).getState() == AssignmentState.WAITING_FOR_ACCEPTANCE) {
                for (int j = 0; j < assignmentDetailDTOs.size(); j++) {
                    if (assignmentDetailDTOs.get(j).getAssetCode().equalsIgnoreCase(assignmentDetails.get(i).getAsset().getAssetCode())) {
                        isExists = true;
                        assignmentDetailDTOs.remove(j);
                        j--;
                    }
                }

                if (isExists == false) {
                    AssetEntity asset = assignmentDetails.get(i).getAsset();
//                    if (asset.getAssignmentDetails().stream().allMatch(a -> a.getId() != assignmentDetails.get(index).getId() && (a.getState() == AssignmentState.COMPLETED
//                            || a.getState() == AssignmentState.DECLINED))) {
//                        asset.setState(AssetState.AVAILABLE);
//                    }

                    // Check all assignment detail to set asset state is available!!!
                    updateAvailableAssetState(assignmentDetails.get(i));

                    assignmentDetails.remove(i);
                    i--;
                }
            }
        }

        for (int i = 0; i < assignmentDetailDTOs.size(); i++) {
            AssetEntity asset = assetRepository.findById(assignmentDetailDTOs.get(i).getAssetCode())
                    .orElseThrow(() -> new ResourceNotFoundException("Asset not found!"));
            AssignmentDetailEntity newAssignmentDetail = new AssignmentDetailEntity();
            asset.setState(AssetState.ASSIGNED);
            newAssignmentDetail.setAsset(asset);
            newAssignmentDetail.setAssignment(assignment);
            newAssignmentDetail.setState(AssignmentState.WAITING_FOR_ACCEPTANCE);
            assignmentDetails.add(newAssignmentDetail);
        }

        // check all assignment detail if update assign date or return date!!!
        for (AssignmentDetailEntity assignmentDetail : assignmentDetails) {
            List<AssignmentDetailEntity> assetAssignmentDetails = assetRepository
                    .getById(assignmentDetail.getAsset().getAssetCode()).getAssignmentDetails()
                    .stream().filter(a -> a.getAssignment().getId() != assignmentDTO.getId())
                    .collect(Collectors.toList());
            for (AssignmentDetailEntity a : assetAssignmentDetails) {
                if (assignmentDTO.getAssignedDate().before(assignmentDTO.getIntendedReturnDate())) {
                    if (!(assignmentDTO.getIntendedReturnDate().before(a.getAssignment().getAssignedDate())
                            || assignmentDTO.getAssignedDate().after(a.getAssignment().getIntendedReturnDate()))) {
                        throw new ConflictException("Asset not available in this time!");
                    }
                } else {
                    throw new ConflictException("Date invalid!");
                }
            }
        }

        assignment.setNote(assignmentDTO.getNote());
        assignment.setIntendedReturnDate(assignmentDTO.getIntendedReturnDate());
        assignment.setUpdatedDate(new Date());
        assignment.setState(AssignmentState.WAITING_FOR_ACCEPTANCE);

//        SimpleMailMessage msg = new SimpleMailMessage();
//        msg.setTo(assignTo.getEmail());
//        msg.setSubject("New assignment assigned to you");
//        msg.setText("Your administrator has assigned you a new assignment: \nAsset " +
//                "code: "+assignment.getAssetEntity().getAssetCode()+
//                "\nAsset name: "+ assignment.getAssetEntity().getAssetName()+
//                "\nDate: "+dateFormatter.format(assignment.getAssignedDate())+
//                "\nPlease check your assignment by your account\nKind Regards,\nAdministrator");
//        javaMailSender.send(msg);
        AssignmentEntity savedAssignment = assignmentRepository.save(assignment);

        String title = "";
        String usernameReceiver = null;
        title = "Admin updated the assignment with id = " + savedAssignment.getId() + " includes: ";
        for(AssignmentDetailEntity a : savedAssignment.getAssignmentDetails()) {
            title += a.getAsset().getAssetCode() + ", ";
        }
        usernameReceiver = savedAssignment.getAssignTo().getUser().getUserName();
        NotificationDTO notificationDTO = new NotificationDTO(savedAssignment.getId(), NotificationType.ASSIGNMENT, usernameReceiver, title, false);
        try {
            firebaseService.saveNotification(notificationDTO);
        } catch (Exception e) {
            System.out.println("Send Notification Error!!");
        } finally {
            return new AssignmentDTO(savedAssignment);
        }
    }

    @Override
    public boolean deleteAssignment(Long assignmentId) {
        AssignmentEntity assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found!"));

        if (assignment.getState() != AssignmentState.WAITING_FOR_ACCEPTANCE) {
            throw new BadRequestException("Assignment delete when state is waiting for acceptance!");
        }

        List<AssignmentDetailEntity> assignmentDetails = assignment.getAssignmentDetails();
        for (AssignmentDetailEntity assignmentDetail : assignmentDetails) {
            // Check all assignment detail to set asset state is available!!!
            updateAvailableAssetState(assignmentDetail);
        }

        assignmentRepository.deleteById(assignmentId);
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

        assignment.setNote(assignmentDTO.getNote());
        for (AssignmentDetailEntity a : assignment.getAssignmentDetails()) {
            if (a.getState() == AssignmentState.WAITING_FOR_ACCEPTANCE) {
                a.setState(assignmentDTO.getState());

                if(assignmentDTO.getState() == AssignmentState.DECLINED) {
                    // Check all assignment detail to set asset state is available!!!
                    updateAvailableAssetState(a);
                }
            }
        }
        assignment.setState(assignmentDTO.getState());
        AssignmentEntity savedAssignment = assignmentRepository.save(assignment);

        String title = "";
        String usernameReceiver = null;
        title = "Admin updated the assignment with id = " + savedAssignment.getId() + " includes: ";
        for(AssignmentDetailEntity a : savedAssignment.getAssignmentDetails()) {
            title += a.getAsset().getAssetCode() + ", ";
        }
        usernameReceiver = savedAssignment.getAssignTo().getUser().getUserName();
        NotificationDTO notificationDTO = new NotificationDTO(savedAssignment.getId(), NotificationType.ASSIGNMENT, usernameReceiver, title, false);
        try {
            firebaseService.saveNotification(notificationDTO);
        } catch (Exception e) {
            System.out.println("Send Notification Error!!");
        } finally {
            return new AssignmentDTO(savedAssignment);
        }
    }

    public void updateAvailableAssetState(AssignmentDetailEntity assignmentDetail) {
        List<AssignmentDetailEntity> allAssetAssignments = assignmentDetail.getAsset().getAssignmentDetails();
        for (int i = 0; i < allAssetAssignments.size(); i++) {
            if (allAssetAssignments.get(i).getAssignment().getId() != assignmentDetail.getAssignment().getId()
                    && (allAssetAssignments.get(i).getState() == AssignmentState.WAITING_FOR_ACCEPTANCE ||
                    allAssetAssignments.get(i).getState() == AssignmentState.ACCEPTED ||
                    allAssetAssignments.get(i).getState() == AssignmentState.WAITING_FOR_RETURNING
            )) break;
            if (i == allAssetAssignments.size() - 1)
                allAssetAssignments.get(i).getAsset().setState(AssetState.AVAILABLE);
        }
    }
}
