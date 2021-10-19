package com.nashtech.assetmanagement.dto;

import com.nashtech.assetmanagement.entity.RequestAssignDetailEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
