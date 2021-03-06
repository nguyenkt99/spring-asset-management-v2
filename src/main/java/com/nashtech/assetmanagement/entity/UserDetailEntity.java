package com.nashtech.assetmanagement.entity;

import com.nashtech.assetmanagement.constants.Gender;
import com.nashtech.assetmanagement.constants.UserState;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;
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
    private LocalDate dateOfBirth;

    @Column(name = "joined_date")
    private LocalDate joinedDate;

    @Column(name = "email", length = 100)
    private String email;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private UserState state;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "staff_code")
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name="dept_code")
    private DepartmentEntity department;

    @OneToMany(mappedBy = "assignTo")
    private List<AssignmentEntity> assignmentTos = new ArrayList<>();

    @OneToMany(mappedBy = "assignBy")
    private List<AssignmentEntity> assignmentsBys = new ArrayList<>();

    @OneToMany(mappedBy = "requestBy")
    private List<RequestReturnEntity> requestReturnBys = new ArrayList<>();

    @OneToMany(mappedBy = "requestAssignBy")
    private List<RequestAssignEntity> requestAssignBys = new ArrayList<>();

    @OneToMany(mappedBy = "acceptBy")
    private List<RequestReturnEntity> acceptBys = new ArrayList<>();

    @OneToMany(mappedBy = "createdBy")
    private List<RepairEntity> repairBys = new ArrayList<>();

    @OneToMany(mappedBy = "user1")
    private List<ConversationEntity> conversationUser1s = new ArrayList<>();

    @OneToMany(mappedBy = "user2")
    private List<ConversationEntity> conversationUser2s = new ArrayList<>();

//    @PrePersist
//    protected void onCreate() {
//        this.state = UserState.ENABLED;
//    }

}
