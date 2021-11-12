package com.nashtech.assetmanagement.service;

import com.nashtech.assetmanagement.dto.AssetDTO;

import java.util.List;

public interface AssetService {
    AssetDTO create(AssetDTO dto, String username);

    List<AssetDTO> findAllByAdminLocation(String username);

    public AssetDTO findByAssetName(String assetName);

    public AssetDTO findByAssetCode(String assetCode);

    Boolean canDelete(String assetCode);

    Boolean delete(String assetCode);

    AssetDTO update(AssetDTO dto);

    int countByCategory(String prefix, String username);

    List<AssetDTO> getAvailableAsset(String startDate, String endDate, String username, Long assignmentId);
}
