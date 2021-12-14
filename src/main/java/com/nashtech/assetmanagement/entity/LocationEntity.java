package com.nashtech.assetmanagement.entity;

import com.nashtech.assetmanagement.constants.Location;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "locations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LocationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "name", length = 20, unique = true)
    private Location name;

    @OneToMany(mappedBy = "location")
    private List<DepartmentEntity> departments;

    @OneToMany(mappedBy = "location")
    private List<AssetEntity> assets;
}
