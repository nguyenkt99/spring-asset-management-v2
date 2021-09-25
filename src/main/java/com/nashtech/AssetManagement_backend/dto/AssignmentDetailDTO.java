package com.nashtech.AssetManagement_backend.dto;

import com.nashtech.AssetManagement_backend.entity.AssignmentDetailEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

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

    public AssignmentDetailDTO(AssignmentDetailEntity assignmentDetail) {
        this.assetCode = assignmentDetail.getAsset().getAssetCode();
        this.assetName = assignmentDetail.getAsset().getAssetName();
        this.category = assignmentDetail.getAsset().getCategoryEntity().getName();
        this.specs = assignmentDetail.getAsset().getSpecification();
    }

}
