package com.nashtech.assetmanagement.dto.report;

public interface ReportNewDTO {
    String getCategory();
    Integer getTotal();
    Integer getAssigned();
    Integer getAvailable();
    Integer getNotAvailable();
    Integer getWaitingForRecycle();
    Integer getRecycled();
    Integer getRepairing();
}
