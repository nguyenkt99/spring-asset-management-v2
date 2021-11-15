package com.nashtech.assetmanagement.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.nashtech.assetmanagement.entity.RequestReturnEntity;
import com.nashtech.assetmanagement.constants.RequestReturnState;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
    private LocalDateTime requestedDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private LocalDateTime returnedDate;
    private String requestBy;
    private String acceptBy;
    private Long assignmentId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private LocalDate assignedDate;
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
