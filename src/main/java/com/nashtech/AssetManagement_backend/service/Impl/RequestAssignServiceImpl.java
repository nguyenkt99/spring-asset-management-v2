package com.nashtech.AssetManagement_backend.service.Impl;

import com.nashtech.AssetManagement_backend.dto.RequestAssignDTO;
import com.nashtech.AssetManagement_backend.entity.*;
import com.nashtech.AssetManagement_backend.exception.BadRequestException;
import com.nashtech.AssetManagement_backend.exception.ConflictException;
import com.nashtech.AssetManagement_backend.exception.ResourceNotFoundException;
import com.nashtech.AssetManagement_backend.repository.*;
import com.nashtech.AssetManagement_backend.service.RequestAssignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RequestAssignServiceImpl implements RequestAssignService {
    @Autowired
    RequestAssignRepository requestAssignRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Override
    public RequestAssignDTO save(RequestAssignDTO requestAssignDTO) {
        RequestAssignEntity requestAssign = requestAssignDTO.toEntity();

        CategoryEntity category = categoryRepository.findById(requestAssignDTO.getPrefix())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found!"));
        UserDetailEntity requestBy = userRepository.findByUserName(requestAssignDTO.getRequestedBy())
                .orElseThrow(() -> new ResourceNotFoundException("RequestBy not found!")).getUserDetail();

        requestAssign.setState(RequestAssignState.WAITING_FOR_ASSIGNING);
        requestAssign.setCategoryEntity(category);
        requestAssign.setRequestBy(requestBy);
        requestAssign.setRequestedDate(new Date());
        return new RequestAssignDTO(requestAssignRepository.save(requestAssign));
    }

    @Override
    public List<RequestAssignDTO> getAll(String username) {
        UserDetailEntity user = userRepository.findByUserName(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found!")).getUserDetail();

        List<RequestAssignEntity> requestAssignEntities = new ArrayList<>();
        if(user.getUser().getRole().getName().equals(RoleName.ROLE_STAFF)) {
            requestAssignEntities = requestAssignRepository.findByRequestBy_StaffCodeOrderByIdAsc(user.getStaffCode());
        } else {
            requestAssignEntities = requestAssignRepository.findByRequestBy_LocationAndRequestBy_StateOrderByIdAsc(user.getLocation(), UserState.Enable);
        }

        return requestAssignEntities.stream().map(RequestAssignDTO::new).collect(Collectors.toList());
    }

    @Override
    public ResponseEntity<?> updateState(RequestAssignDTO requestAssignDTO) {
        if(requestAssignDTO.getState() == null) {
            throw new BadRequestException("State is invalid!");
        }

        RequestAssignEntity requestAssign = requestAssignRepository.findById(requestAssignDTO.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Request for assigning not found!"));
        if (requestAssign.getState() != RequestAssignState.WAITING_FOR_ASSIGNING)
            throw new BadRequestException("Request for assigning can be update when state is waiting for assigning!");

        // if admin accept then delete this
        if(requestAssignDTO.getState().equals(RequestAssignState.ACCEPTED)) {
            requestAssignRepository.deleteById(requestAssignDTO.getId());
            return ResponseEntity.noContent().build();
        } else { // declined
            requestAssign.setNote(requestAssignDTO.getNote());
            requestAssign.setState(requestAssignDTO.getState());
            return ResponseEntity.ok(new RequestAssignDTO(requestAssignRepository.save(requestAssign)));
        }
    }

    @Override
    public void delete(Long id, String username) {
        UserDetailEntity userDetail = userRepository.findByUserName(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found!")).getUserDetail();

        RequestAssignEntity requestAssign = requestAssignRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Request not found"));

        if(!userDetail.getUser().getRole().getName().equals(RoleName.ROLE_ADMIN)) {
            if(!requestAssign.getRequestBy().getUser().getUserName().equals(username)) {
                throw new ConflictException("User does not own this request for assigning!");
            }
        }
//        if (!requestAssign.getState().equals(RequestAssignState.WAITING_FOR_ASSIGNING))
//            throw new ConflictException("Request must be waiting for returning!");
        requestAssignRepository.deleteById(id);
    }

}
