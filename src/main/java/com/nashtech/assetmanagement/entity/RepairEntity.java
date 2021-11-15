package com.nashtech.assetmanagement.entity;

import com.nashtech.assetmanagement.constants.RepairState;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

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

    @Column(name = "note")
    private String note;

    @Enumerated(EnumType.STRING)
    @Column(name = "state", length = 30)
    private RepairState state;

    @ManyToOne
    @JoinColumn(name="asset_code")
    private AssetEntity asset;

    @Column(name = "started_date")
    private LocalDateTime startedDate;

    @Column(name = "finished_date")
    private LocalDateTime finishedDate;

    @ManyToOne
    @JoinColumn(name="repair_by")
    private UserDetailEntity repairBy;

}
