package com.nashtech.assetmanagement.dto;

import com.nashtech.assetmanagement.entity.DepartmentEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentDTO {
    @Length(min = 1, max = 5, message = "length 1-5")
    private String deptCode;
    @Length(min = 1, max = 15, message = "length 1-15")
    private String name;

    public DepartmentDTO(DepartmentEntity dep) {
        this.deptCode = dep.getDeptCode();
        this.name = dep.getName();
    }
    public DepartmentEntity toEntity() {
        DepartmentEntity dep = new DepartmentEntity();
        dep.setName(this.name);
        dep.setDeptCode(this.deptCode);
        return dep;
    }
}
