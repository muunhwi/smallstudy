package com.smallstudy.repo.member_study_form_repo;

import com.smallstudy.domain.member_study_form_entity.MemberStudyForm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MemberStudyFormRepository extends JpaRepository<MemberStudyForm, Long> {



    @Query("SELECT a FROM MemberStudyForm a " +
            "JOIN FETCH a.member b " +
            "JOIN FETCH a.studyForm c " +
            "JOIN FETCH c.study d " +
            "WHERE b.id = :memberId AND c.id = :studyFormId")
    Optional<MemberStudyForm> findByMemberIdAndStudyFormId(@Param("memberId") Long memberId, @Param("studyFormId") Long studyFormId);

    @Modifying
    @Query("DELETE FROM MemberStudyForm a WHERE a.member.id = :memberId AND a.studyForm.id = :studyFormId")
    void deleteByMemberIdAndStudyFormId(@Param("memberId") Long memberId, @Param("studyFormId") Long studyFormId);

}
