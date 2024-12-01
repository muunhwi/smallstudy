package com.smallstudy.controller.member_controller;


import com.smallstudy.dto.member_dto.LoginDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@Slf4j
public class LoginController {

    @GetMapping("/login")
    String loginGet(Model model) {

        if(!model.containsAttribute("form"))
            model.addAttribute("form", new LoginDTO());

        return "smallstudy/member/login";
    }

}
