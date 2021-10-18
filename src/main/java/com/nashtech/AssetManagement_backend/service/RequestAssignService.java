package com.nashtech.AssetManagement_backend.service;

import com.nashtech.AssetManagement_backend.dto.RequestAssignDTO;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface RequestAssignService {
    RequestAssignDTO save(RequestAssignDTO requestAssignDTO);
    RequestAssignDTO update(RequestAssignDTO requestAssignDTO);
    List<RequestAssignDTO> getAll(String username);
    ResponseEntity<?> handleRequestAssign(RequestAssignDTO requestAssignDTO);
    void delete(Long id, String username);
}
