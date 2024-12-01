package com.smallstudy.repo.category_repo;

import com.smallstudy.domain.category_entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
