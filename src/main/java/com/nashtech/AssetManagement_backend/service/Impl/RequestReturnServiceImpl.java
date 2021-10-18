package com.nashtech.AssetManagement_backend.service.Impl;

import com.nashtech.AssetManagement_backend.dto.AssignmentDetailDTO;
import com.nashtech.AssetManagement_backend.dto.RequestReturnDTO;
import com.nashtech.AssetManagement_backend.entity.*;
import com.nashtech.AssetManagement_backend.exception.BadRequestException;
import com.nashtech.AssetManagement_backend.exception.ConflictException;
import com.nashtech.AssetManagement_backend.exception.ResourceNotFoundException;
import com.nashtech.AssetManagement_backend.repository.*;
import com.nashtech.AssetManagement_backend.service.RequestReturnService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

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
    JavaMailSender javaMailSender;

    @Override
    public RequestReturnDTO create(RequestReturnDTO requestReturnDTO) {
        List<AssignmentDetailEntity> assignmentDetailsForReturn = new ArrayList<>();
        RequestReturnEntity requestReturn = requestReturnDTO.toEntity();
        AssignmentEntity assignment = assignmentRepository.findById(requestReturnDTO.getAssignmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found!"));
        if (assignment.getState() != AssignmentState.ACCEPTED) {
            throw new ConflictException("Assignment must have accepted state!");
        }
        List<AssignmentDetailEntity> assignmentDetails = assignment.getAssignmentDetails();

        UserDetailEntity requestBy = userRepository.getByUserName(requestReturnDTO.getRequestBy()).getUserDetail();
        if(!requestBy.getUser().getRole().getName().equals(RoleName.ROLE_ADMIN)) { // requestedBy is not admin
            if(!requestBy.equals(assignment.getAssignTo())) { // requestedBy is also not assignedTo
                throw new ConflictException("RequestedBy must be admin or assignee!");
            }
        }

        for(AssignmentDetailDTO assignmentDetailDTO : requestReturnDTO.getAssignmentDetails()) {
            for(AssignmentDetailEntity assignmentDetail : assignmentDetails) {
                if(assignmentDetailDTO.getAssetCode().equals(assignmentDetail.getAsset().getAssetCode())) {
                    assignmentDetail.setState(AssignmentState.WAITING_FOR_RETURNING);
                    assignmentDetail.setRequestReturn(requestReturn);
                    assignmentDetailsForReturn.add(assignmentDetail);
                }
            }
        }

        requestReturn.setState(RequestReturnState.WAITING_FOR_RETURNING);
        requestReturn.setRequestedDate(new Date());
        requestReturn.setRequestBy(requestBy);
        requestReturn.setAssignment(assignment);
        requestReturn.setAssignmentDetails(assignmentDetailsForReturn);

//        if(!requestBy.getUser().getUserName().equals(assignment.getAssignTo().getUser().getUserName()))
//        {
//            SimpleMailMessage msg = new SimpleMailMessage();
//            msg.setTo(assignment.getAssignTo().getEmail());
//            msg.setSubject("Returning Asset");
//            msg.setText("Your administrator need you to return assets to the company: " +
//                    "" +
//                    "\nAsset" +
//                    " " +
//                    "code: " + assignment.getAssetEntity().getAssetCode()+
//                    "\nAsset name: " + assignment.getAssetEntity().getAssetName()+
//                    "\nRequested Date: " + format.format(request.getRequestedDate())+
//                    "\nYou must return it within 3 days." +
//                    "\nPlease check your request by your account\nKind Regards," +
//                    "\nAdministrator");
//            javaMailSender.send(msg);
//        }

        if(!assignmentDetails.stream().anyMatch(x->x.getRequestReturn() == null))
            assignment.setState(AssignmentState.WAITING_FOR_RETURNING);
        return new RequestReturnDTO(requestReturnRepository.save(requestReturn));
    }

    @Override
    public List<RequestReturnDTO> getAllByAdminLocation(String adminUsername) {
        LocationEntity location = userRepository.findByUserName(adminUsername).get().getUserDetail().getLocation();
        List<RequestReturnDTO> requestReturnDTOS = requestReturnRepository.findAll().stream()
                .filter(request -> (request.getRequestBy().getLocation().equals(location)))
                .sorted((o1, o2) -> (int) (o1.getId() - o2.getId()))
                .map(RequestReturnDTO::new).collect(Collectors.toList());
        return requestReturnDTOS;
    }

    @Override
    public void delete(Long id, String username) {
        UsersEntity requestBy = userRepository.findByUserName(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found!"));
        RequestReturnEntity requestReturn = requestReturnRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(REQUEST_NOT_FOUND_ERROR));
        if (!requestReturn.getState().equals(RequestReturnState.WAITING_FOR_RETURNING)) {
            throw new BadRequestException(REQUEST_STATE_INVALID_ERROR);
        }

        if(!requestBy.getUserName().equals(requestReturn.getRequestBy().getUser().getUserName())) {
            throw new BadRequestException("The request wasn't created by user so cannot delete!");
        }

        AssignmentEntity assignment = requestReturn.getAssignment();
        List<AssignmentDetailEntity> assignmentDetails = requestReturn.getAssignmentDetails();
        for(AssignmentDetailEntity assignmentDetail : assignmentDetails) {
            if(assignmentDetail.getRequestReturn().getId() == requestReturn.getId()) {
                assignmentDetail.setState(AssignmentState.ACCEPTED);
                assignmentDetail.setRequestReturn(null);
            }
        }

        if(assignmentDetails.stream().anyMatch(x->x.getRequestReturn() == null))
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
        for(AssignmentDetailEntity assignmentDetail : assignment.getAssignmentDetails()) {
            if(assignmentDetail.getState() == AssignmentState.WAITING_FOR_RETURNING) {
                assignmentDetail.setState(AssignmentState.COMPLETED);
                assignmentService.updateAvailableAssetState(assignmentDetail);
            }
        }

        if(!assignment.getAssignmentDetails().stream().anyMatch(x->x.getState() == null))
            assignment.setState(AssignmentState.COMPLETED);
        requestReturn.setAssignment(assignment);
        requestReturn.setReturnedDate(new Date());
        requestReturn.setState(RequestReturnState.COMPLETED);
        requestReturn.setAcceptBy(userRepository.getByStaffCode(staffCode).getUserDetail());

//        if(requestReturn.getRequestBy().getUser().getRole().equals(RoleName.ROLE_STAFF))
//        {
//            SimpleMailMessage msg = new SimpleMailMessage();
//            msg.setTo(assignment.getAssignTo().getEmail());
//            msg.setSubject("Your request ");
//            msg.setText("Administrator has been accepted your request: " +
//                    "\nRequestID: "+requestReturn.getId()+
//                    "\nAsset code: "+assignment.getAssetEntity().getAssetCode()+
//                    "\nAsset name: "+ assignment.getAssetEntity().getAssetName()+
//                    "\nRequest state: "+request.getState()+
//                    "\nPlease check your request by your account\nKind Regards," +
//                    "\nAdministrator");
//            javaMailSender.send(msg);
//        }
        return new RequestReturnDTO(requestReturnRepository.save(requestReturn));
    }

    private final String REQUEST_NOT_FOUND_ERROR = "Request not found.";
    private final String REQUEST_STATE_INVALID_ERROR = "Request state must be 'Waiting for returning'.";
}
