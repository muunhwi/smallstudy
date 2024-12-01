package com.smallstudy.validator;

import com.smallstudy.dto.member_dto.SignupDTO;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import static com.smallstudy.validator.GlobalValidationService.emailValidate;
import static com.smallstudy.validator.GlobalValidationService.passwordValidate;

@Component
public class SignupValidator implements Validator {


    @Override
    public boolean supports(Class<?> clazz) {
        return SignupDTO.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {

        SignupDTO dto = (SignupDTO) target;

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "username", "required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "nickname", "required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "emailToken", "required");
        if(errors.hasErrors()) return;

        if(!emailValidate(dto.getUsername()))
            errors.rejectValue("username", "invalid");

        if(!passwordValidate(dto.getPassword()))
            errors.rejectValue("password", "invalid");

        int length = dto.getNickname().length();
        if(length < 4 || length > 12)
            errors.rejectValue("nickname", "invalid");

    }



}
