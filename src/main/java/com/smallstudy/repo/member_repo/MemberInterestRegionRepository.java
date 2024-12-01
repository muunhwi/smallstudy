package com.smallstudy.repo.member_repo;

import com.smallstudy.domain.member_entity.MemberInterestRegion;
import com.smallstudy.dto.profile_dto.RegionDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MemberInterestRegionRepository extends JpaRepository<MemberInterestRegion, Long> {

    @Query("SELECT new com.smallstudy.dto.profile_dto.RegionDTO(b.id, " +
            "CONCAT(CONCAT(b.region.city, ' ', b.region.gu), ' ', b.region.dong)) " +
            "FROM MemberInterestRegion a " +
            "JOIN a.interestRegion b " +
            "WHERE a.member.id = :memberId " +
            "ORDER BY b.id")
    List<RegionDTO> findInterestRegionsByMemberId(@Param("memberId") Long memberId);


    @Query("SELECT a " +
            "FROM MemberInterestRegion a " +
            "JOIN FETCH a.member b " +
            "JOIN FETCH a.interestRegion c " +
            "WHERE b.id = :memberId")
    List<MemberInterestRegion> findInterestRegionsWithMemberByMemberId(@Param("memberId") Long memberId);


    @Modifying
    @Query("DELETE FROM MemberInterestRegion a " +
            "WHERE a.member.id = :memberId " +
            "AND a.interestRegion.id = :regionId")
    void deleteByMemberIdAndRegionId(@Param("memberId") Long memberId, @Param("regionId") Long regionId);


}
