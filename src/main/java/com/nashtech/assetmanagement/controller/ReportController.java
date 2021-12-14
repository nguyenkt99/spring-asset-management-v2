package com.nashtech.assetmanagement.controller;

import com.nashtech.assetmanagement.dto.report.AssignmentAssignedDTO;
import com.nashtech.assetmanagement.dto.report.ReportDTO;
import com.nashtech.assetmanagement.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/count-by-category")
    public List<ReportDTO> getReports(Authentication authentication) {
        return reportService.getReports(authentication.getName());
    }

    @GetMapping("/assignments-assigned/{date}")
    public List<AssignmentAssignedDTO> getAssignmentsAssignedByDate(@PathVariable String date, Authentication authentication) {
        return reportService.getAssignmentAssignedByDate(date, authentication.getName());
    }
}
