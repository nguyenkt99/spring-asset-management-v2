package com.nashtech.assetmanagement.service.impl;

import com.nashtech.assetmanagement.constants.AssetState;
import com.nashtech.assetmanagement.constants.AssignmentState;
import com.nashtech.assetmanagement.constants.RepairState;
import com.nashtech.assetmanagement.dto.RepairDTO;
import com.nashtech.assetmanagement.entity.*;
import com.nashtech.assetmanagement.exception.ConflictException;
import com.nashtech.assetmanagement.exception.ResourceNotFoundException;
import com.nashtech.assetmanagement.repository.AssetRepository;
import com.nashtech.assetmanagement.repository.RepairRepository;
import com.nashtech.assetmanagement.repository.UserRepository;
import com.nashtech.assetmanagement.service.RepairService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RepairServiceImpl implements RepairService {
    @Autowired
    RepairRepository repairRepository;

    @Autowired
    AssetRepository assetRepository;

    @Autowired
    UserRepository userRepository;

    @Override
    public RepairDTO create(RepairDTO dto, String username) {
        AssetEntity asset = assetRepository.findByAssetCode(dto.getAssetCode())
                .orElseThrow(() -> new ResourceNotFoundException("Asset not found!"));

        if(asset.getState() == AssetState.REPAIRING)
            throw new ConflictException("Asset is being repaired!");

        UserDetailEntity user = userRepository.findByUserName(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found!")).getUserDetail();

        RepairEntity repair = dto.toEntity();
        List<AssignmentDetailEntity> assignmentDetails = asset.getAssignmentDetails();
        LocalDate now = LocalDate.now();

        // check asset must be available
        if(assignmentDetails.stream().anyMatch(ad ->
                ad.getAssignment().getAssignedDate().isBefore(now)
                && ad.getAssignment().getIntendedReturnDate().isAfter(now)
                && ad.getState() != AssignmentState.COMPLETED && ad.getState() != AssignmentState.DECLINED))
            throw new ConflictException("Asset is busy! Please return before create repair.");

        asset.setState(AssetState.REPAIRING);
        repair.setAsset(asset);
        repair.setCreatedBy(user);
        repair.setStartedDate(now);
        repair.setState(RepairState.REPAIRING);
        return new RepairDTO(repairRepository.save(repair));
    }

    @Override
    public List<RepairDTO> getRepairs(String username) {
        Long locationId = userRepository.findByUserName(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found")).getUserDetail().getDepartment().getLocation().getId();
        return repairRepository.findAllByLocationId(locationId).stream().map(RepairDTO::new).collect(Collectors.toList());
    }

    @Override
    public void delete(Long id) {
        RepairEntity repair = repairRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("The repair not found!"));
        repair.getAsset().setState(AssetState.AVAILABLE);
        repairRepository.deleteById(id);
    }

    @Override
    public RepairDTO update(RepairDTO dto) {
        return null;
    }
}
