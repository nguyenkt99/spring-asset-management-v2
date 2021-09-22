package com.nashtech.AssetManagement_backend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.nashtech.AssetManagement_backend.entity.AssetEntity;
import com.nashtech.AssetManagement_backend.entity.AssetState;
import com.nashtech.AssetManagement_backend.entity.Location;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AssetDTO {
    private String assetCode;
    @NotBlank(message = "asset name can not be empty")
    @Length(max = 50)
    private String assetName;
    private AssetState state;
    private String specification;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private Date installedDate;
    private Location location;
    @NotBlank(message = "category prefix can not be empty.")
    @Length(min = 2, max = 2, message = "length is 2.")
    private String categoryPrefix;
    private String categoryName;

    private List<AssignmentDetailDTO> assignmentDetailDTOList = new ArrayList<>();

    public static AssetDTO toDTO(AssetEntity asset){
        if (asset == null)
            return null;
        AssetDTO dto = new AssetDTO();
        dto.setAssetCode(asset.getAssetCode());
        dto.setLocation(asset.getLocation().getName());
        dto.setAssetName(asset.getAssetName());
        dto.setSpecification(asset.getSpecification());
        dto.setCategoryPrefix(asset.getCategoryEntity().getPrefix());
        dto.setInstalledDate(asset.getInstalledDate());
        dto.setState(asset.getState());
        dto.setCategoryName(asset.getCategoryEntity().getName());
        dto.setAssignmentDetailDTOList(asset.getAssignmentDetails()
            .stream().map(AssignmentDetailDTO::new).collect(Collectors.toList()));
        return dto;
    }

    public static AssetEntity toEntity(AssetDTO dto){
        if (dto == null)
            return null;
        AssetEntity asset = new AssetEntity();
        asset.setAssetName(dto.getAssetName());
        asset.setInstalledDate(dto.getInstalledDate());
        asset.setState(dto.getState());
        asset.setSpecification(dto.getSpecification());
        return asset;
    }
}
