package com.nashtech.assetmanagement.entity;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "departments")
public class DepartmentEntity {
    @Id
    @Column(name = "dept_code", length = 5)
    private String deptCode;

    @Column(name ="name", unique = true, length = 15)
    private String name;

    @OneToMany(mappedBy = "department")
    private List<UserDetailEntity> userDetailEntities = new ArrayList<>();

}
