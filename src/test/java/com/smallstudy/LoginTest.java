package com.smallstudy;

import com.smallstudy.domain.member_entity.Member;
import com.smallstudy.repo.member_repo.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;



@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class LoginTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MemberRepository memberRepository;


    @BeforeAll
    static void signup(@Autowired MemberRepository memberRepository) {
        PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

        if(!memberRepository.existsById(1L)) {
            Member saveMember = new Member("test@test.com", "nickname", encoder.encode("random123!"));
            saveMember.setEmailValid();
            memberRepository.save(saveMember);
        }
    }

    @DisplayName("로그인 - 성공")
    @Test
    void 성공로그인() throws Exception {
        mockMvc.perform(post("/login")
                        .param("username", "test@test.com")
                        .param("password", "random123!")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    @DisplayName("로그인 - 이메일이 유효하지 않음")
    @Test
    void 이메일_유효하지않음() throws Exception {
        mockMvc.perform(post("/login")
                .param("username", "test@google.com")
                .param("password", "dwc02207!")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error=true"));

    }

    @DisplayName("로그인 - 비밀번호가 유효하지 않음")
    @Test
    void 비밀번호_유효하지않음() throws Exception {
        mockMvc.perform(post("/login")
                        .param("username", "test@google.com")
                        .param("password", "test01")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error=true"));
    }

    @DisplayName("로그인 - 이메일이 존재하지 않음")
    @Test
    void 이메일_존재하지않음() throws Exception {
        mockMvc.perform(post("/login")
                        .param("username", "test@gmail.com")
                        .param("password", "dwc02207!")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error=true"));
    }

    @DisplayName("로그인 - 비밀번호가 존재하지 않음")
    @Test
    void 비밀번호_존재하지않음() throws Exception {
        mockMvc.perform(post("/login")
                        .param("username", "test@google.com")
                        .param("password", "test12301!")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error=true"));
    }


}
