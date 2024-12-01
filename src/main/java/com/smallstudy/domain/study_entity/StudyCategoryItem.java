package com.smallstudy.domain.study_entity;


import com.smallstudy.domain.category_entity.CategoryItem;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class StudyCategoryItem {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_id")
    @Setter
    private Study study;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_item_id")
    @Setter
    private CategoryItem categoryItem;

    public StudyCategoryItem(Study study, CategoryItem categoryItem) {
        this.study = study;
        this.categoryItem = categoryItem;
    }

    public static List<Long> convertToLongList(List<StudyCategoryItem> studyCategoryItems) {
        return studyCategoryItems.stream()
                .map(StudyCategoryItem::getCategoryItem)
                .map(CategoryItem::getId)
                .toList();
    }
}
