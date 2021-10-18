package com.nashtech.AssetManagement_backend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.nashtech.AssetManagement_backend.entity.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Date;
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
    private Date requestedDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private Date updatedDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private Date intendedAssignDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private Date intendedReturnDate;
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
        this.requestedBy = entity.getRequestBy().getUser().getUserName();
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
