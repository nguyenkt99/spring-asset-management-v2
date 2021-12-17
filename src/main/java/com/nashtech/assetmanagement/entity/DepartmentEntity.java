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
    @Column(name = "dept_code", length = 6)
    private String deptCode;

    @Column(name ="name", unique = true, length = 20)
    private String name;

    @ManyToOne
    @JoinColumn(name="location_id")
    private LocationEntity location;

    @OneToMany(mappedBy = "department")
    private List<UserDetailEntity> userDetailEntities = new ArrayList<>();

}
