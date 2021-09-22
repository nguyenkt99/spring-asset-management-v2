package com.nashtech.AssetManagement_backend.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "assignment_details")
public class AssignmentDetail {
    @EmbeddedId
    private AssignmentDetailId id = new AssignmentDetailId();

    @ManyToOne
    @MapsId("assignmentId")
    @JoinColumn(name="assignment_id")
    private AssignmentEntity assignment;

    @ManyToOne
    @MapsId("assetCode")
    @JoinColumn(name="asset_code")
    private AssetEntity asset;

}
