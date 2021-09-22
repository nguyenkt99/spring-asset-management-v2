package com.nashtech.AssetManagement_backend.service.Impl;

import com.nashtech.AssetManagement_backend.dto.RequestDTO;
import com.nashtech.AssetManagement_backend.entity.*;
import com.nashtech.AssetManagement_backend.exception.BadRequestException;
import com.nashtech.AssetManagement_backend.exception.ConflictException;
import com.nashtech.AssetManagement_backend.exception.ResourceNotFoundException;
import com.nashtech.AssetManagement_backend.repository.AssignmentRepository;
import com.nashtech.AssetManagement_backend.repository.RequestRepository;
import com.nashtech.AssetManagement_backend.repository.UserRepository;
import com.nashtech.AssetManagement_backend.service.RequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RequestServiceImpl implements RequestService {
    @Autowired
    RequestRepository requestRepository;

    @Autowired
    AssignmentRepository assignmentRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    JavaMailSender javaMailSender;
    SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");;
    @Override
    public RequestDTO create(RequestDTO requestDTO) {
        RequestEntity request = requestDTO.toEntity();

        AssignmentEntity assignment = assignmentRepository.findById(requestDTO.getAssignmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found!"));
        if (assignment.getState() != AssignmentState.ACCEPTED) {
            throw new ConflictException("Assignment must have accepted state!");
        }

        if (assignment.getRequestEntity() != null) {
            throw new ConflictException("Request has already been created!");
        }

        UserDetailEntity requestBy = userRepository.getByUserName(requestDTO.getRequestBy()).getUserDetail();
        if(!requestBy.getUser().getRole().getName().equals(RoleName.ROLE_ADMIN)) { // requestedBy is not admin
            if(!requestBy.equals(assignment.getAssignTo())) { // requestedBy is also not assignedTo
                throw new ConflictException("RequestedBy must be admin or assignee!");
            }
        }

        request.setRequestedDate(new Date());
        request.setAssignmentEntity(assignment);
        request.setRequestBy(requestBy);
        request.setState(RequestState.WAITING_FOR_RETURNING);
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
        assignment.setState(AssignmentState.WAITING_FOR_RETURNING);
        request.setAssignmentEntity(assignment);
        return new RequestDTO(requestRepository.save(request));
    }

    @Override
    public List<RequestDTO> getAllByAdminLocation(String adminUsername) {
        LocationEntity location = userRepository.findByUserName(adminUsername).get().getUserDetail().getLocation();
        List<RequestDTO> requestDTOs = requestRepository.findAll().stream()
                .filter(request -> (request.getRequestBy().getLocation().equals(location)))
                .sorted((o1, o2) -> (int) (o1.getId() - o2.getId()))
                .map(RequestDTO::new).collect(Collectors.toList());
        return requestDTOs;
    }

    @Override
    public void delete(Long id) {
        RequestEntity request = requestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Request not found"));
        if (!request.getState().equals(RequestState.WAITING_FOR_RETURNING))
            throw new ConflictException("Request must be waiting for returning!");
        request.getAssignmentEntity().setState(AssignmentState.ACCEPTED);
        requestRepository.deleteById(id);
    }

    @Override
    public RequestDTO accept(Long id, String staffCode) {
        RequestEntity request = requestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(REQUEST_NOT_FOUND_ERROR));
        if (!request.getState().equals(RequestState.WAITING_FOR_RETURNING))
            throw new BadRequestException(REQUEST_STATE_INVALID_ERROR);
        request.setState(RequestState.COMPLETED);
        request.setAcceptBy(userRepository.getByStaffCode(staffCode).getUserDetail());
        request.setReturnedDate(new Date());
        requestRepository.save(request);
        AssignmentEntity assignment = request.getAssignmentEntity();
//        AssetEntity asset = assignment.getAssetEntity();
//        asset.setState(AssetState.AVAILABLE);
//        assignment.setAssetEntity(asset);
        assignment.setState(AssignmentState.COMPLETED);
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
        assignmentRepository.save(assignment);
        return new RequestDTO(request);
    }

    private final String REQUEST_NOT_FOUND_ERROR = "Request not found.";
    private final String REQUEST_STATE_INVALID_ERROR = "Request state must be 'Waiting for returning'.";
}
