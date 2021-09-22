package com.nashtech.AssetManagement_backend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.nashtech.AssetManagement_backend.entity.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RequestAssignDTO {
    private Long id;
    @NotBlank
    private String prefix;
    private String category;
    @NotBlank
    private String note;
    private RequestAssignState state;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private Date requestedDate;
    private String requestedBy;

    public RequestAssignDTO(RequestAssignEntity entity) {
        this.id = entity.getId();
        this.prefix = entity.getCategoryEntity().getPrefix();
        this.category = entity.getCategoryEntity().getName();
        this.note = entity.getNote();
        this.state = entity.getState();
        this.requestedDate = entity.getRequestedDate();
        this.requestedBy = entity.getRequestBy().getUser().getUserName();
    }

    public RequestAssignEntity toEntity() {
        RequestAssignEntity entity = new RequestAssignEntity();
        entity.setState(this.state);
        entity.setNote(this.note);
        entity.setRequestedDate(this.requestedDate);
        return entity;
    }
}
