//package com.nashtech.AssetManagement_backend.entity;
//
//import lombok.AllArgsConstructor;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import lombok.Setter;
//import org.springframework.http.RequestEntity;
//
//import javax.persistence.*;
//
//@Entity
//@Table(name = "request_return_details")
//@Getter
//@Setter
//@NoArgsConstructor
//@AllArgsConstructor
//public class RequestReturnDetailEntity {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @ManyToOne
//    @JoinColumn(name="request_id")
//    private RequestReturnEntity requestReturn;
//
//    @OneToOne(fetch = FetchType.LAZY)
//    @MapsId
//    @JoinColumn(name = "assignment_detail_id")
//    private AssignmentDetailEntity assignmentDetail;
//}
