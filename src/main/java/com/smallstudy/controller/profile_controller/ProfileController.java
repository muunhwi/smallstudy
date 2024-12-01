package com.smallstudy.controller.profile_controller;

import com.smallstudy.domain.member_entity.Member;
import com.smallstudy.dto.profile_dto.ProfileDTO;
import com.smallstudy.error.UnSupportedImageFileExtensionException;
import com.smallstudy.error.UnSupportedImageFileTypeException;
import com.smallstudy.security.CustomUser;
import com.smallstudy.service.MemberService;
import com.smallstudy.validator.ProfileValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.io.IOException;
import java.util.Locale;
import java.util.Objects;


@Controller
@Slf4j
@RequiredArgsConstructor
public class ProfileController {

    private final MemberService memberService;
    private final ProfileValidator profileValidator;
    private final MessageSource messageSource;

    @InitBinder("form")
    public void init(WebDataBinder binder) {
        binder.addValidators(profileValidator);
    }

    @GetMapping("/profile")
    String profileGet(@AuthenticationPrincipal CustomUser member,
                      Model model) {
        model.addAttribute("type", "profile");
        model.addAttribute("form", new ProfileDTO(member.getUsername(), member.getNickname(), member.getMessage(), null));
        return "smallstudy/profile/profile_member";
    }

    @PostMapping("/profile")
    String profilePost(@Valid @ModelAttribute("form") ProfileDTO dto,
                       BindingResult errors,
                       Model model) throws IOException {

        if (errors.hasErrors()) {
            model.addAttribute("type", "profile");
            return "smallstudy/profile/profile_member";
        }

        SecurityContext context = SecurityContextHolder.getContext();
        CustomUser user = (CustomUser) context.getAuthentication().getPrincipal();

        if (dto.profileImage.isEmpty() &&
            dto.nickname.equals(Objects.toString(user.getNickname(), "")) &&
            dto.message.equals(Objects.toString(user.getMessage(), ""))) {

            model.addAttribute("type", "profile");
            return "smallstudy/profile/profile_member";
        }

        if (!user.getNickname().equals(dto.nickname) &&
                memberService.duplicatedNickname(dto.getNickname())) {

            model.addAttribute("type", "profile");
            errors.rejectValue("nickname", "duplicated");
            return "smallstudy/profile/profile_member";
        }

        dto.username = user.getUsername();
        RequestContextHolder.currentRequestAttributes().setAttribute("dto", dto, RequestAttributes.SCOPE_REQUEST);
        Member member = memberService.updateMemberProfile(dto);

        CustomUser updatedUser = new CustomUser(member);
        Authentication updatedAuth = new UsernamePasswordAuthenticationToken(updatedUser, null, updatedUser.getAuthorities());
        context.setAuthentication(updatedAuth);

        return "redirect:/profile";
    }

    @ExceptionHandler({UnSupportedImageFileTypeException.class, UnSupportedImageFileExtensionException.class})
    public String handleUnSupportedImageFileTypeException(@RequestAttribute("dto") ProfileDTO dto,
                                                          Model model) {

        model.addAttribute("type", "profile");
        model.addAttribute("form", dto);
        model.addAttribute("image_type_error",  messageSource.getMessage("unsupported.image", null, Locale.getDefault()));
        return "smallstudy/profile/profile_member";
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String handleMaxUploadSizeExceededException(@AuthenticationPrincipal CustomUser member,
                                                               Model model) throws IOException {
        model.addAttribute("type", "profile");
        model.addAttribute("form", new ProfileDTO(member.getUsername(), member.getNickname(), member.getMessage(), null));
        model.addAttribute("image_type_error",  messageSource.getMessage("unsupported.file.size", null, Locale.getDefault()));
        return "smallstudy/profile/profile_member";
    }

}
