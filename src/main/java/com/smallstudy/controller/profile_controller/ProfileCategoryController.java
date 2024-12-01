package com.smallstudy.controller.profile_controller;

import com.smallstudy.dto.profile_dto.CategoryItemDTO;
import com.smallstudy.security.CustomUser;
import com.smallstudy.service.CategoryItemService;
import com.smallstudy.utils.MyLocalCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
public class ProfileCategoryController {

    private final CategoryItemService categoryItemService;

    @GetMapping("/profile/category")
    String categoryGet(@AuthenticationPrincipal CustomUser member, Model model) {

        List<CategoryItemDTO> result = categoryItemService.findCategoryItemDTOsByMemberId(member.getId());
        bindDataToModel(model, result, new CategoryItemDTO());

        return "smallstudy/profile/profile_category";
    }

    @PostMapping("/profile/category")
    String categoryPost(@ModelAttribute("category_form") CategoryItemDTO dto,
                        BindingResult bindingResult,
                        @AuthenticationPrincipal CustomUser member,
                        Model model) {

        List<CategoryItemDTO> result = categoryItemService.saveMemberCategoryItem(member.getId(), dto.getCode());

        if(!result.isEmpty()) {
            bindDataToModel(model, result, dto);
            bindingResult.rejectValue("code", "duplicated");
            return "smallstudy/profile/profile_category";
        }

        return "redirect:/profile/category";
    }

    @GetMapping("/profile/category-delete")
    String categoryDelete(@RequestParam("code") Long categoryItemCode,
                          @AuthenticationPrincipal CustomUser member) {
        categoryItemService.deleteByMemberIdAndCategoryItemId(member.getId(), categoryItemCode);
        return "redirect:/profile/category";
    }

    private void bindDataToModel(Model model, List<CategoryItemDTO> selectedCategoryItem, CategoryItemDTO dto) {
        model.addAttribute("selectedList", selectedCategoryItem);
        model.addAttribute("list", MyLocalCache.categoryItems);
        model.addAttribute("type", "category");
        model.addAttribute("category_form", dto);
    }

}
