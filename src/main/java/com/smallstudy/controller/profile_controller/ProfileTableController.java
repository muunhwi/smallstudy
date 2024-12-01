package com.smallstudy.controller.profile_controller;

import com.smallstudy.domain.study_entity.Study;
import com.smallstudy.dto.profile_dto.ProfileTableDTO;
import com.smallstudy.security.CustomUser;
import com.smallstudy.service.StudyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.nio.file.AccessDeniedException;

@Controller
@Slf4j
@RequiredArgsConstructor
public class ProfileTableController {

    private final StudyService studyService;


    @GetMapping("/profile/table")
    private String profileTableGet(@AuthenticationPrincipal CustomUser member,
                      @PageableDefault(size = 4) Pageable pageable,
                      Model model) {

        model.addAttribute("selected",pageable.getPageNumber() + 1);
        model.addAttribute("type", "table");
        model.addAttribute("maxPage", pageable.getPageSize());
        model.addAttribute("list", studyService.findStudiesWithPaginationByMemberId(member.getId(), pageable));
        model.addAttribute("pageNumber", pageable.getPageNumber());

        return "smallstudy/profile/profile_table";
    }

    @GetMapping("/profile/study/{studyId}/delete")
    public String studyDeletePostWithId(@PathVariable("studyId") Long studyId,
                                        @AuthenticationPrincipal CustomUser member) throws AccessDeniedException {
        Study study = studyService.findStudyByStudyId(studyId);
        studyService.deleteStudy(studyId);
        return "redirect:/profile/table";
    }


}
