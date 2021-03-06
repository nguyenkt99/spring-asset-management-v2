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
@Table(name = "categories")
public class CategoryEntity {
    @Id
    @Column(name = "category_code", length = 2)
    private String prefix;

    @Column(name ="name", unique = true, length = 15)
    private String name;

    @OneToMany(mappedBy = "categoryEntity")
    private List<AssetEntity> assetEntities = new ArrayList<>();

    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RequestAssignDetailEntity> requestAssignDetailEntities;
}
