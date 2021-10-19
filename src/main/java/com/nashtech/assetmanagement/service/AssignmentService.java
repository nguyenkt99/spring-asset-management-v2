package com.nashtech.assetmanagement.service;

import java.util.List;

import com.nashtech.assetmanagement.dto.AssignmentDTO;

public interface AssignmentService {
    List<AssignmentDTO> getAllByAdminLocation(String username);
    List<AssignmentDTO> getAssignmentsByUser(String username);
    AssignmentDTO getAssignmentById(Long assignmentId);
    AssignmentDTO save(AssignmentDTO assignmentDTO);
    AssignmentDTO updateAssignment(AssignmentDTO assignmentDTO);
    boolean deleteAssignment(Long assignmentId);
    AssignmentDTO updateStateAssignment(AssignmentDTO assignmentDTO, String username);
}
