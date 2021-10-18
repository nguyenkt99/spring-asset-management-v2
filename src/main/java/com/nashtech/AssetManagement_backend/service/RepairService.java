package com.nashtech.AssetManagement_backend.service;

import com.nashtech.AssetManagement_backend.dto.AssetDTO;
import com.nashtech.AssetManagement_backend.dto.RepairDTO;

import java.util.List;

public interface RepairService {
    RepairDTO create(RepairDTO dto, String username);

    List<RepairDTO> getRepairs(String username);

    void delete(Long id);

    RepairDTO update(RepairDTO dto);
}
