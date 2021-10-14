package com.nashtech.AssetManagement_backend.service.Impl;

import com.nashtech.AssetManagement_backend.dto.AssignmentDetailDTO;
import com.nashtech.AssetManagement_backend.dto.RequestReturnDTO;
//import com.nashtech.AssetManagement_backend.dto.RequestReturnDetailDTO;
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
    AssignmentDetailRepository assignmentDetailRepository;

    @Autowired
    AssignmentRepository assignmentRepository;

    @Autowired
    UserRepository userRepository;

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
    public void delete(Long id) {
        RequestReturnEntity request = requestReturnRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Request not found"));
        if (!request.getState().equals(RequestReturnState.WAITING_FOR_RETURNING))
            throw new ConflictException("Request must be waiting for returning!");
//        request.getAssignmentEntity().setState(AssignmentState.ACCEPTED);
        requestReturnRepository.deleteById(id);
    }

    @Override
    public RequestReturnDTO accept(Long id, String staffCode) {
        RequestReturnEntity request = requestReturnRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(REQUEST_NOT_FOUND_ERROR));
        if (!request.getState().equals(RequestReturnState.WAITING_FOR_RETURNING))
            throw new BadRequestException(REQUEST_STATE_INVALID_ERROR);
        request.setState(RequestReturnState.COMPLETED);
        request.setAcceptBy(userRepository.getByStaffCode(staffCode).getUserDetail());
        request.setReturnedDate(new Date());
        requestReturnRepository.save(request);
//        AssignmentEntity assignment = request.getAssignmentEntity();
//        AssetEntity asset = assignment.getAssetEntity();
//        asset.setState(AssetState.AVAILABLE);
//        assignment.setAssetEntity(asset);
//        assignment.setState(AssignmentState.COMPLETED);
//        if(request.getRequestBy().getUser().getRole().equals(RoleName.ROLE_STAFF))
//        {
//            SimpleMailMessage msg = new SimpleMailMessage();
//            msg.setTo(assignment.getAssignTo().getEmail());
//            msg.setSubject("Your request ");
//            msg.setText("Administrator has been accepted your request: " +
//                    "\nRequestID: "+request.getId()+
//                    "\nAsset code: "+assignment.getAssetEntity().getAssetCode()+
//                    "\nAsset name: "+ assignment.getAssetEntity().getAssetName()+
//                    "\nRequest state: "+request.getState()+
//                    "\nPlease check your request by your account\nKind Regards," +
//                    "\nAdministrator");
//            javaMailSender.send(msg);
//        }
//        assignmentRepository.save(assignment);
        return new RequestReturnDTO(request);
    }

    private final String REQUEST_NOT_FOUND_ERROR = "Request not found.";
    private final String REQUEST_STATE_INVALID_ERROR = "Request state must be 'Waiting for returning'.";
}
