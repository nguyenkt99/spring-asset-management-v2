package com.nashtech.AssetManagement_backend.controller;

import com.nashtech.AssetManagement_backend.dto.ReportDTO;
import com.nashtech.AssetManagement_backend.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/report")
public class ReportController {
    @Autowired
    ReportService reportService;

    @GetMapping
    public List<ReportDTO> getReport(Authentication authentication) {
        return reportService.getReport(authentication.getName());
    }
}
