package com.nashtech.AssetManagement_backend.service;

import com.nashtech.AssetManagement_backend.dto.CategoryDTO;

import java.util.Date;
import java.util.List;

public interface CategoryService {
    List<CategoryDTO> showAll();
    CategoryDTO create(CategoryDTO dto);
    CategoryDTO update(CategoryDTO dto);
    void delete(String prefix);
    Integer getSumOfAvailableAssetByCategory(String prefix, String startDate, String endDate);
}
