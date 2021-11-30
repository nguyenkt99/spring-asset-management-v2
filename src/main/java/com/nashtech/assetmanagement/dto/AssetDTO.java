package com.nashtech.assetmanagement.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.nashtech.assetmanagement.constants.AssetState;
import com.nashtech.assetmanagement.constants.AssignmentState;
import com.nashtech.assetmanagement.constants.Location;
import com.nashtech.assetmanagement.constants.RepairState;
import com.nashtech.assetmanagement.entity.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AssetDTO {
    private static LocalDate current = LocalDate.now();
    private String assetCode;
    @NotBlank(message = "asset name can not be empty")
    @Length(max = 50)
    private String assetName;
    private AssetState state;
    private String specification;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private LocalDate installedDate;
    private Location location;
    @NotBlank(message = "category prefix can not be empty.")
    @Length(min = 2, max = 2, message = "length is 2.")
    private String categoryPrefix;
    private String categoryName;
    private Boolean isFreeToday = true;
    private List<AssignmentDetailDTO> assignmentDetails = new ArrayList<>();
    private List<RepairDTO> repairs = new ArrayList<>();

    public AssetDTO(AssetEntity asset) {
        this.assetCode = asset.getAssetCode();
        this.assetName = asset.getAssetName();
        this.location = asset.getLocation().getName();
        this.specification = asset.getSpecification();
        this.categoryPrefix = asset.getCategoryEntity().getPrefix();
        this.categoryName = asset.getCategoryEntity().getName();
        this.installedDate = asset.getInstalledDate();
        this.state = asset.getState();

        // asset free today if it is available state or
        if(asset.getState() == AssetState.REPAIRING)
            this.isFreeToday = false;
        else {
            if(asset.getState() != AssetState.AVAILABLE) {
                List<AssignmentDetailEntity> assignmentDetails = asset.getAssignmentDetails();
                for (AssignmentDetailEntity assignmentDetail : assignmentDetails) {
                    if (assignmentDetail.getState() != AssignmentState.COMPLETED
                            && assignmentDetail.getState() != AssignmentState.DECLINED)
                        if (!(assignmentDetail.getAssignment().getIntendedReturnDate().isBefore(current)
                                || assignmentDetail.getAssignment().getAssignedDate().isAfter(current)))
                            this.isFreeToday = false;
                }
            }
        }

        this.assignmentDetails = asset.getAssignmentDetails().stream()
                .filter(ad -> ad.getState() != AssignmentState.DECLINED)
                .map(AssignmentDetailDTO::new).collect(Collectors.toList());

        this.repairs = asset.getRepairs().stream()
                .map(RepairDTO::new).collect(Collectors.toList());
    }

    public AssetEntity toEntity(){
        AssetEntity asset = new AssetEntity();
        asset.setAssetName(this.assetName);
        asset.setInstalledDate(this.installedDate);
        asset.setState(this.state);
        asset.setSpecification(this.specification);
        return asset;
    }
}
