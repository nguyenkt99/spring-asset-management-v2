package com.nashtech.assetmanagement.service;

import com.nashtech.assetmanagement.dto.RepairDTO;

import java.util.List;

public interface RepairService {
    RepairDTO create(RepairDTO dto, String username);

    List<RepairDTO> getRepairs(String username);

    void delete(Long id);

    RepairDTO update(RepairDTO dto);
}
