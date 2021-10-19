package com.nashtech.assetmanagement.service;

import com.nashtech.assetmanagement.dto.ReportDTO;

import java.util.List;

public interface ReportService {
    List<ReportDTO> getReport(String username);
}
