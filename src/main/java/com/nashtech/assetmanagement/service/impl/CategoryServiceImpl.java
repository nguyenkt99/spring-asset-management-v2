package com.nashtech.assetmanagement.service.impl;

import com.nashtech.assetmanagement.dto.CategoryDTO;
import com.nashtech.assetmanagement.entity.CategoryEntity;
import com.nashtech.assetmanagement.exception.BadRequestException;
import com.nashtech.assetmanagement.exception.ConflictException;
import com.nashtech.assetmanagement.exception.ResourceNotFoundException;
import com.nashtech.assetmanagement.repository.CategoryRepository;
import com.nashtech.assetmanagement.service.CategoryService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Data
@RequiredArgsConstructor
@Service
public class CategoryServiceImpl implements CategoryService {
    private final String EXIST_PREFIX_CATEGORY_ERROR = "Prefix is already existed. Please enter a different prefix";

    private final String EXIST_NAME_CATEGORY_ERROR = " Category is already existed. " +
            "Please enter a different category ";

    private final CategoryRepository categoryRepo;

    @Override
    public List<CategoryDTO> showAll() {
        return categoryRepo.findAll().stream().map(CategoryDTO::new).collect(Collectors.toList());
    }

    @Override
    public CategoryDTO create(CategoryDTO dto) {
        if (categoryRepo.getByName(dto.getName()) != null)
            throw new ConflictException(EXIST_NAME_CATEGORY_ERROR);
        if (categoryRepo.getByPrefix(dto.getPrefix()) != null)
            throw new BadRequestException(EXIST_PREFIX_CATEGORY_ERROR);
        CategoryEntity cate = dto.toEntity();
        return new CategoryDTO(categoryRepo.save(cate));
    }

    @Override
    public CategoryDTO update(CategoryDTO dto) {
        CategoryEntity category = categoryRepo.findByPrefix(dto.getPrefix())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found!"));

        if (!dto.getName().equalsIgnoreCase(category.getName()) && categoryRepo.existsByName(dto.getName())) {
            throw new ConflictException("Category is exists!");
        }

        category.setName(dto.getName());
        return new CategoryDTO(categoryRepo.save(category));
    }

    @Override
    public void delete(String prefix) {
        CategoryEntity category = categoryRepo.findByPrefix(prefix)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found!"));
        if (category.getAssetEntities().size() > 0) {
            throw new ConflictException("Asset is available in category!");
        }

        categoryRepo.deleteById(prefix);
    }

    @Override
    public Integer getSumOfAvailableAssetByCategory(String prefix, String startDate, String endDate) {
        LocalDate date1 = LocalDate.parse(startDate);
        LocalDate date2 = LocalDate.parse(endDate);
        return categoryRepo.getSumOfAvailableAssetByCategory(prefix, date1, date2);
    }

}
