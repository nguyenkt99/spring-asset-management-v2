package com.nashtech.AssetManagement_backend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.nashtech.AssetManagement_backend.entity.AssignmentDetailEntity;
import com.nashtech.AssetManagement_backend.entity.RequestAssignDetailEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RequestAssignDetailDTO {
    private Long requestAssignId;
    private String categoryId;
    private String categoryName;
    private Integer quantity;

    public RequestAssignDetailDTO(RequestAssignDetailEntity entity) {
        this.requestAssignId = entity.getRequestAssign().getId();
        this.categoryId = entity.getCategory().getPrefix();
        this.categoryName = entity.getCategory().getName();
        this.quantity = entity.getQuantity();
    }

}
