package com.nashtech.assetmanagement.service.impl;

import com.nashtech.assetmanagement.constants.AssetState;
import com.nashtech.assetmanagement.constants.AssignmentState;
import com.nashtech.assetmanagement.dto.AssetDTO;
import com.nashtech.assetmanagement.entity.*;
import com.nashtech.assetmanagement.exception.BadRequestException;
import com.nashtech.assetmanagement.exception.ConflictException;
import com.nashtech.assetmanagement.exception.ResourceNotFoundException;
import com.nashtech.assetmanagement.repository.AssetRepository;
import com.nashtech.assetmanagement.repository.AssignmentRepository;
import com.nashtech.assetmanagement.repository.CategoryRepository;
import com.nashtech.assetmanagement.repository.UserRepository;
import com.nashtech.assetmanagement.service.AssetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AssetServiceImpl implements AssetService {
    private final String CATEGORY_NOT_FOUND_ERROR = "Category prefix not exists.";
    private final String USER_NOT_FOUND_ERROR = "User not exists.";
    private final String ASSET_NOT_FOUND_ERROR = "Asset not found.";
    private final String ASSET_CONFLICT_ERROR = "Asset belongs to one or more historical assignments.";
    private final String ASSET_BAD_STATE_ERROR = "Asset must be AVAILABLE or NOT_AVAILABLE.";

    @Autowired
    AssetRepository assetRepo;

    @Autowired
    CategoryRepository categoryRepo;

    @Autowired
    UserRepository userRepo;

    @Autowired
    AssignmentRepository assignmentRepository;

    @Override
    public AssetDTO create(AssetDTO dto, String username) {
        CategoryEntity cate = categoryRepo.findByPrefix(dto.getCategoryPrefix())
                .orElseThrow(() -> new ResourceNotFoundException(CATEGORY_NOT_FOUND_ERROR));
        LocationEntity location = userRepo.findByUserName(username)
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND_ERROR)).getUserDetail().getDepartment().getLocation();
        if (dto.getState() != AssetState.NOT_AVAILABLE && dto.getState() != AssetState.AVAILABLE)
            throw new BadRequestException(ASSET_BAD_STATE_ERROR);
        AssetEntity asset = dto.toEntity();
        asset.setLocation(location);
        asset.setCategoryEntity(cate);
        return new AssetDTO(assetRepo.save(asset));
    }

    @Override
    public List<AssetDTO> findAllByAdminLocation(String username) {
        LocationEntity location = userRepo.findByUserName(username)
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND_ERROR)).getUserDetail().getDepartment().getLocation();
        return assetRepo.findAll(location.getId()).stream().map(AssetDTO::new).collect(Collectors.toList());
    }

    @Override
    public AssetDTO findByAssetName(String assetName) {
        AssetEntity assetEntity = assetRepo.findByAssetName(assetName).orElseThrow(
                () -> new ResourceNotFoundException("Asset is not found for this asset name:" + assetName));
        return new AssetDTO(assetEntity);
    }

    @Override
    public AssetDTO findByAssetCode(String assetCode) {
        AssetEntity assetEntity = assetRepo.findByAssetCode(assetCode).orElseThrow(
                () -> new ResourceNotFoundException("Asset is not found for this asset code:" + assetCode));
        return new AssetDTO(assetEntity);
    }

    @Override
    public Boolean canDelete(String assetCode) {
        return !(assetRepo.findByAssetCode(assetCode).orElseThrow(() -> new ResourceNotFoundException(ASSET_NOT_FOUND_ERROR))
                .getAssignmentDetails().size() > 0);
    }

    @Override
    public Boolean delete(String assetCode) {
        AssetEntity asset = assetRepo.findByAssetCode(assetCode)
                .orElseThrow(() -> new ResourceNotFoundException(ASSET_NOT_FOUND_ERROR));
        if (asset.getAssignmentDetails().size() > 0)
            throw new ConflictException(ASSET_CONFLICT_ERROR);
        assetRepo.deleteById(assetCode);
        return true;
    }

    @Override
    public AssetDTO update(AssetDTO dto) {
        AssetEntity asset = assetRepo.findByAssetCode(dto.getAssetCode())
                .orElseThrow(() -> new ResourceNotFoundException(ASSET_NOT_FOUND_ERROR));
        if (asset.getState() == AssetState.ASSIGNED) {
            throw new ConflictException("Asset has been assigned");
        }

        asset.setAssetName(dto.getAssetName());
        asset.setSpecification(dto.getSpecification());
        asset.setInstalledDate(dto.getInstalledDate());
        asset.setState(dto.getState());
        return new AssetDTO(assetRepo.save(asset));
    }

    @Override
    public int countByCategory(String prefix, String username) {
        LocationEntity location = userRepo.findByUserName(username).get().getUserDetail().getDepartment().getLocation();
        CategoryEntity category = categoryRepo.findById(prefix).get();
        return assetRepo.countByCategoryEntityAndLocation(category, location);
    }

    @Override
    public List<AssetDTO> getAvailableAsset(String startDate, String endDate, String username, Long assignmentId) {
        List<AssetEntity> assets;
        LocalDate date1 = LocalDate.parse(startDate);
        LocalDate date2 = LocalDate.parse(endDate);

        Long locationId = userRepo.findByUserName(username).get().getUserDetail().getDepartment().getLocation().getId();
        assets = assetRepo.findAvailableAsset(locationId, date1, date2);

        if(assignmentId != null) {
            AssignmentEntity assignment = assignmentRepository.findById(assignmentId)
                    .orElseThrow(() -> new ResourceNotFoundException("Assignment not found!"));
            List<AssetEntity> assetOlds = assignment.getAssignmentDetails().stream().map(ad -> ad.getAsset()).collect(Collectors.toList());
            for(AssetEntity asset : assetOlds) {
                boolean isExists = false; // flag to check asset already exist in other assignment
                for(AssignmentDetailEntity assignmentDetail : asset.getAssignmentDetails()) { // all asset's assignment
                    AssignmentEntity assignment2 = assignmentDetail.getAssignment();
                    if(assignment2.getState() != AssignmentState.COMPLETED
                        && assignment2.getState() != AssignmentState.DECLINED) {
                        if(!(assignment2.getIntendedReturnDate().isBefore(date1) // asset already exist in other assignment
                                || assignment2.getAssignedDate().isAfter(date2))
                            && assignment2.getId() != assignmentId) {
                            isExists = true;
                            break;
                        }
                    }
                }
                if(!assets.contains(asset) && !isExists) { // assets list does not include it then
                    assets.add(asset);
                }
            }
        }

        return assets.stream().sorted(Comparator.comparing(AssetEntity::getAssetCode)).map(AssetDTO::new)
                .collect(Collectors.toList());
    }
}
