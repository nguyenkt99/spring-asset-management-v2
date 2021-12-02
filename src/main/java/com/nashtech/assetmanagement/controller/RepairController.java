package com.nashtech.assetmanagement.controller;

import com.nashtech.assetmanagement.dto.RepairDTO;
import com.nashtech.assetmanagement.service.RepairService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/repair")
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

    @PutMapping("/{id}")
    public RepairDTO finishRepair(@PathVariable Long id, @RequestBody String note) {
        return repairService.finishRepair(id, note);
    }

    @DeleteMapping("/{repairId}")
    public void delete(@PathVariable Long repairId) {
        repairService.delete(repairId);
    }
}
