package com.nashtech.assetmanagement.entity;

import com.nashtech.assetmanagement.constants.AssignmentState;
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

    @Column(name = "created_date")
    private Date createdDate;

    @Column(name = "updated_date")
    private Date updatedDate;

    @Column(name = "assigned_date")
    private Date assignedDate;

    @Column(name = "intended_return_date")
    private Date intendedReturnDate;

    @ManyToOne
    @JoinColumn(name="assign_to")
    private UserDetailEntity assignTo;

    @ManyToOne
    @JoinColumn(name="assign_by")
    private UserDetailEntity assignBy;

    @OneToMany(mappedBy = "assignment", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    List<AssignmentDetailEntity> assignmentDetails;

    @OneToMany(mappedBy = "assignment", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    List<RequestReturnEntity> requestReturns;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "request_assign_id", referencedColumnName = "id", nullable = true)
    private RequestAssignEntity requestAssign;
}

