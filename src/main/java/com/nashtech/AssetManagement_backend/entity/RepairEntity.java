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
@Table(name = "repairs")
public class RepairEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "state", length = 30)
    private RequestReturnState state;

    @ManyToOne
    @JoinColumn(name="asset_code")
    private AssetEntity asset;

    @Column(name = "requested_date")
    private Date requestedDate;

    @Column(name = "repaired_date")
    private Date repairedDate;

    @ManyToOne
    @JoinColumn(name="repair_by")
    private UserDetailEntity repairBy;

}
