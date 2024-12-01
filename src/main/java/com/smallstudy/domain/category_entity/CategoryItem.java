package com.smallstudy.domain.category_entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryItem {

    @Id
    @GeneratedValue
    Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    @Setter
    Category category;
    String categoryItemName;

    public CategoryItem(Category category, String categoryItemName) {
        this.category = category;
        this.categoryItemName = categoryItemName;
    }
}
