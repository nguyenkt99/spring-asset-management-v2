package com.nashtech.AssetManagement_backend.service.Impl;

import com.nashtech.AssetManagement_backend.dto.ReportDTO;
import com.nashtech.AssetManagement_backend.dto.StateQuantity;
import com.nashtech.AssetManagement_backend.entity.CategoryEntity;
import com.nashtech.AssetManagement_backend.entity.LocationEntity;
import com.nashtech.AssetManagement_backend.repository.AssetRepository;
import com.nashtech.AssetManagement_backend.repository.CategoryRepository;
import com.nashtech.AssetManagement_backend.repository.UserRepository;
import com.nashtech.AssetManagement_backend.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ReportServiceImpl implements ReportService {
    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    AssetRepository assetRepository;

    @Autowired
    UserRepository userRepository;

    @Override
    public List<ReportDTO> getReport(String username) {
        LocationEntity location = userRepository.findByUserName(username).get().getUserDetail().getLocation();
        List<CategoryEntity> categories = categoryRepository.findAll();
        List<ReportDTO> reportList = new ArrayList<>();
        for(CategoryEntity category : categories) {
            reportList.add(getReportByCategory(category, location));
        }
        return reportList;
    }

    private ReportDTO getReportByCategory(CategoryEntity category, LocationEntity location) {
        ReportDTO report = new ReportDTO(category.getName(), 0, 0, 0, 0, 0, 0);
        List<StateQuantity> stateQuantityList = assetRepository.countState(category.getPrefix(), location.getId());

        report.setTotal(assetRepository.countByCategoryEntityAndLocation(category, location));
        for(StateQuantity stateQuantity : stateQuantityList) {
            switch (stateQuantity.getState()) {
                case "AVAILABLE":
                    report.setAvailable(stateQuantity.getQuantity());
                    break;
                case "NOT_AVAILABLE":
                    report.setNotAvailable(stateQuantity.getQuantity());
                    break;
                case "ASSIGNED":
                    report.setAssigned(stateQuantity.getQuantity());
                    break;
                case "WAITING_FOR_RECYCLING":
                    report.setWaitingForRecycle(stateQuantity.getQuantity());
                    break;
                case "RECYCLED":
                    report.setRecycled(stateQuantity.getQuantity());
                    break;
            }
        }
        return report;
    }
}
