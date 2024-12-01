package com.smallstudy.repo.study_repo;

import com.smallstudy.domain.study_form_entity.StudyForm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface StudyFormRepository extends JpaRepository<StudyForm, Long> {

    @Query("SELECT a FROM StudyForm a WHERE a.id = :id")
    Optional<StudyForm> findStudyFormById(@Param("id") Long id);


}
