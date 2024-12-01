package com.smallstudy.validator;


import com.smallstudy.domain.study_entity.Study;
import com.smallstudy.error.RuntimeAccessDeniedException;
import com.smallstudy.security.CustomUser;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.regex.Pattern;

@Slf4j
public class GlobalValidationService {

    private GlobalValidationService() {}

    public static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    public static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*])[A-Za-z\\d!@#$%^&*]{8,16}$");

    public static boolean emailValidate(String email) {
        return GlobalValidationService.EMAIL_PATTERN.matcher(email).matches();
    }

    public static boolean passwordValidate(String password) {
        return GlobalValidationService.PASSWORD_PATTERN.matcher(password).matches();
    }

    public static void isAuthor(Long authorId, Long accessId) throws RuntimeAccessDeniedException {

        if(Objects.isNull(authorId) || Objects.isNull(accessId)) {
            log.info("isAuthor call param error");
            throw new IllegalArgumentException();
        }

        if(!authorId.equals(accessId)) {
            log.info(" authorId : {} accessId : {}, [AccessDeniedException]", authorId, accessId);
            throw new RuntimeAccessDeniedException();
        }
    }


}
