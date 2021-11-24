package com.nashtech.assetmanagement.service;

import com.nashtech.assetmanagement.dto.RequestAssignDTO;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface RequestAssignService {
    RequestAssignDTO save(RequestAssignDTO requestAssignDTO);
    RequestAssignDTO update(RequestAssignDTO requestAssignDTO);
    List<RequestAssignDTO> getAll(String username);
    RequestAssignDTO getRequestAssign(Long requestAssignId);
    RequestAssignDTO declineRequestAssign(Long id, String note);
    void delete(Long id, String username);
}
