package com.nashtech.AssetManagement_backend.controller;

import com.nashtech.AssetManagement_backend.dto.AssetDTO;
import com.nashtech.AssetManagement_backend.dto.RepairDTO;
import com.nashtech.AssetManagement_backend.service.RepairService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/repair")
@RequiredArgsConstructor
public class RepairController {
    @Autowired
    RepairService repairService;

    @GetMapping
    public List<RepairDTO> getAll(Authentication authentication) {
        return repairService.getRepairs(authentication.getName());
    }

    @PostMapping
    public RepairDTO create(@Valid @RequestBody RepairDTO dto, Authentication authentication) {
        return repairService.create(dto, authentication.getName());
    }

    @DeleteMapping("/{repairId}")
    public void delete(@PathVariable Long repairId) {
        repairService.delete(repairId);
    }
}
