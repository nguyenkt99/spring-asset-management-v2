package com.nashtech.AssetManagement_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReportDTO {
    private String category;
    private int total;
    private int assigned;
    private int available;
    private int notAvailable;
    private int waitingForRecycle;
    private int recycled;
}