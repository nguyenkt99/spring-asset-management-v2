package com.nashtech.AssetManagement_backend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.nashtech.AssetManagement_backend.entity.RequestReturnEntity;
import com.nashtech.AssetManagement_backend.entity.RequestReturnState;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RequestDTO {
    private Long id;
    private RequestReturnState state;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private Date requestedDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private Date returnedDate;
    private String requestBy;
    private String acceptBy;
    @NotNull
    private Long assignmentId;
    private String assetCode;
    private String assetName;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private Date assignedDate;

    public RequestDTO(RequestReturnEntity entity) {
        this.id = entity.getId();
        this.state = entity.getState();
        this.requestedDate = entity.getRequestedDate();
        this.returnedDate = entity.getReturnedDate();
        this.requestBy = entity.getRequestBy().getUser().getUserName();
        if(entity.getAcceptBy() != null)
            this.acceptBy = entity.getAcceptBy().getUser().getUserName();
//        this.assignmentId = entity.getAssignmentEntity().getId();
//        this.assignedDate = entity.getAssignmentEntity().getAssignedDate();
    }

    public RequestReturnEntity toEntity() {
        RequestReturnEntity entity = new RequestReturnEntity();
        entity.setState(this.state);
        entity.setRequestedDate(entity.getRequestedDate());
        entity.setReturnedDate(entity.getReturnedDate());
        return entity;
    }
}
