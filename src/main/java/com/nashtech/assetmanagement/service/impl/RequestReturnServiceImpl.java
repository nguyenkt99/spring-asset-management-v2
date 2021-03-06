package com.nashtech.assetmanagement.service.impl;

import com.nashtech.assetmanagement.constants.AssignmentState;
import com.nashtech.assetmanagement.constants.NotificationType;
import com.nashtech.assetmanagement.constants.RequestReturnState;
import com.nashtech.assetmanagement.constants.RoleName;
import com.nashtech.assetmanagement.dto.AssignmentDetailDTO;
import com.nashtech.assetmanagement.dto.NotificationDTO;
import com.nashtech.assetmanagement.dto.RequestReturnDTO;
import com.nashtech.assetmanagement.entity.*;
import com.nashtech.assetmanagement.exception.BadRequestException;
import com.nashtech.assetmanagement.exception.ConflictException;
import com.nashtech.assetmanagement.exception.ResourceNotFoundException;
import com.nashtech.assetmanagement.repository.*;
import com.nashtech.assetmanagement.service.FirebaseService;
import com.nashtech.assetmanagement.service.RequestReturnService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RequestReturnServiceImpl implements RequestReturnService {
    @Autowired
    RequestReturnRepository requestReturnRepository;

    @Autowired
    AssignmentRepository assignmentRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    AssignmentServiceImpl assignmentService;

    @Autowired
    FirebaseService firebaseService;

    @Autowired
    JavaMailSender javaMailSender;

    @Override
    public RequestReturnDTO create(RequestReturnDTO requestReturnDTO) {
        String strReturnedAssets = "";
        List<AssignmentDetailEntity> assignmentDetailsForReturn = new ArrayList<>();
        RequestReturnEntity requestReturn = requestReturnDTO.toEntity();
        AssignmentEntity assignment = assignmentRepository.findById(requestReturnDTO.getAssignmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found!"));
        if (assignment.getState() != AssignmentState.ACCEPTED) {
            throw new ConflictException("Assignment must have accepted state!");
        }
        List<AssignmentDetailEntity> assignmentDetails = assignment.getAssignmentDetails();

        UserDetailEntity requestBy = userRepository.getByUserName(requestReturnDTO.getRequestBy()).getUserDetail();
        if (!requestBy.getUser().getRole().getName().equals(RoleName.ROLE_ADMIN)) { // requestedBy is not admin
            if (!requestBy.equals(assignment.getAssignTo())) { // requestedBy is also not assignedTo
                throw new ConflictException("RequestedBy must be admin or assignee!");
            }
        }

        for (AssignmentDetailDTO assignmentDetailDTO : requestReturnDTO.getAssignmentDetails()) {
            boolean assetExists = false;
            for (AssignmentDetailEntity assignmentDetail : assignmentDetails) {
                if (assignmentDetailDTO.getAssetCode().equals(assignmentDetail.getAsset().getAssetCode())) {
                    assetExists = true;
                    assignmentDetail.setState(AssignmentState.WAITING_FOR_RETURNING);
                    assignmentDetail.setRequestReturn(requestReturn);
                    assignmentDetailsForReturn.add(assignmentDetail);
                }
            }
            if(!assetExists) {
                throw new ResourceNotFoundException(assignmentDetailDTO.getAssetCode() + " not exists in assignment details!");
            }
        }

        requestReturn.setState(RequestReturnState.WAITING_FOR_RETURNING);
        requestReturn.setRequestedDate(LocalDateTime.now());
        requestReturn.setRequestBy(requestBy);
        requestReturn.setAssignment(assignment);
        requestReturn.setAssignmentDetails(assignmentDetailsForReturn);

        if (!assignmentDetails.stream().anyMatch(x -> x.getRequestReturn() == null))
            assignment.setState(AssignmentState.WAITING_FOR_RETURNING);
        RequestReturnEntity savedReq = requestReturnRepository.save(requestReturn);

        String title = "";
        String usernameReceiver = null;
        for(AssignmentDetailEntity a : assignmentDetailsForReturn)
            strReturnedAssets += a.getAsset().getAssetName() + " (" + a.getAsset().getAssetCode() + "), ";
        strReturnedAssets = strReturnedAssets.substring(0, strReturnedAssets.length() - 2);

        if (savedReq.getRequestBy().getUser().getRole().getName() == RoleName.ROLE_ADMIN) {
            usernameReceiver = savedReq.getAssignment().getAssignTo().getUser().getUserName();
            title = "Admin created the request for retuning from assignment with id=" + assignment.getId() +  "includes: " + strReturnedAssets;
        } else {
            usernameReceiver = "admin";
            title = savedReq.getRequestBy().getFirstName() + " " + savedReq.getRequestBy().getLastName() +
                    " created the request for retuning includes: " + strReturnedAssets;
        }
        NotificationDTO notificationDTO = new NotificationDTO(savedReq.getId(), NotificationType.REQUEST_RETURN, usernameReceiver, title, false, new Date());

        try {
            firebaseService.saveNotification(notificationDTO);
            if(!requestBy.getUser().getUserName().equals(assignment.getAssignTo().getUser().getUserName())) {
                SimpleMailMessage msg = new SimpleMailMessage();
                msg.setTo(assignment.getAssignTo().getEmail());
                msg.setSubject("Returning Asset");
                msg.setText("Admin need you to return assets to the company at " +
                        assignment.getIntendedReturnDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) +
                        "\nAsset list: " + strReturnedAssets +
                        "\nPlease check the request by your account" +
                        "\nKind Regards, " +
                        "\nAdministrator");
                javaMailSender.send(msg);
            }
        } catch (Exception e) {
            System.out.println("Send Notification Error!!");
        } finally {
            return new RequestReturnDTO(savedReq);
        }

    }

    @Override
    public List<RequestReturnDTO> getAllByAdminLocation(String adminUsername) {
        LocationEntity location = userRepository.findByUserName(adminUsername).get().getUserDetail().getDepartment().getLocation();
        List<RequestReturnDTO> requestReturnDTOS = requestReturnRepository.findAll().stream()
                .filter(request -> (request.getRequestBy().getDepartment().getLocation().equals(location)))
                .sorted((o1, o2) -> (int) (o1.getId() - o2.getId()))
                .map(RequestReturnDTO::new).collect(Collectors.toList());
        return requestReturnDTOS;
    }

    @Override
    public void delete(Long id, String username) {
        UserEntity requestBy = userRepository.findByUserName(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found!"));
        RequestReturnEntity requestReturn = requestReturnRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(REQUEST_NOT_FOUND_ERROR));
        if (!requestReturn.getState().equals(RequestReturnState.WAITING_FOR_RETURNING)) {
            throw new BadRequestException(REQUEST_STATE_INVALID_ERROR);
        }

//        if (!requestBy.getUserName().equals(requestReturn.getRequestBy().getUser().getUserName())) {
//            throw new BadRequestException("The request wasn't created by user so cannot delete!");
//        }

        AssignmentEntity assignment = requestReturn.getAssignment();
        List<AssignmentDetailEntity> assignmentDetails = requestReturn.getAssignmentDetails();
        for (AssignmentDetailEntity assignmentDetail : assignmentDetails) {
            if (assignmentDetail.getRequestReturn().getId() == requestReturn.getId()) {
                assignmentDetail.setState(AssignmentState.ACCEPTED);
                assignmentDetail.setRequestReturn(null);
            }
        }

        if (assignmentDetails.stream().anyMatch(x -> x.getRequestReturn() == null))
            assignment.setState(AssignmentState.ACCEPTED);
        requestReturnRepository.save(requestReturn);
        requestReturn.setAssignment(null);
        requestReturn.setAssignmentDetails(null);
        requestReturnRepository.deleteById(id);
    }

    @Override
    public RequestReturnDTO accept(Long id, String staffCode) {
        RequestReturnEntity requestReturn = requestReturnRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(REQUEST_NOT_FOUND_ERROR));
        if (!requestReturn.getState().equals(RequestReturnState.WAITING_FOR_RETURNING))
            throw new BadRequestException(REQUEST_STATE_INVALID_ERROR);

        AssignmentEntity assignment = requestReturn.getAssignment();
        for (AssignmentDetailEntity assignmentDetail : assignment.getAssignmentDetails()) {
            if ((assignmentDetail.getState() == AssignmentState.WAITING_FOR_RETURNING)
                && assignmentDetail.getRequestReturn().getId() == id) {
                assignmentDetail.setState(AssignmentState.COMPLETED);
                assignmentService.updateAvailableAssetState(assignmentDetail);
            }
        }

        if (assignment.getAssignmentDetails().stream().allMatch(x -> x.getState() == AssignmentState.COMPLETED))
            assignment.setState(AssignmentState.COMPLETED);
        requestReturn.setAssignment(assignment);
        requestReturn.setReturnedDate(LocalDateTime.now());
        requestReturn.setState(RequestReturnState.COMPLETED);
        requestReturn.setAcceptBy(userRepository.getByStaffCode(staffCode).getUserDetail());
        RequestReturnEntity savedReq = requestReturnRepository.save(requestReturn);


        if(requestReturn.getRequestBy().getUser().getRole().equals(RoleName.ROLE_STAFF)) {
            SimpleMailMessage msg = new SimpleMailMessage();
            String usernameReceiver = null;
            String strReturnedAssets = "";
            String title = "Admin has accepted the request for retuning from assignment with id=" + assignment.getId() + " includes: ";
            for(AssignmentDetailEntity a : savedReq.getAssignmentDetails())
                strReturnedAssets += a.getAsset().getAssetName() + " (" + a.getAsset().getAssetCode() + "), ";
            strReturnedAssets = strReturnedAssets.substring(0, title.length() - 2);
            usernameReceiver = savedReq.getRequestBy().getUser().getUserName();
            NotificationDTO notificationDTO = new NotificationDTO(savedReq.getId(), NotificationType.REQUEST_RETURN, usernameReceiver, title + strReturnedAssets, false, new Date());

            try {
                firebaseService.saveNotification(notificationDTO);
                msg.setTo(assignment.getAssignTo().getEmail());
                msg.setSubject("Admin accept the request for returning");
                msg.setText("Admin has accepted the request for returning: " +
                        "\nAssignmentID: " + assignment.getId() +
                        "\nRequestID: " + requestReturn.getId() +
                        "\nAsset list: " + strReturnedAssets +
                        "\nPlease check your request by your account" +
                        "\nKind Regards," +
                        "\nAdministrator");
                javaMailSender.send(msg);
            } catch (Exception e) {
                System.out.println("Send Notification Error!!");
            } finally {
                return new RequestReturnDTO(savedReq);
            }
        }
        return new RequestReturnDTO(savedReq);
    }

    private final String REQUEST_NOT_FOUND_ERROR = "Request not found.";
    private final String REQUEST_STATE_INVALID_ERROR = "Request state must be 'Waiting for returning'.";
}
