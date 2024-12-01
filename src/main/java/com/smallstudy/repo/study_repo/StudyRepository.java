package com.smallstudy.repo.study_repo;

import com.smallstudy.domain.study_entity.Study;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StudyRepository extends JpaRepository<Study, Long>, StudyRepositoryCustom {

    @Query(" SELECT s FROM Study s " +
            "JOIN FETCH s.member m " +
            "LEFT JOIN FETCH s.studyForm sf " +
            "WHERE s.id = :studyId")
    Optional<Study> findStudyWithMemberById(@Param("studyId") Long studyId);


    @Query("SELECT a FROM Study a " +
            "JOIN FETCH a.interestRegion b " +
            "JOIN FETCH a.member c " +
            "LEFT JOIN FETCH a.studyForm d " +
            "WHERE a.id = :id")
    Optional<Study> findStudyWithFilesAndStudyCategoryItemByStudyId(@Param("id") Long studyId);

    @Query("SELECT a FROM Study a " +
            "JOIN FETCH a.member b " +
            "LEFT JOIN FETCH a.studyForm c " +
            "WHERE c.id = :formId")
    Optional<Study> findStudyWithFormAndMemberByFormId(@Param("formId") Long formId);

    @Query("SELECT s FROM Study s " +
            "LEFT JOIN FETCH s.interestRegion a " +
            "JOIN FETCH s.member b " +
            "LEFT JOIN FETCH s.studyForm c " +
            "WHERE s.member.id = :memberId " +
            "ORDER BY (SELECT COUNT(a) FROM Application a " +
            "          WHERE a.study.id = s.id " +
            "          AND a.status = com.smallstudy.domain.member_entity.ApplicationStatus.APPLIED) DESC, s.lastModifiedDate desc ")
    List<Study> findStudiesByMemberIdOrderByApplicationStatusApplied(@Param("memberId") Long memberId, Pageable pageable);




    @Query("SELECT COUNT(s) FROM Study s WHERE s.member.id = :memberId")
    Long countStudyByMemberId(@Param("memberId") Long memberId);




}
