package com.smallstudy.service;


import com.smallstudy.domain.category_entity.CategoryItem;
import com.smallstudy.domain.member_entity.Member;
import com.smallstudy.domain.member_entity.MemberCategoryItem;
import com.smallstudy.dto.profile_dto.CategoryItemDTO;
import com.smallstudy.repo.category_repo.CategoryItemRepository;
import com.smallstudy.repo.member_repo.MemberCategoryItemRepository;
import com.smallstudy.repo.member_repo.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
@Service
@Slf4j
@RequiredArgsConstructor
public class CategoryItemService {

    private final CategoryItemRepository categoryItemRepository;
    private final MemberCategoryItemRepository memberCategoryItemRepository;
    private final MemberRepository memberRepository;

    public static CategoryItemDTO toCategoryItemDTO(CategoryItem item) {
        return new CategoryItemDTO(item.getId(), item.getCategory().getCategoryName(), item.getCategoryItemName());
    }

    public static List<CategoryItemDTO> toCategoryItemDTOs(List<CategoryItem> studyCategoryItems) {
        return studyCategoryItems.stream()
                .map(CategoryItemService::toCategoryItemDTO)
                .toList();
    }

    public static List<CategoryItemDTO> filteredCategoryDTOs(List<CategoryItemDTO> regions, List<CategoryItemDTO> sub) {
        return regions.stream()
                .filter(category -> sub.stream()
                        .noneMatch(selected -> selected.getCode().equals(category.getCode())))
                .sorted(Comparator.comparingLong(CategoryItemDTO::getCode))
                .toList();
    }

    public List<CategoryItemDTO> getCategoryItemDTOs() {
        return categoryItemRepository.findAllCategoryItemsWithCategory()
                .stream()
                .map(CategoryItemService::toCategoryItemDTO)
                .toList();
    }

    public List<CategoryItemDTO> findCategoryItemDTOsByMemberId(Long memberId) {
        return memberCategoryItemRepository.findCategoryItemDTOsByMemberId(memberId);
    }


    @Transactional
    public List<CategoryItemDTO> saveMemberCategoryItem(Long memberId, Long categoryCode) {
        List<MemberCategoryItem> memberCategoryItems = memberCategoryItemRepository.findMemberCategoryItems(memberId);

        for (MemberCategoryItem memberCategoryItem : memberCategoryItems) {
            if (categoryCode.equals(memberCategoryItem.getCategoryItem().getId())) {
                return memberCategoryItems.stream()
                        .map(mci -> toCategoryItemDTO(mci.getCategoryItem()))
                        .sorted(Comparator.comparing(CategoryItemDTO::getCode))
                        .toList();
            }
        }

        Member member = memberCategoryItems.isEmpty() ?
                memberRepository.findById(memberId).orElseThrow(() -> new EntityNotFoundException(
                                String.format("Member [ID:%d] Not Found", memberId)))
                : memberCategoryItems.get(0).getMember();

        CategoryItem categoryItem = categoryItemRepository.findById(categoryCode)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("CategoryItem [ID:%d] Not Found", categoryCode)));

        MemberCategoryItem newMemberCategoryItem = new MemberCategoryItem(member, categoryItem);
        memberCategoryItemRepository.save(newMemberCategoryItem);

        return List.of();
    }

    @Transactional
    public void deleteByMemberIdAndCategoryItemId(Long memberId, Long categoryCode) {
        memberCategoryItemRepository.deleteByMemberIdAndCategoryItemId(memberId, categoryCode);
    }






}
