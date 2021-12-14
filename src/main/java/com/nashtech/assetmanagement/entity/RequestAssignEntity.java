package com.nashtech.assetmanagement.entity;

import com.nashtech.assetmanagement.constants.RequestAssignState;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
    private LocalDateTime requestedDate;

    @Column(name = "updated_date")
    private LocalDateTime updatedDate;

    @Column(name = "assigned_date")
    private LocalDate intendedAssignDate;

    @Column(name = "returned_date")
    private LocalDate intendedReturnDate;

    @ManyToOne
    @JoinColumn(name="requested_by")
    private UserDetailEntity requestAssignBy;

    @OneToOne(mappedBy = "requestAssign")
    private AssignmentEntity assignment;

    @OneToMany(mappedBy = "requestAssign", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RequestAssignDetailEntity> requestAssignDetails = new ArrayList<>();

}
