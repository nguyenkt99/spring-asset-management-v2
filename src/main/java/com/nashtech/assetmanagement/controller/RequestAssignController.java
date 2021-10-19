package com.nashtech.assetmanagement.controller;

import com.nashtech.assetmanagement.dto.RequestAssignDTO;
import com.nashtech.assetmanagement.service.RequestAssignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/request-assign")
public class RequestAssignController {
    @Autowired
    RequestAssignService requestAssignService;

    @PostMapping
    public RequestAssignDTO create(@Valid @RequestBody RequestAssignDTO requestAssignDTO, Authentication authentication) {
        requestAssignDTO.setRequestedBy(authentication.getName());
        return requestAssignService.save(requestAssignDTO);
    }

    @GetMapping
    public List<RequestAssignDTO> getAll(Authentication authentication) {
        return requestAssignService.getAll(authentication.getName());
    }

    @PutMapping("/{requestAssignId}")
    public RequestAssignDTO edit(@Valid @RequestBody RequestAssignDTO requestAssignDTO, @PathVariable Long requestAssignId, Authentication authentication) {
        requestAssignDTO.setId(requestAssignId);
        requestAssignDTO.setRequestedBy(authentication.getName());
        return requestAssignService.update(requestAssignDTO);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> handleRequestAssign(@PathVariable Long id,
                                           @RequestBody RequestAssignDTO requestAssignDTO,
                                           Authentication authentication) {
        requestAssignDTO.setId(id);
        return requestAssignService.handleRequestAssign(requestAssignDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> acceptRequest(@PathVariable Long id, Authentication authentication) {
        requestAssignService.delete(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }

}
