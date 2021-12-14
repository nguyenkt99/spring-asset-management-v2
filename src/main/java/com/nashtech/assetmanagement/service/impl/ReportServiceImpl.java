package com.nashtech.assetmanagement.service.impl;

import com.nashtech.assetmanagement.dto.report.*;
import com.nashtech.assetmanagement.entity.CategoryEntity;
import com.nashtech.assetmanagement.entity.LocationEntity;
import com.nashtech.assetmanagement.repository.AssetRepository;
import com.nashtech.assetmanagement.repository.AssignmentRepository;
import com.nashtech.assetmanagement.repository.CategoryRepository;
import com.nashtech.assetmanagement.repository.UserRepository;
import com.nashtech.assetmanagement.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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

    @Autowired
    AssignmentRepository assignmentRepository;

    @Override
    public List<ReportDTO> getReport(String username) {
        LocationEntity location = userRepository.findByUserName(username).get().getUserDetail().getDepartment().getLocation();
        List<CategoryEntity> categories = categoryRepository.findAll();
        List<ReportDTO> reportList = new ArrayList<>();
        for(CategoryEntity category : categories) {
            reportList.add(getReportByCategory(category, location));
        }
        return reportList;
    }

    private ReportDTO getReportByCategory(CategoryEntity category, LocationEntity location) {
//        ReportDTO report = new ReportDTO(category.getName(), 0, 0, 0, 0, 0, 0); // old report
        ReportDTO report = new ReportDTO(category.getName(), 0, 0, 0, 0, 0, 0, 0); // new report
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
                case "REPAIRING":
                    report.setRepairing(stateQuantity.getQuantity());
                    break;
            }
        }
        return report;
    }

    /* Report */
    @Override
    public List<ReportDTO> getReports(String username) {
        LocationEntity location = userRepository.findByUserName(username).get().getUserDetail().getDepartment().getLocation();
        List<ReportNewDTO> resultList = assetRepository.getReports(location.getId());

        List<ReportDTO> reportDTOs = new ArrayList<>();
        for(ReportNewDTO r : resultList) {
            reportDTOs.add(new ReportDTO(r.getCategory(), r.getTotal(), r.getAssigned(), r.getAvailable(),
                    r.getNotAvailable(), r.getWaitingForRecycle(), r.getRecycled(), r.getRepairing()));
        }
        return reportDTOs;
    }

    @Override
    public List<AssignmentAssignedDTO> getAssignmentAssignedByDate(String date, String username) {
        List<AssignmentAssignedDTO> assignmentAssignedDTOs = new ArrayList<>();
        LocalDate parsedDate = LocalDate.parse(date);
        LocationEntity location = userRepository.findByUserName(username).get().getUserDetail().getDepartment().getLocation();
        List<IAssignmentAssigned> resultList = assignmentRepository.getAssignmentsAssignedByDate(parsedDate, location.getId());
        for(IAssignmentAssigned i : resultList) {
            assignmentAssignedDTOs.add(new AssignmentAssignedDTO(i.getAssetCode(), i.getAssetName(), i.getAssignedBy(), i.getAssignedTo()));
        }

        return assignmentAssignedDTOs;
    }
}
