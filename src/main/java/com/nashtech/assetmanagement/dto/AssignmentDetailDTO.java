package com.nashtech.assetmanagement.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.nashtech.assetmanagement.entity.AssignmentDetailEntity;
import com.nashtech.assetmanagement.constants.AssignmentState;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AssignmentDetailDTO {
    @NotBlank
    private String assetCode;

    @NotBlank
    private String assetName;

    private String category;

    private String specs;

    private AssignmentState state;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private LocalDate assignedDate;

    private String assignedTo;

    private String assignedBy;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private LocalDateTime returnedDate;

    public AssignmentDetailDTO(AssignmentDetailEntity assignmentDetail) {
        this.assetCode = assignmentDetail.getAsset().getAssetCode();
        this.assetName = assignmentDetail.getAsset().getAssetName();
        this.category = assignmentDetail.getAsset().getCategoryEntity().getName();
        this.specs = assignmentDetail.getAsset().getSpecification();
        this.state = assignmentDetail.getState();
        this.assignedDate = assignmentDetail.getAssignment().getAssignedDate();
        this.assignedTo = assignmentDetail.getAssignment().getAssignTo().getUser().getUserName();
        this.assignedBy = assignmentDetail.getAssignment().getAssignBy().getUser().getUserName();
//        this.returnedDate = assignmentDetail.getReturnedDate();
        if(assignmentDetail.getRequestReturn() != null) {
            this.returnedDate = assignmentDetail.getRequestReturn().getReturnedDate();
        }
    }

}
