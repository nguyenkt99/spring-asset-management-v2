package com.nashtech.assetmanagement.dto;

import com.nashtech.assetmanagement.entity.CategoryEntity;
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

    public CategoryDTO(CategoryEntity entity){
        this.name = entity.getName();
        this.prefix = entity.getPrefix();
    }

    public CategoryEntity toEntity(){
        CategoryEntity entity = new CategoryEntity();
        entity.setName(this.name);
        entity.setPrefix(this.prefix);
        return entity;
    }
}
