package com.nashtech.AssetManagement_backend.controller;
import com.nashtech.AssetManagement_backend.dto.AssignmentDTO;
import com.nashtech.AssetManagement_backend.entity.AssignmentState;
import com.nashtech.AssetManagement_backend.entity.LocationEntity;
import com.nashtech.AssetManagement_backend.exception.BadRequestException;
import com.nashtech.AssetManagement_backend.service.AssignmentService;
import com.nashtech.AssetManagement_backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/assignment")
public class AssignmentController {
    @Autowired
    AssignmentService assignmentService;

    @Autowired
    private UserService userService;


    @GetMapping("")
    @ResponseBody
    public ResponseEntity<List<AssignmentDTO>> getAllByAdmimLocation(Authentication authentication) {
        List<AssignmentDTO> assignmentDTOs = assignmentService.getAllByAdmimLocation(authentication.getName());
        if (assignmentDTOs.isEmpty()) {
            return new ResponseEntity<>(assignmentDTOs, HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(assignmentDTOs, HttpStatus.OK);
    }

    @GetMapping("/home")
    @ResponseBody
    public ResponseEntity<List<AssignmentDTO>> getAssignmentsByUser(Authentication authentication){
        List<AssignmentDTO> assignmentDTOs = assignmentService.getAssignmentsByUser(authentication.getName());
        if (assignmentDTOs.isEmpty()){
            return new ResponseEntity<>(assignmentDTOs, HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(assignmentDTOs, HttpStatus.OK);
    }

    @GetMapping("/{assignmentId}")
    public AssignmentDTO getAssignmentById(@PathVariable Long assignmentId) {
        return assignmentService.getAssignmentById(assignmentId);
    }
    
    @PostMapping
    public AssignmentDTO createAssignment(@RequestBody AssignmentDTO assignmentDTO, Authentication authentication) {
        assignmentDTO.setAssignedBy(authentication.getName());
        return assignmentService.save(assignmentDTO);
    }

    @PutMapping("/{assignmentId}")
    public AssignmentDTO editAssignment(@PathVariable("assignmentId") Long assignmentId, @RequestBody AssignmentDTO assignmentDTO, Authentication authentication) {
        assignmentDTO.setAssignedBy(authentication.getName());
        assignmentDTO.setId(assignmentId);
        return assignmentService.update(assignmentDTO);
    }


    @DeleteMapping("/{assignmentId}")
    public ResponseEntity<Map<String, Boolean>> deleteCategory(Authentication authentication,
                                                               @PathVariable("assignmentId") Long assignmentId) {
        LocationEntity location = userService.getLocationByUserName(authentication.getName());
        assignmentService.deleteAssignment(assignmentId, location);
        Map<String, Boolean> map = new HashMap<>();
        map.put("success", true);
        return new ResponseEntity<>(map, HttpStatus.OK);
    }


    @PutMapping("/staff/{assignmentId}")
    public ResponseEntity<AssignmentDTO> changeStateStaffAssignment(@PathVariable("assignmentId") Long assignmentId
            , @RequestBody AssignmentDTO assignmentDTO, Authentication authentication) {
        String username = authentication.getName();
        assignmentDTO.setId((assignmentId));
        return ResponseEntity.ok(assignmentService.updateStateAssignment(assignmentDTO, username));
    }


}
