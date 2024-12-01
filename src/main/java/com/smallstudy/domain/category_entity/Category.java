package com.smallstudy.domain.category_entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Category {

    @Id @GeneratedValue
    Long id;
    String categoryName;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "category", cascade = CascadeType.ALL)
    List<CategoryItem> categoryItems = new ArrayList<>();

    public Category(String categoryName) {
        this.categoryName = categoryName;
    }

    public void setCategoryItem(CategoryItem categoryItem) {
        categoryItem.setCategory(this);
        categoryItems.add(categoryItem);
    }

}
