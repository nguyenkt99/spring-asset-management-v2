package com.nashtech.AssetManagement_backend.service;

import com.nashtech.AssetManagement_backend.dto.RequestDTO;

import java.util.List;

public interface RequestService {
    RequestDTO create(RequestDTO requestDTO);

    List<RequestDTO> getAllByAdminLocation(String adminUsername);

    void delete(Long id);

    RequestDTO accept(Long requestId, String staffCode);
}

