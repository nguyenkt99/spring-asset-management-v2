package com.nashtech.assetmanagement.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.nashtech.assetmanagement.constants.RequestAssignState;
import com.nashtech.assetmanagement.entity.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RequestAssignDTO {
    private Long id;
    private String note;
    private RequestAssignState state;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private LocalDateTime requestedDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private LocalDateTime updatedDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private LocalDate intendedAssignDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private LocalDate intendedReturnDate;
    private String requestedBy;
    private List<RequestAssignDetailDTO> requestAssignDetails = new ArrayList<>();

    public RequestAssignDTO(RequestAssignEntity entity) {
        this.id = entity.getId();
        this.note = entity.getNote();
        this.state = entity.getState();
        this.requestedDate = entity.getRequestedDate();
        this.updatedDate = entity.getUpdatedDate();
        this.intendedAssignDate = entity.getIntendedAssignDate();
        this.intendedReturnDate = entity.getIntendedReturnDate();
        this.requestedBy = entity.getRequestAssignBy().getUser().getUserName();
        this.requestAssignDetails = entity.getRequestAssignDetails().stream().map(RequestAssignDetailDTO::new).collect(Collectors.toList());
    }

    public RequestAssignEntity toEntity() {
        RequestAssignEntity entity = new RequestAssignEntity();
        entity.setState(this.state);
        entity.setNote(this.note);
        entity.setRequestedDate(this.requestedDate);
        entity.setIntendedAssignDate(this.intendedAssignDate);
        entity.setIntendedReturnDate(this.intendedReturnDate);
        return entity;
    }
}
