package com.nashtech.AssetManagement_backend.service.Impl;

import com.nashtech.AssetManagement_backend.dto.RepairDTO;
import com.nashtech.AssetManagement_backend.entity.*;
import com.nashtech.AssetManagement_backend.exception.ConflictException;
import com.nashtech.AssetManagement_backend.exception.ResourceNotFoundException;
import com.nashtech.AssetManagement_backend.repository.AssetRepository;
import com.nashtech.AssetManagement_backend.repository.RepairRepository;
import com.nashtech.AssetManagement_backend.repository.UserRepository;
import com.nashtech.AssetManagement_backend.service.RepairService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
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

        if(asset.getState() == AssetState.REPAIRING) {
            throw new ConflictException("Asset is being repaired!");
        }

        UserDetailEntity user = userRepository.findByUserName(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found!")).getUserDetail();

        RepairEntity repair = dto.toEntity();
        List<AssignmentDetailEntity> assetAssignmentDetails = asset.getAssignmentDetails();
        Date current = new Date();
        for (AssignmentDetailEntity assignmentDetail : assetAssignmentDetails) {
            asset.setState(AssetState.REPAIRING);
            if (assignmentDetail.getAssignment().getAssignedDate().before(current)
                    && assignmentDetail.getAssignment().getIntendedReturnDate().after(current)
                    && assignmentDetail.getState() != AssignmentState.COMPLETED)
                throw new ConflictException("Asset is busy! Please return before create repair.");
        }

//        repair.setStartedDate(current);
        repair.setAsset(asset);
        repair.setRepairBy(user);
        repair.setState(RepairState.REPAIRING);
        return new RepairDTO(repairRepository.save(repair));
    }

    @Override
    public List<RepairDTO> getRepairs(String username) {
        Long locationId = userRepository.findByUserName(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found")).getUserDetail().getLocation().getId();
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
