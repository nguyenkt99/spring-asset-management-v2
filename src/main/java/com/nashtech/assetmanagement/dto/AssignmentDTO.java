package com.nashtech.assetmanagement.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.nashtech.assetmanagement.entity.AssignmentEntity;
import com.nashtech.assetmanagement.constants.AssignmentState;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AssignmentDTO {
    private Long id;

    private Long requestAssignId;

    @NotBlank
    private String assignedTo;

    @NotBlank
    private String assignedBy;

    private AssignmentState state;

    private String note;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private LocalDateTime createdDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private LocalDate updatedDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private LocalDate assignedDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private LocalDate intendedReturnDate;;

//    private Boolean isCreatedRequest = false;

    private List<AssignmentDetailDTO> assignmentDetails = new ArrayList<>();

    public AssignmentDTO(AssignmentEntity assignment) {
        this.id = assignment.getId();
        if(assignment.getRequestAssign() != null)
            this.requestAssignId = assignment.getRequestAssign().getId();
        this.assignedTo = assignment.getAssignTo().getUser().getUserName();
        this.assignedBy = assignment.getAssignBy().getUser().getUserName();
        this.state = assignment.getState();
        this.note = assignment.getNote();
        this.createdDate = assignment.getCreatedDate();
        this.assignedDate = assignment.getAssignedDate();
        this.intendedReturnDate = assignment.getIntendedReturnDate();
        this.assignmentDetails = assignment.getAssignmentDetails().stream().map(AssignmentDetailDTO::new).collect(Collectors.toList());
    }

    public AssignmentEntity toEntity() {
        AssignmentEntity assignment = new AssignmentEntity();
        assignment.setNote(this.note);
        assignment.setAssignedDate(this.assignedDate);
        assignment.setIntendedReturnDate(this.intendedReturnDate);
        return assignment;
    }
}
