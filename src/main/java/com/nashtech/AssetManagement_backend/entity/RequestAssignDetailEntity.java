package com.nashtech.AssetManagement_backend.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name= "request_assign_details")
public class RequestAssignDetailEntity {
    @EmbeddedId
    private RequestAssignDetailId id = new RequestAssignDetailId();

    @ManyToOne
    @MapsId("requestAssignId")
    @JoinColumn(name="request_assign_id")
    private RequestAssignEntity requestAssign;

    @ManyToOne
    @MapsId("categoryId")
    @JoinColumn(name="category_id")
    private CategoryEntity category;

    @Column(name = "quantity")
    private Integer quantity;

}
