package com.nashtech.assetmanagement.service;

import com.nashtech.assetmanagement.dto.CategoryDTO;

import java.util.List;

public interface CategoryService {
    List<CategoryDTO> showAll();
    CategoryDTO create(CategoryDTO dto);
    CategoryDTO update(CategoryDTO dto);
    void delete(String prefix);
    Integer getSumOfAvailableAssetByCategory(String prefix, String startDate, String endDate);
}
