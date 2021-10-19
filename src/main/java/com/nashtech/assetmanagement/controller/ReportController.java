package com.nashtech.assetmanagement.controller;

import com.nashtech.assetmanagement.dto.ReportDTO;
import com.nashtech.assetmanagement.service.ReportService;
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
