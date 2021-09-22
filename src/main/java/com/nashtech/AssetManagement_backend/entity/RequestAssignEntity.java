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

    @ManyToOne
    @JoinColumn(name="category_id")
    private CategoryEntity categoryEntity;

    @ManyToOne
    @JoinColumn(name="request_by")
    private UserDetailEntity requestBy;

}
