package com.smallstudy.security;

import com.smallstudy.domain.member_entity.Member;
import com.smallstudy.repo.member_repo.MemberRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.util.Optional;

import static com.smallstudy.validator.GlobalValidationService.emailValidate;

@Service("userDetailService")
@RequiredArgsConstructor
@Slf4j
public class CustomMemberDetailService implements UserDetailsService     {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        if(!emailValidate(username))
            throw new UsernameNotFoundException(String.format("%s email not found", username));

        Optional<Member> findMember = memberRepository.findByEmail(username);
        Member member = findMember.orElseThrow(() -> new UsernameNotFoundException(String.format("%s email not found", username)));
        return new CustomUser(member);
    }

}
