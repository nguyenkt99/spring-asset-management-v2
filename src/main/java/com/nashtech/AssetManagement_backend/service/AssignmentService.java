package com.nashtech.AssetManagement_backend.service;

import java.util.List;

import com.nashtech.AssetManagement_backend.dto.AssignmentDTO;
import com.nashtech.AssetManagement_backend.entity.AssignmentState;
import com.nashtech.AssetManagement_backend.entity.LocationEntity;

public interface AssignmentService {
    List<AssignmentDTO> getAllByAdmimLocation(String username);
    List<AssignmentDTO> getAssignmentsByUser(String username);
    AssignmentDTO getAssignmentById(Long assignmentId);
    AssignmentDTO save(AssignmentDTO assignmentDTO);
    AssignmentDTO update(AssignmentDTO assignmentDTO);

    boolean deleteAssignment(Long assignmentId, LocationEntity location);

    AssignmentDTO updateStateAssignment(AssignmentDTO assignmentDTO, String username);
}
