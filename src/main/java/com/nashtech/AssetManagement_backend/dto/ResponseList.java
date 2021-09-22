package com.nashtech.AssetManagement_backend.dto;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ResponseList {
    private int totalItems;
    private Object resultList;
    private int currentPage;
    private int pageSize;
    private int totalPage;

}
