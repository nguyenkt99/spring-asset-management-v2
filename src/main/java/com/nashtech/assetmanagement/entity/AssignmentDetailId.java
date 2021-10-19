package com.nashtech.assetmanagement.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@Getter
@Setter
public class AssignmentDetailId implements Serializable {
    @Column(name = "assignment_id")
    private Long assignmentId;
    @Column(name = "asset_code")
    private String assetCode;

}
