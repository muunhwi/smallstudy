package com.smallstudy.repo;

import com.smallstudy.App;
import com.smallstudy.domain.member_entity.Application;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ApplicationRepository extends JpaRepository<Application, Long> {

    @Query("SELECT COUNT(a) FROM Application a " +
            "WHERE a.member.id = :memberId AND a.study.id = :studyId")
    Long countByMemberIdAndStudyId(@Param("memberId") Long memberId, @Param("studyId") Long studyId);


    @Query("SELECT a FROM Application a " +
           "JOIN FETCH a.study b " +
           "JOIN FETCH a.member c " +
           "LEFT JOIN FETCH b.studyForm d " +
           "WHERE a.member.id = :memberId AND a.study.id = :studyId")
    Optional<Application> findApplicationByMemberIdAndStudyId(@Param("memberId") Long memberId, @Param("studyId") Long studyId);


    @Query("SELECT a FROM Application a " +
            "JOIN FETCH a.study b " +
            "LEFT JOIN FETCH b.studyForm c " +
            "WHERE a.member.id = :memberId " +
            "ORDER BY a.lastModifiedDate DESC")
    List<Application> findApplicationByMemberIdOrderByLastModifiedDate(@Param("memberId") Long memberId, Pageable pageable);

    @Query("SELECT COUNT(a) FROM Application a WHERE a.member.id = :memberId")
    Long countApplicationByMemberId(@Param("memberId") Long memberId);

}
