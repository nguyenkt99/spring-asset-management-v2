package com.nashtech.assetmanagement.service.Impl;

import com.nashtech.assetmanagement.dto.RequestAssignDTO;
import com.nashtech.assetmanagement.dto.RequestAssignDetailDTO;
import com.nashtech.assetmanagement.entity.*;
import com.nashtech.assetmanagement.exception.BadRequestException;
import com.nashtech.assetmanagement.exception.ConflictException;
import com.nashtech.assetmanagement.exception.ResourceNotFoundException;
import com.nashtech.assetmanagement.repository.*;
import com.nashtech.assetmanagement.service.RequestAssignService;
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

        List<RequestAssignDetailEntity> requestAssignDetails = requestAssign.getRequestAssignDetails();
        for(RequestAssignDetailDTO r : requestAssignDTO.getRequestAssignDetails()) {
            RequestAssignDetailEntity requestAssignDetail = new RequestAssignDetailEntity();
            CategoryEntity category = categoryRepository.findById(r.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found!"));

            int sumOfAvailableAsset = categoryRepository.getSumOfAvailableAssetByCategory(r.getCategoryId(), requestAssignDTO.getIntendedAssignDate(), requestAssignDTO.getIntendedReturnDate());
            if(r.getQuantity() > sumOfAvailableAsset) {
                throw new ConflictException("Asset not enough!");
            }
            requestAssignDetail.setCategory(category);
            requestAssignDetail.setRequestAssign(requestAssign);
            requestAssignDetail.setQuantity(r.getQuantity());
            requestAssignDetails.add(requestAssignDetail);
        }

        UserDetailEntity requestBy = userRepository.findByUserName(requestAssignDTO.getRequestedBy())
                .orElseThrow(() -> new ResourceNotFoundException("RequestBy not found!")).getUserDetail();
        requestAssign.setState(RequestAssignState.WAITING_FOR_ASSIGNING);
        requestAssign.setRequestBy(requestBy);
        requestAssign.setRequestedDate(new Date());
        requestAssign.setIntendedAssignDate(requestAssignDTO.getIntendedAssignDate());
        requestAssign.setIntendedReturnDate(requestAssignDTO.getIntendedReturnDate());
        return new RequestAssignDTO(requestAssignRepository.save(requestAssign));
    }

    @Override
    public RequestAssignDTO update(RequestAssignDTO requestAssignDTO) {
        RequestAssignEntity requestAssign = requestAssignRepository.findById(requestAssignDTO.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Request assign not found!"));
        if(requestAssign.getState() != RequestAssignState.WAITING_FOR_ASSIGNING) {
            throw new BadRequestException("Request for assigning can be update when is waiting for assigning state!");
        }

        List<RequestAssignDetailEntity> requestAssignDetails = requestAssign.getRequestAssignDetails();
        List<RequestAssignDetailDTO> requestAssignDetailDTOs = requestAssignDTO.getRequestAssignDetails();

        for(int i = 0; i < requestAssignDetails.size(); i++) {
            boolean isNoLonger = true;
            for(RequestAssignDetailDTO requestAssignDetailDTO : requestAssignDetailDTOs) {
                if (requestAssignDetailDTO.getCategoryId().equals(requestAssignDetails.get(i).getCategory().getPrefix())) {
                    isNoLonger = false;
                }
            }

            if(isNoLonger) {
                requestAssignDetails.remove(i);
                --i;
            }
        }

        for(RequestAssignDetailDTO requestAssignDetailDTO : requestAssignDTO.getRequestAssignDetails()) {
            boolean isExists = false;
            for(RequestAssignDetailEntity requestAssignDetail : requestAssignDetails) {
                if(requestAssignDetail.getCategory().getPrefix().equals(requestAssignDetailDTO.getCategoryId())) {
                    isExists = true;
                    int sumOfAvailableAsset = categoryRepository.getSumOfAvailableAssetByCategory(requestAssignDetailDTO.getCategoryId()
                            , requestAssignDTO.getIntendedAssignDate(), requestAssignDTO.getIntendedReturnDate());
                    if(requestAssignDetailDTO.getQuantity() > sumOfAvailableAsset) {
                        throw new ConflictException("Asset not enough!");
                    }
                    requestAssignDetail.setQuantity(requestAssignDetailDTO.getQuantity());
                    break;
                }
            }
            if(!isExists) {
                RequestAssignDetailEntity newRequestAssignDetail = new RequestAssignDetailEntity();
                CategoryEntity category = categoryRepository.findById(requestAssignDetailDTO.getCategoryId())
                        .orElseThrow(() -> new ResourceNotFoundException("Category not found!"));
                int sumOfAvailableAsset = categoryRepository.getSumOfAvailableAssetByCategory(category.getPrefix()
                        , requestAssignDTO.getIntendedAssignDate(), requestAssignDTO.getIntendedReturnDate());
                if(requestAssignDetailDTO.getQuantity() > sumOfAvailableAsset) {
                    throw new ConflictException("Asset not enough!");
                }
                newRequestAssignDetail.setCategory(category);
                newRequestAssignDetail.setQuantity(requestAssignDetailDTO.getQuantity());
                newRequestAssignDetail.setRequestAssign(requestAssign);
                requestAssignDetails.add(newRequestAssignDetail);
            }
        }

        requestAssign.setUpdatedDate(new Date());
        requestAssign.setIntendedAssignDate(requestAssignDTO.getIntendedAssignDate());
        requestAssign.setIntendedReturnDate(requestAssignDTO.getIntendedReturnDate());
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
    public ResponseEntity<?> handleRequestAssign(RequestAssignDTO requestAssignDTO) {
        if(requestAssignDTO.getState() == null) {
            throw new BadRequestException("State is invalid!");
        }

        RequestAssignEntity requestAssign = requestAssignRepository.findById(requestAssignDTO.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Request for assigning not found!"));
        if (requestAssign.getState() != RequestAssignState.WAITING_FOR_ASSIGNING)
            throw new BadRequestException("Request for assigning can be update when state is waiting for assigning!");

        // if admin decline then the request has note
        if(requestAssignDTO.getState().equals(RequestAssignState.DECLINED)) {
            if(requestAssignDTO.getNote() == null) {
                throw new BadRequestException("Note cannot be null!");
            }
            if(requestAssignDTO.getNote().isEmpty()) {
                throw new BadRequestException("Note cannot be empty!");
            }
            requestAssign.setNote(requestAssignDTO.getNote());
        }
        requestAssign.setState(requestAssignDTO.getState());
        return ResponseEntity.ok(new RequestAssignDTO(requestAssignRepository.save(requestAssign)));
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

        if (!requestAssign.getState().equals(RequestAssignState.WAITING_FOR_ASSIGNING))
            throw new ConflictException("Request must be waiting for returning state!");
        requestAssignRepository.deleteById(id);
    }

}
