package com.nashtech.assetmanagement.service.impl;

import com.nashtech.assetmanagement.constants.*;
import com.nashtech.assetmanagement.dto.AssetDTO;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
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
        LocationEntity location = userService.findByUserName(username).getUserDetail().getDepartment().getLocation();
        List<AssignmentDTO> assignmentDTOs = assignmentRepository.findAllByAdminLocation(location.getId())
                .stream().map(AssignmentDTO::new).collect(Collectors.toList());
        return assignmentDTOs;
    }

    @Override
    public List<AssignmentDTO> getAssignmentsByUser(String username) {
        UserEntity user = userRepository.findByUserName(username).get();
        List<AssignmentDTO> assignmentDTOs = assignmentRepository.findByAssignTo_StaffCodeAndAssignedDateIsLessThanEqualOrderByIdAsc(user.getStaffCode(), LocalDate.now())
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
        // check assigned date < returned date and both must be today or future
        if(!isValidDate(assignmentDTO.getAssignedDate(), assignmentDTO.getIntendedReturnDate()))
            throw new ConflictException("Date must be today or future!");

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

        if (assignTo.getDepartment().getLocation() != assignBy.getDepartment().getLocation())
            throw new ConflictException("The location of assignTo difference from admin!");

        List<AssignmentDetailDTO> assignmentDetailDTOs = assignmentDTO.getAssignmentDetails();
        List<AssignmentDetailEntity> assignmentDetails = new ArrayList<>();
        // check asset's state
        for (AssignmentDetailDTO assignmentDetailDTO : assignmentDetailDTOs) {
            AssetEntity asset = assetRepository.findById(assignmentDetailDTO.getAssetCode())
                    .orElseThrow(() -> new ResourceNotFoundException("Asset not found!"));

            if(asset.getState() != AssetState.AVAILABLE && asset.getState() != AssetState.ASSIGNED)
                throw new BadRequestException("Please check state of asset " + asset.getAssetCode());

            List<AssignmentDetailEntity> validAssignmentDetails = asset.getAssignmentDetails().stream().filter(ad ->
                    ad.getState() != AssignmentState.DECLINED && ad.getState() != AssignmentState.COMPLETED)
                    .collect(Collectors.toList());

            for (AssignmentDetailEntity ad : validAssignmentDetails) {
                    AssignmentEntity asm = ad.getAssignment();
                    if(isBusyDate(assignmentDTO.getAssignedDate(), assignmentDTO.getIntendedReturnDate(),
                            asm.getAssignedDate(), asm.getIntendedReturnDate()))
                        throw new ConflictException("Asset not available in this time!");
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

        assignment.setRequestAssign(requestAssign); // requestAssign can be null
        assignment.setAssignTo(assignTo);
        assignment.setAssignBy(assignBy);
        assignment.setCreatedDate(LocalDateTime.now());
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
        NotificationDTO notificationDTO = new NotificationDTO(savedAssignment.getId(), NotificationType.ASSIGNMENT, usernameReceiver, title, false, new Date());
        try {
            firebaseService.saveNotification(notificationDTO);
        } catch (Exception e) {
            System.out.println("Send Notification Error!!");
        } finally {
            return new AssignmentDTO(savedAssignment);
        }
    }

    public AssignmentDTO updateAssignment(AssignmentDTO assignmentDTO) {
        // check assigned date < returned date and both must be today or future
        if(!isValidDate(assignmentDTO.getAssignedDate(), assignmentDTO.getIntendedReturnDate()))
            throw new ConflictException("Date must be today or future!");

        AssignmentEntity assignment = assignmentRepository.findById(assignmentDTO.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found!"));
        if (assignment.getState() != AssignmentState.WAITING_FOR_ACCEPTANCE && assignment.getState() != AssignmentState.ACCEPTED) {
            throw new ConflictException("Assignment is editable while in waiting for acceptance or accepted state!");
        }

        List<AssignmentDetailDTO> assignmentDetailDTOs = assignmentDTO.getAssignmentDetails();
        List<AssignmentDetailEntity> assignmentDetails = assignment.getAssignmentDetails();

        UserDetailEntity assignTo = assignment.getAssignTo();
        UserDetailEntity assignBy;

        // assignment is waiting for acceptance state, then allow update all information
        if (assignment.getAssignmentDetails().stream()
                .allMatch(a -> a.getState() == AssignmentState.WAITING_FOR_ACCEPTANCE)) {

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
                if (assignTo.getDepartment().getLocation() != assignBy.getDepartment().getLocation()) {
                    throw new ConflictException("The location of assignTo difference from admin!");
                }
            }

            assignment.setAssignedDate(assignmentDTO.getAssignedDate());
            assignment.setIntendedReturnDate(assignmentDTO.getIntendedReturnDate());
        }

        for (int i = 0; i < assignmentDetails.size(); i++) {
            boolean isExists = false;
            if (assignmentDetails.get(i).getState() == AssignmentState.WAITING_FOR_ACCEPTANCE
                    || assignmentDetails.get(i).getState() == AssignmentState.ACCEPTED) {
                for (int j = 0; j < assignmentDetailDTOs.size(); j++) {
                    if (assignmentDetailDTOs.get(j).getAssetCode().equals(assignmentDetails.get(i).getAsset().getAssetCode())) {
                        isExists = true;
                        assignmentDetailDTOs.remove(j);
                        j--;
                    }
                }

                // if new assignment not include asset then set state and remove it
                if (isExists == false && assignmentDetails.get(i).getState() == AssignmentState.WAITING_FOR_ACCEPTANCE) {
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

            if(asset.getState() != AssetState.AVAILABLE && asset.getState() != AssetState.ASSIGNED)
                throw new BadRequestException("Please check state of asset " + asset.getAssetCode());

            AssignmentDetailEntity newAssignmentDetail = new AssignmentDetailEntity();
            asset.setState(AssetState.ASSIGNED);
            newAssignmentDetail.setAsset(asset);
            newAssignmentDetail.setAssignment(assignment);
            newAssignmentDetail.setState(AssignmentState.WAITING_FOR_ACCEPTANCE);
            assignmentDetails.add(newAssignmentDetail);
        }

        // check all assignment detail if update assign date or return date!!!
        for (AssignmentDetailEntity assignmentDetail : assignmentDetails) {
//            List<AssignmentDetailEntity> assetAssignmentDetails = assetRepository // can get asset from assignmentDetail directly
//                    .getById(assignmentDetail.getAsset().getAssetCode()).getAssignmentDetails()
//                    .stream().filter(a -> a.getAssignment().getId() != assignmentDTO.getId())
//                    .collect(Collectors.toList());
//            for (AssignmentDetailEntity a : assetAssignmentDetails) {
//                AssignmentEntity asm = a.getAssignment();
//                if(isBusyDate(assignmentDTO.getAssignedDate(), assignmentDTO.getIntendedReturnDate(),
//                        asm.getAssignedDate(), asm.getIntendedReturnDate()))
//                    throw new ConflictException("Asset not available in this time!");
//            }

            List<AssignmentDetailEntity> validAssignmentDetails = assignmentDetail.getAsset().getAssignmentDetails()
                    .stream().filter(ad -> ad.getAssignment().getId() != assignmentDTO.getId()
                            && ad.getState() != AssignmentState.DECLINED
                            && ad.getState() != AssignmentState.COMPLETED
                    ).collect(Collectors.toList());
            for (AssignmentDetailEntity ad : validAssignmentDetails) {
                AssignmentEntity asm = ad.getAssignment();
                if(isBusyDate(assignmentDTO.getAssignedDate(), assignmentDTO.getIntendedReturnDate(),
                        asm.getAssignedDate(), asm.getIntendedReturnDate()))
                    throw new ConflictException("Asset not available in this time!");
            }
        }

        assignment.setNote(assignmentDTO.getNote());
        assignment.setUpdatedDate(LocalDateTime.now());
        assignment.setState(AssignmentState.WAITING_FOR_ACCEPTANCE);
        AssignmentEntity savedAssignment = assignmentRepository.save(assignment);

//        SimpleMailMessage msg = new SimpleMailMessage();
//        msg.setTo(assignTo.getEmail());
//        msg.setSubject("New assignment assigned to you");
//        msg.setText("Your administrator has assigned you a new assignment: \nAsset " +
//                "code: "+assignment.getAssetEntity().getAssetCode()+
//                "\nAsset name: "+ assignment.getAssetEntity().getAssetName()+
//                "\nDate: "+dateFormatter.format(assignment.getAssignedDate())+
//                "\nPlease check your assignment by your account\nKind Regards,\nAdministrator");
//        javaMailSender.send(msg);

        String title = "";
        String usernameReceiver = null;
        title = "Admin updated the assignment with id = " + savedAssignment.getId() + " includes: ";
        for(AssignmentDetailEntity a : savedAssignment.getAssignmentDetails()) {
            title += a.getAsset().getAssetCode() + ", ";
        }
        usernameReceiver = savedAssignment.getAssignTo().getUser().getUserName();
        NotificationDTO notificationDTO = new NotificationDTO(savedAssignment.getId(), NotificationType.ASSIGNMENT, usernameReceiver, title, false, new Date());
        try {
            firebaseService.saveNotification(notificationDTO);
        } catch (Exception e) {
            System.out.println("Send Notification Error!!");
        } finally {
            return new AssignmentDTO(savedAssignment);
        }
    }

    @Override
    public void deleteAssignment(Long assignmentId) {
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
        NotificationDTO notificationDTO = new NotificationDTO(savedAssignment.getId(), NotificationType.ASSIGNMENT, usernameReceiver, title, false, new Date());
        try {
            firebaseService.saveNotification(notificationDTO);
        } catch (Exception e) {
            System.out.println("Send Notification Error!!");
        } finally {
            return new AssignmentDTO(savedAssignment);
        }
    }

    @Override
    public Map<String, Object> checkAssetListAvailable(AssignmentDTO assignmentDTO) { // has assignment Id
        Map<String, Object> result = new HashMap<>();
        List<Object> assetInvalidList = new ArrayList<>();
        List<AssignmentDetailDTO> assignmentDetailDTOs = assignmentDTO.getAssignmentDetails();

        if(!isValidDate(assignmentDTO.getAssignedDate(), assignmentDTO.getIntendedReturnDate()))
            throw new ConflictException("Date must be today or future!");

        boolean isValidDate = true;
        for (AssignmentDetailDTO assignmentDetailDTO : assignmentDetailDTOs) {
            AssetEntity asset = assetRepository.findById(assignmentDetailDTO.getAssetCode())
                    .orElseThrow(() -> new ResourceNotFoundException("Asset not found!"));

            List<AssignmentDetailEntity> validAssignmentDetails = asset.getAssignmentDetails().stream().filter(ad ->
                            ad.getState() != AssignmentState.DECLINED  && ad.getState() != AssignmentState.COMPLETED
                            && ad.getAssignment().getId() != assignmentDTO.getId())
                    .collect(Collectors.toList());
            for (AssignmentDetailEntity ad : validAssignmentDetails) {
                AssignmentEntity asm = ad.getAssignment();
                if(isBusyDate(assignmentDTO.getAssignedDate(), assignmentDTO.getIntendedReturnDate(),
                        asm.getAssignedDate(), asm.getIntendedReturnDate())) {
                    isValidDate = false;
                    assetInvalidList.add(new AssetDTO(ad.getAsset()));
                }
            }
        }

        if(!isValidDate) {
            result.put("statusCode", 409);
            result.put("error", "Conflict");
            result.put("assetCodeList", assetInvalidList);
            result.put("message", "Asset not available in this time!");
        } else {
            result.put("statusCode", 200);
            result.put("message", "Asset available in this time!");
        }
        return  result;
    }

    public void updateAvailableAssetState(AssignmentDetailEntity assignmentDetail) {
        List<AssignmentDetailEntity> allAssetAssignments = assignmentDetail.getAsset().getAssignmentDetails();
        int i;
        for (i = 0; i < allAssetAssignments.size(); i++) {
            AssignmentDetailEntity ad = allAssetAssignments.get(i);
            if (ad.getAssignment().getId() != assignmentDetail.getAssignment().getId()
                    && (ad.getState() == AssignmentState.WAITING_FOR_ACCEPTANCE
                        || ad.getState() == AssignmentState.ACCEPTED
                        || ad.getState() == AssignmentState.WAITING_FOR_RETURNING))
                break;
        }
        if (i == allAssetAssignments.size())
            assignmentDetail.getAsset().setState(AssetState.AVAILABLE);
    }

    private boolean isValidDate(LocalDate assignedDate, LocalDate returnedDate) {
        // check assigned date and intended date must be today or future
        LocalDate now = LocalDate.now();
        if (assignedDate.isBefore(now) || returnedDate.isBefore(now))
            return false;
        return true;
    }

    private boolean isBusyDate(LocalDate inputAssignedDate, LocalDate inputReturnedDate, LocalDate asmAssignedDate, LocalDate asmReturnedDate) {
        if (!(inputReturnedDate.isBefore(asmAssignedDate) || inputAssignedDate.isAfter(asmReturnedDate)))
            return true;
        return false;
    }
}
