package com.smallstudy.repo.study_repo;

import com.smallstudy.domain.study_entity.StudyCategoryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StudyCategoryItemRepository extends JpaRepository<StudyCategoryItem, Long> {


    @Query("SELECT a FROM StudyCategoryItem a " +
           "JOIN FETCH a.categoryItem b " +
           "WHERE a.study.id = :studyId")
    List<StudyCategoryItem> findByStudyIdWithCategoryItem(@Param("studyId") Long studyId);


    @Modifying
    @Query("DELETE FROM StudyCategoryItem sci " +
            "WHERE sci.study.id = :studyId")
    void deleteByStudyId(@Param("studyId") Long studyId);

}
