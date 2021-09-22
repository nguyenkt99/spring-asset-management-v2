package com.nashtech.AssetManagement_backend.service;

import com.nashtech.AssetManagement_backend.dto.ReportDTO;

import java.util.List;

public interface ReportService {
    List<ReportDTO> getReport(String username);
}
