package com.nashtech.assetmanagement.controller;

import com.nashtech.assetmanagement.dto.AssetDTO;

import com.nashtech.assetmanagement.service.AssetService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/asset")
public class AssetController {
    @Autowired
    AssetService assetService;

    @PostMapping()
    public ResponseEntity<AssetDTO> create(@Valid @RequestBody AssetDTO dto, Authentication authentication) {
        return ResponseEntity.ok().body(assetService.create(dto, authentication.getName()));
    }

    @GetMapping("/{assetCode}/delete")
    public ResponseEntity<Boolean> canDelete(@PathVariable("assetCode") String assetCode) {
        return ResponseEntity.ok().body(assetService.canDelete(assetCode));
    }

    @DeleteMapping("/{assetCode}")
    public ResponseEntity<Boolean> delete(@PathVariable("assetCode") String assetCode) {
        return ResponseEntity.ok().body(assetService.delete(assetCode));
    }

    @GetMapping
    public List<AssetDTO> getAll(Authentication authentication){
        return assetService.findAllByAdminLocation(authentication.getName());
    }

    @GetMapping("/available")
    public List<AssetDTO> getAvailableAsset(
            @RequestParam(value = "assignmentId", required = false) Long assignmentId,
                                            @RequestParam("startDate") String startDate,
                                            @RequestParam("endDate") String endDate,
                                            Authentication authentication){
        return assetService.getAvailableAsset(startDate, endDate, authentication.getName(), assignmentId);
    }

    @GetMapping("/{assetCode}")
    public ResponseEntity<AssetDTO> getAssetByAssetCode(@PathVariable("assetCode") String assetCode){
        AssetDTO assetDTOs = assetService.findByAssetCode(assetCode);
        return new ResponseEntity<>(assetDTOs, HttpStatus.OK);
    }

    @PutMapping("/{assetCode}")
    public ResponseEntity<AssetDTO> update(@Valid @RequestBody AssetDTO dto, @PathVariable("assetCode") String assetCode) {
        dto.setAssetCode(assetCode);
        return ResponseEntity.ok().body(assetService.update(dto));
    }

}

