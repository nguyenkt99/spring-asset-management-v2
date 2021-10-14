package com.nashtech.AssetManagement_backend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.nashtech.AssetManagement_backend.entity.RequestReturnEntity;
import com.nashtech.AssetManagement_backend.entity.RequestReturnState;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RequestReturnDTO {
    private Long id;
    private String note;
    private RequestReturnState state;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private Date requestedDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private Date returnedDate;
    private String requestBy;
    private String acceptBy;
    private Long assignmentId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private Date assignedDate;
    private List<AssignmentDetailDTO> assignmentDetails = new ArrayList<>();

    public RequestReturnDTO(RequestReturnEntity entity) {
        this.id = entity.getId();
        this.note = entity.getNote();
        this.state = entity.getState();
        this.requestedDate = entity.getRequestedDate();
        this.returnedDate = entity.getReturnedDate();
        this.requestBy = entity.getRequestBy().getUser().getUserName();
        if(entity.getAcceptBy() != null)
            this.acceptBy = entity.getAcceptBy().getUser().getUserName();
        this.assignmentId = entity.getAssignment().getId();
        this.assignedDate = entity.getAssignment().getAssignedDate();
        this.assignmentDetails = entity.getAssignmentDetails().stream().map(AssignmentDetailDTO::new).collect(Collectors.toList());
    }

    public RequestReturnEntity toEntity() {
        RequestReturnEntity entity = new RequestReturnEntity();
        entity.setNote(this.note);
        entity.setState(this.state);
        return entity;
    }
}
