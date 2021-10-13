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

    @Enumerated(EnumType.STRING)
    @Column(length = 30,name = "state")
    private AssignmentState state;

    @Column(name = "returned_date")
    private Date returnedDate;

    @ManyToOne
    @JoinColumn(name="request_return_id")
    private RequestReturnEntity requestReturn;


//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @ManyToOne
//    @JoinColumn(name="assignment_id")
//    private AssignmentEntity assignment;
//
//    @ManyToOne
//    @JoinColumn(name="asset_code")
//    private AssetEntity asset;
//
//    @Enumerated(EnumType.STRING)
//    @Column(length = 30,name = "state")
//    private AssignmentState state;
//
//    @Column(name = "returned_date")
//    private Date returnedDate;
//
//    @OneToOne(mappedBy = "assignmentDetail", fetch = FetchType.LAZY)
//    @PrimaryKeyJoinColumn
//    private RequestReturnDetailEntity requestReturnDetail;

}
