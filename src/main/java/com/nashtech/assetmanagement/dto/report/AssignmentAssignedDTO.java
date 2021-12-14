package com.nashtech.assetmanagement.dto.report;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AssignmentAssignedDTO {
    private String assetCode;
    private String assetName;
    private String assignedBy;
    private String assignedTo;
}
