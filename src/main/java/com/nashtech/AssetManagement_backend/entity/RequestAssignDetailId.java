package com.nashtech.AssetManagement_backend.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@Getter
@Setter
public class RequestAssignDetailId implements Serializable {
    @Column(name = "request_assign_id")
    private Long requestAssignId;
    @Column(name = "category_id")
    private String categoryId;

}
