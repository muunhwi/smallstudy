package com.smallstudy.advice;

import com.smallstudy.error.BadRequestException;
import com.smallstudy.error.RuntimeAccessDeniedException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalControllerAdvice {


    @ExceptionHandler({RuntimeAccessDeniedException.class,
            BadRequestException.class,
            EntityNotFoundException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleBadRequest(Model model) {
        model.addAttribute("message", "잘못된 요청입니다.");
        return "page_error/400";
    }


}