package com.nashtech.assetmanagement.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name= "request_assigns")
public class RequestAssignEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "note")
    private String note;

    @Enumerated(EnumType.STRING)
    @Column(length = 30,name = "state")
    private RequestAssignState state;

    @Column(name = "requested_date")
    private Date requestedDate;

    @Column(name = "updated_date")
    private Date updatedDate;

    @Column(name = "intended_assign_date")
    private Date intendedAssignDate;

    @Column(name = "intended_return_date")
    private Date intendedReturnDate;

    @ManyToOne
    @JoinColumn(name="request_by")
    private UserDetailEntity requestBy;

    @OneToOne(mappedBy = "requestAssign")
    private AssignmentEntity assignment;

    @OneToMany(mappedBy = "requestAssign", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    List<RequestAssignDetailEntity> requestAssignDetails = new ArrayList<>();

}
