//package com.nashtech.AssetManagement_backend.dto;
//
//import com.fasterxml.jackson.annotation.JsonFormat;
//import com.nashtech.AssetManagement_backend.entity.AssignmentDetailEntity;
//import com.nashtech.AssetManagement_backend.entity.AssignmentState;
//import lombok.AllArgsConstructor;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import lombok.Setter;
//
//import javax.validation.constraints.NotBlank;
//import java.util.Date;
//
//@Getter
//@Setter
//@NoArgsConstructor
//@AllArgsConstructor
//public class RequestReturnDetailDTO {
//    private Long assignmentDetailId;
//    private String assetCode;
//    private String assetName;
////    private String category;
////    private String specs;
//    private AssignmentState state;
//    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
//    private Date returnedDate;
//
//    public RequestReturnDetailDTO(RequestReturnDetailEntity entity) {
//        this.assignmentDetailId = entity.getId();
//        this.assetCode = entity.getAssignmentDetail().getAsset().getAssetCode();
//        this.assetName = entity.getAssignmentDetail().getAsset().getAssetName();
////        this.category = entity.getAsset().getCategoryEntity().getName();
////        this.specs = entity.getAsset().getSpecification();
//        this.state = entity.getAssignmentDetail().getState();
//        this.returnedDate = entity.getAssignmentDetail().getReturnedDate();
//    }
//
////    public RequestReturnDetailEntity toEntity() {
////        RequestReturnDetailEntity requestReturnDetail = new RequestReturnDetailEntity();
////    }
//
//}
