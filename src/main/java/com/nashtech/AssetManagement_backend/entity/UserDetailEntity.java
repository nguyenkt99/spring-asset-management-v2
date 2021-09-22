package com.nashtech.AssetManagement_backend.entity;

import com.nashtech.AssetManagement_backend.generators.StaffCodeGenerator;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "staffs")
public class UserDetailEntity {
    @Id
    @Column(name = "staff_code", length = 6)
    private String staffCode;

    @Column(name = "first_name", length = 20)
    private String firstName;

    @Column(name = "last_name", length = 100)
    private String lastName;

    @Enumerated(EnumType.STRING)
    @Column(length = 10,name = "gender")
    private Gender gender;

    @Column(name = "date_of_birth")
    private Date dateOfBirth;

    @Column(name = "joined_date")
    private Date joinedDate;

    @Column(name = "email", length = 100)
    private String email;

    @ManyToOne
    @JoinColumn(name="location_id")
    private LocationEntity location;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private UserState state;

    @OneToMany(mappedBy = "assignTo")
    private List<AssignmentEntity> assignmentTos = new ArrayList<>();

    @OneToMany(mappedBy = "assignBy")
    private List<AssignmentEntity> assignmentsBys = new ArrayList<>();

    @OneToMany(mappedBy = "requestBy")
    private List<RequestEntity> requestBys = new ArrayList<>();

    @OneToMany(mappedBy = "acceptBy")
    private List<RequestEntity> acceptBys = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "staff_code")
    private UsersEntity user;


    @PrePersist
    protected void onCreate() {
        this.state = UserState.Enable;
    }

}
