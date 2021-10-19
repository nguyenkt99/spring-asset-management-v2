package com.nashtech.assetmanagement.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.nashtech.assetmanagement.constants.RepairState;
import com.nashtech.assetmanagement.entity.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RepairDTO {
    private Long id;
    private String assetCode;
    private String assetName;
    private String specification;
    private String note;
    private RepairState state;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private Date startedDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private Date finishedDate;
    private String repairedBy;

    public RepairDTO (RepairEntity entity){
        this.id = entity.getId();
        this.assetCode = entity.getAsset().getAssetCode();
        this.assetName = entity.getAsset().getAssetName();
        this.specification = entity.getAsset().getSpecification();
        this.note = entity.getNote();
        this.state = entity.getState();
        this.startedDate = entity.getStartedDate();
        this.finishedDate = entity.getFinishedDate();
        this.repairedBy = entity.getRepairBy().getUser().getUserName();
    }

    public RepairEntity toEntity(){
        RepairEntity entity = new RepairEntity();
        entity.setNote(this.note);
        entity.setState(this.state);
        entity.setStartedDate(this.startedDate);
        entity.setFinishedDate(this.finishedDate);
        return entity;
    }
}
