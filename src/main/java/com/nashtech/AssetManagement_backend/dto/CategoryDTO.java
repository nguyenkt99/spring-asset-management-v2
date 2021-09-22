package com.nashtech.AssetManagement_backend.dto;

import com.nashtech.AssetManagement_backend.entity.CategoryEntity;
import lombok.*;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDTO {
    @Length(min = 1, max = 255, message = "length 1-255")
    private String name;
    @Length(min = 2, max = 2, message = "length is 2")
    private String prefix;

    public static CategoryDTO toDTO(CategoryEntity cate){
        if (cate == null)
            return null;
        CategoryDTO dto = new CategoryDTO();
        dto.setName(cate.getName());
        dto.setPrefix(cate.getPrefix());
        return dto;
    }

    public static CategoryEntity toEntity(CategoryDTO dto){
        if (dto == null)
            return null;
        CategoryEntity cate = new CategoryEntity();
        cate.setName(dto.getName());
        cate.setPrefix(dto.getPrefix());
        return cate;
    }
}
