package com.nashtech.AssetManagement_backend.service;

import com.nashtech.AssetManagement_backend.dto.RequestReturnDTO;

import java.util.List;

public interface RequestReturnService {
    RequestReturnDTO create(RequestReturnDTO requestReturnDTO);

    List<RequestReturnDTO> getAllByAdminLocation(String adminUsername);

    void delete(Long id, String username);

    RequestReturnDTO accept(Long requestId, String staffCode);
}

