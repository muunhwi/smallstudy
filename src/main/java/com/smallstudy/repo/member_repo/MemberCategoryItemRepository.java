package com.smallstudy.repo.member_repo;

import com.smallstudy.domain.member_entity.MemberCategoryItem;
import com.smallstudy.dto.profile_dto.CategoryItemDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MemberCategoryItemRepository extends JpaRepository<MemberCategoryItem, Long> {

    @Query("SELECT new com.smallstudy.dto.profile_dto.CategoryItemDTO(b.id, c.categoryName, b.categoryItemName) " +
            "FROM MemberCategoryItem a " +
            "JOIN a.categoryItem b " +
            "JOIN b.category c " +
            "JOIN a.member d " +
            "WHERE a.member.id = :memberId " +
            "ORDER BY b.id")
    List<CategoryItemDTO> findCategoryItemDTOsByMemberId(@Param("memberId") Long memberId);


    @Query("SELECT a " +
            "FROM MemberCategoryItem a " +
            "JOIN FETCH a.categoryItem b " +
            "JOIN FETCH b.category c " +
            "JOIN FETCH a.member d " +
            "WHERE a.member.id = :memberId")
    List<MemberCategoryItem> findMemberCategoryItems(@Param("memberId") Long memberId);

    @Modifying
    @Query("DELETE FROM MemberCategoryItem a " +
            "WHERE a.member.id = :memberId " +
            "AND a.categoryItem.id = :categoryItemId")
    void deleteByMemberIdAndCategoryItemId(@Param("memberId") Long memberId, @Param("categoryItemId") Long categoryItemId);

}
