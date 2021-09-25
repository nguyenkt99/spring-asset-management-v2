package com.nashtech.AssetManagement_backend.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "assignments")
public class AssignmentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "note")
    private String note;

    @Enumerated(EnumType.STRING)
    @Column(length = 30,name = "state")
    private AssignmentState state;

    @Column(name = "assigned_date")
    private Date assignedDate;

//    @ManyToOne
//    @JoinColumn(name="asset_id")
//    private AssetEntity assetEntity;

    @ManyToOne
    @JoinColumn(name="assign_to")
    private UserDetailEntity assignTo;

    @ManyToOne
    @JoinColumn(name="assign_by")
    private UserDetailEntity assignBy;

    @OneToMany(mappedBy = "assignment", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    List<AssignmentDetailEntity> assignmentDetails;

//    @OneToOne(mappedBy = "assignmentEntity", fetch = FetchType.LAZY)
//    @PrimaryKeyJoinColumn
//    private RequestEntity requestEntity;
}

