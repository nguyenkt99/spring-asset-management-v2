package com.nashtech.AssetManagement_backend.controller;

import com.nashtech.AssetManagement_backend.dto.RequestAssignDTO;
import com.nashtech.AssetManagement_backend.dto.RequestDTO;
import com.nashtech.AssetManagement_backend.security.services.UserDetailsImpl;
import com.nashtech.AssetManagement_backend.service.RequestAssignService;
import com.nashtech.AssetManagement_backend.service.RequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
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

    @PutMapping("/{id}")
    public ResponseEntity<?> acceptRequest(@PathVariable Long id,
                                                    @RequestBody RequestAssignDTO requestAssignDTO,
                                                    Authentication authentication) {
        requestAssignDTO.setId(id);
        return requestAssignService.updateState(requestAssignDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> acceptRequest(@PathVariable Long id, Authentication authentication) {
        requestAssignService.delete(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }

}
