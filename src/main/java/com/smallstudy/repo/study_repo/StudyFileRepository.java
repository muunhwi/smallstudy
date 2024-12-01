package com.smallstudy.repo.study_repo;

import com.smallstudy.domain.study_entity.StudyFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StudyFileRepository extends JpaRepository<StudyFile, Long> {

    @Modifying
    @Query("DELETE FROM StudyFile sf " +
            "WHERE sf.study.id = :studyId")
    void deleteFilesByStudyId(@Param("studyId") Long studyId);


}
