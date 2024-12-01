package com.smallstudy.dto.profile_dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CategoryItemDTO {
    private Long code;
    private String text;

    public CategoryItemDTO(Long code, String categoryName, String itemName) {
        this.code = code;
        this.text = String.format("%s - %s", categoryName, itemName);
    }
}
