package com.smallstudy;



import com.smallstudy.domain.member_entity.Member;
import com.smallstudy.repo.member_repo.MemberRepository;
import com.smallstudy.security.CustomUser;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Optional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class ProfileRegionTest {


    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MemberRepository memberRepository;

    /*
    * 등록 테스트
    * 중복 등록 시 예외 처리 테스트
    * 삭제 테스트
    * */

    @BeforeAll
    static void signup(@Autowired MemberRepository memberRepository) throws IOException {
        PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

        if(!memberRepository.existsById(1L)) {
            Member saveMember = new Member("test@test.com", "nickname", encoder.encode("random123!"));
            saveMember.setEmailValid();
            memberRepository.save(saveMember);
        }
    }

    private void setContext(Member member) {
        UserDetails userDetails = new CustomUser(member);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @DisplayName("프로파일 지역 화면 GET 미인증")
    @Test
    void 프로파일_미인증() throws Exception {

        mockMvc.perform(get("/profile/region"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));

    }

    @DisplayName("프로파일 관심지역 화면 GET 인증")
    @Test
    void 프로파일_인증() throws Exception {

        MockHttpSession session = new MockHttpSession();

        Optional<Member> findMember = memberRepository.findById(1L);
        Member member = findMember.orElseThrow(EntityNotFoundException::new);

        setContext(member);

        mockMvc.perform(get("/profile/region").session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("smallstudy/profile/profile_region"))
                .andExpect(model().attributeExists("region_form", "type"));
    }

    @DisplayName("프로파일 관심 지역 등록")
    @Test
    void 프로파일_관심_지역_등록() throws Exception {

        MockHttpSession session = new MockHttpSession();

        Optional<Member> findMember = memberRepository.findById(1L);
        Member member = findMember.orElseThrow(EntityNotFoundException::new);

        setContext(member);

        mockMvc.perform(post("/profile/region")
                        .param("code", "1")
                        .param("text", "qwe")
                        .with(csrf())
                        .session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile/region"));
    }

    @DisplayName("프로파일 관심 지역 중복 등록")
    @Test
    void 프로파일_관심_지역_중복_등록() throws Exception {

        MockHttpSession session = new MockHttpSession();

        Optional<Member> findMember = memberRepository.findById(1L);
        Member member = findMember.orElseThrow(EntityNotFoundException::new);

        setContext(member);

        mockMvc.perform(post("/profile/region")
                        .param("code", "2")
                        .param("text", "qwes")
                        .with(csrf())
                        .session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile/region"));

        mockMvc.perform(post("/profile/region")
                        .param("code", "2")
                        .param("text", "qwes")
                        .with(csrf())
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("smallstudy/profile/profile_region"))
                .andExpect(model().attributeHasFieldErrors("region_form", "code"));

    }

    @DisplayName("프로파일 관심 지역 삭제")
    @Test
    void 프로파일_관심_지역_삭제() throws Exception {

        MockHttpSession session = new MockHttpSession();

        Optional<Member> findMember = memberRepository.findById(1L);
        Member member = findMember.orElseThrow(EntityNotFoundException::new);

        setContext(member);

        mockMvc.perform(post("/profile/region")
                        .param("code", "3")
                        .param("text", "qwer")
                        .with(csrf())
                        .session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile/region"));

        mockMvc.perform(get("/profile/region-delete")
                        .param("code", "3")
                        .param("text", "qwer")
                        .with(csrf())
                        .session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile/region"));

    }

}
