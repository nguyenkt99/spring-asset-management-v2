package com.nashtech.assetmanagement.controller;

import com.nashtech.assetmanagement.dto.CategoryDTO;
import com.nashtech.assetmanagement.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/category")
public class CategoryController {
    @Autowired
    CategoryService categoryService;

    @GetMapping()
    public ResponseEntity<List<CategoryDTO>> showAll(){
        return ResponseEntity.ok().body(categoryService.showAll());
    }

    @PostMapping()
    public ResponseEntity<CategoryDTO> create(@Valid @RequestBody CategoryDTO dto){
        return ResponseEntity.ok().body(categoryService.create(dto));
    }

    @PutMapping("/{prefix}")
    public CategoryDTO editCategory(@PathVariable String prefix, @RequestBody CategoryDTO categoryDTO) {
        categoryDTO.setPrefix(prefix);
        return categoryService.update(categoryDTO);
    }

    @DeleteMapping("/{prefix}")
    public void deleteCategory(@PathVariable String prefix) {
        categoryService.delete(prefix);
    }

    @GetMapping("/{prefix}")
    public Integer getSumOfAvailableAssetByCategory(@PathVariable String prefix, @RequestParam("startDate") String startDate, @RequestParam("endDate") String endDate) {
        return categoryService.getSumOfAvailableAssetByCategory(prefix, startDate, endDate);
    }


}
