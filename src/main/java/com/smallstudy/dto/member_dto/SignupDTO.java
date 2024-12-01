package com.smallstudy.dto.member_dto;


import com.smallstudy.domain.member_entity.Member;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SignupDTO {

    private String nickname;
    private String username;
    private String password;
    private String emailToken;

    public Member maptoMember() {
        PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        return new Member(this.username,this.nickname, encoder.encode(this.password), this.emailToken);
    }


}
