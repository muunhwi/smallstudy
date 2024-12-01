package com.smallstudy.controller.member_controller;



import com.smallstudy.dto.member_dto.LoginDTO;
import com.smallstudy.dto.member_dto.SignupDTO;

import com.smallstudy.dto.member_dto.EmailSendErrorDTO;
import com.smallstudy.service.MemberService;
import com.smallstudy.validator.GlobalValidationService;
import com.smallstudy.validator.SignupValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Locale;

@Controller
@RequiredArgsConstructor
@Slf4j
public class SignupController {

    private final MemberService memberService;
    private final SignupValidator signupValidator;
    private final MessageSource messageSource;

    @InitBinder
    public void init(WebDataBinder binder) {
        binder.addValidators(signupValidator);
    }

    @GetMapping("/signup")
    String signupGet(Model model) {
        model.addAttribute("form", new SignupDTO());
        return "smallstudy/member/signup";
    }

    @PostMapping("/signup")
    String signupPost(@Valid @ModelAttribute("form") SignupDTO dto, BindingResult errors, Model model, RedirectAttributes redirect)
    {
        if(errors.hasErrors()) {
            model.addAttribute("form", dto);
            return "smallstudy/member/signup";
        }

        if(memberService.duplicatedEmail(dto.getUsername())) {
            model.addAttribute("form", dto);
            errors.rejectValue("username", "duplicated");
            return "smallstudy/member/signup";
        }

        if(memberService.duplicatedNickname(dto.getNickname())) {
            model.addAttribute("form", dto);
            errors.rejectValue("nickname", "duplicated");
            return "smallstudy/member/signup";
        }

        if(memberService.emailValidAndSignup(dto.maptoMember())) {
            model.addAttribute("form", dto);
            errors.rejectValue("emailToken", "invalid");
            return "smallstudy/member/signup";
        }

        redirect.addFlashAttribute("form", new LoginDTO(dto.getUsername(), ""));
        return "redirect:/login";
    }


    @PostMapping("/email-token")
    @ResponseBody
    EmailSendErrorDTO emailCheckPost(@ModelAttribute("form") SignupDTO dto, BindingResult errors) {

        String email = dto.getUsername();
        boolean emailValidate = GlobalValidationService.emailValidate(email);

        if(!emailValidate)
        {
            String msg = messageSource.getMessage("invalid.form.username", null, Locale.getDefault());
            return new EmailSendErrorDTO(100, msg);
        }

        if(memberService.duplicatedEmail(email)) {
            String msg = messageSource.getMessage("duplicated.form.username", null, Locale.getDefault());
            return new EmailSendErrorDTO(101, msg);
        }

        if(!memberService.canResendEmailToken(email)) {
            String msg = messageSource.getMessage("after.email.token", null, Locale.getDefault());
            return new EmailSendErrorDTO(102, msg);
        }

        memberService.sendEmailTokenAndTemporarySave(dto.getUsername());
        return new EmailSendErrorDTO(0, "이메일로 토큰이 전송 되었습니다. 일정 시간 동안 메일이 발송 되지 않았다면 다시 요청해주세요.");
    }
}
