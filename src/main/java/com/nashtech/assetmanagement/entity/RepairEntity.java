package com.nashtech.assetmanagement.entity;

import com.nashtech.assetmanagement.constants.RepairState;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

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
    private LocalDate startedDate;

    @Column(name = "finished_date")
    private LocalDate finishedDate;

    @ManyToOne
    @JoinColumn(name="created_by")
    private UserDetailEntity createdBy;

}
