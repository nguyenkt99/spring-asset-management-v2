package com.nashtech.assetmanagement.service;

import com.nashtech.assetmanagement.dto.report.AssignmentAssignedDTO;
import com.nashtech.assetmanagement.dto.report.ReportDTO;

import java.util.List;

public interface ReportService {
    List<ReportDTO> getReport(String username);

    /* Report */
    List<ReportDTO> getReports(String username);
    List<AssignmentAssignedDTO> getAssignmentAssignedByDate(String date, String username);

}
