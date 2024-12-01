package com.smallstudy.controller.profile_controller;

import com.smallstudy.security.CustomUser;
import com.smallstudy.service.ApplicationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Slf4j
@RequiredArgsConstructor
public class ProfileApplicationController {

    private final ApplicationService applicationService;

    @GetMapping("/profile/apply")
    private String profileGet(@AuthenticationPrincipal CustomUser member,
                                   @PageableDefault(size = 7) Pageable pageable,
                                   Model model) {

        model.addAttribute("selected",pageable.getPageNumber() + 1);
        model.addAttribute("type", "apply");
        model.addAttribute("maxPage", pageable.getPageSize());
        model.addAttribute("list", applicationService.getApplicationsByMemberId(member.getId(), pageable));
        model.addAttribute("pageNumber", pageable.getPageNumber());

        return "smallstudy/profile/profile_apply";
    }


}
