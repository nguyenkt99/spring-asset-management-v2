package com.nashtech.AssetManagement_backend.entity;

import com.nashtech.AssetManagement_backend.generators.PasswordGenerator;
import com.nashtech.AssetManagement_backend.generators.StaffCodeGenerator;
import com.nashtech.AssetManagement_backend.generators.UsernameGenerator;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.*;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "accounts")
public class UsersEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq")
    @GenericGenerator(
            name = "user_seq",
            strategy = "com.nashtech.AssetManagement_backend.generators.StaffCodeGenerator",
            parameters = {
                    @Parameter(name = StaffCodeGenerator.NUMBER_FORMAT_PARAMETER, value = "%04d") })
    @Column(name = "staff_code", length = 6)
    private String staffCode;


    @GeneratorType(type = UsernameGenerator.class, when = GenerationTime.INSERT)
    @Column(name = "user_name", length = 20)
    private String userName;

    @Size(min=6, max = 100)
    @GeneratorType(type = PasswordGenerator.class, when = GenerationTime.INSERT)
    private String password;

    @Column(name = "is_first_login")
    private boolean isFirstLogin;

    @ManyToOne
    @JoinColumn(name="role_id")
    private RolesEntity role;


    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @PrimaryKeyJoinColumn
    private UserDetailEntity userDetail;


}
