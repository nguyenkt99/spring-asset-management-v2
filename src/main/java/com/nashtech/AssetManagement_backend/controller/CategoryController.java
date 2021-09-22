package com.nashtech.AssetManagement_backend.controller;

import com.nashtech.AssetManagement_backend.dto.CategoryDTO;
import com.nashtech.AssetManagement_backend.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/category")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

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

}
