package com.smallstudy.repo.category_repo;

import com.smallstudy.domain.category_entity.CategoryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CategoryItemRepository extends JpaRepository<CategoryItem, Long> {
    @Query("SELECT a FROM CategoryItem a JOIN FETCH a.category b WHERE b.id = a.category.id")
    List<CategoryItem> findAllCategoryItemsWithCategory();

    @Query("SELECT a FROM CategoryItem a WHERE a.id IN :ids")
    List<CategoryItem> findCategoryItemByIds(@Param("ids") List<Long> ids);
}
