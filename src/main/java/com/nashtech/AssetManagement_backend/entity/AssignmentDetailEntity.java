package com.nashtech.AssetManagement_backend.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "assignment_details")
public class AssignmentDetailEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="assignment_id")
    private AssignmentEntity assignment;

    @ManyToOne
    @JoinColumn(name="asset_code")
    private AssetEntity asset;

    @Enumerated(EnumType.STRING)
    @Column(length = 30,name = "state")
    private AssignmentDetailState state;

    @Column(name = "expiry_date")
    private Date expiryDate;

    @OneToOne(mappedBy = "assignmentDetail", fetch = FetchType.LAZY)
    @PrimaryKeyJoinColumn
    private RequestDetailEntity requestDetail;
}
