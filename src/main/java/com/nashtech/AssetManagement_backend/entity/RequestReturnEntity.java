package com.nashtech.AssetManagement_backend.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "request_returns")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RequestReturnEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "state", length = 30)
    private RequestReturnState state;

    @Column(name = "note")
    private String note;

    @Column(name = "requested_date")
    private Date requestedDate;

    @Column(name = "returned_date")
    private Date returnedDate;

    @ManyToOne
    @JoinColumn(name="request_by")
    private UserDetailEntity requestBy;

    @ManyToOne
    @JoinColumn(name="accept_by")
    private UserDetailEntity acceptBy;

    @ManyToOne
    @JoinColumn(name="assignment_id")
    private AssignmentEntity assignment;

    @OneToMany(mappedBy = "requestReturn", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    List<AssignmentDetailEntity> assignmentDetails;

}
