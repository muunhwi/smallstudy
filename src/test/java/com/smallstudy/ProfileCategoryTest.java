package com.smallstudy;
import com.smallstudy.domain.member_entity.Member;
import com.smallstudy.repo.member_repo.MemberRepository;
import com.smallstudy.security.CustomUser;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
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
public class ProfileCategoryTest {


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

    @DisplayName("프로파일 관심 분야 GET 미인증")
    @Test
    void 프로파일_미인증() throws Exception {

        mockMvc.perform(get("/profile/category"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));

    }

    @DisplayName("프로파일 관심 분야 화면 GET 인증")
    @Test
    void 프로파일_인증() throws Exception {

        MockHttpSession session = new MockHttpSession();

        Optional<Member> findMember = memberRepository.findById(1L);
        Member member = findMember.orElseThrow(EntityNotFoundException::new);

        setContext(member);

        mockMvc.perform(get("/profile/category").session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("smallstudy/profile/profile_category"))
                .andExpect(model().attributeExists("category_form", "type"));
    }



    @DisplayName("프로파일 관심 분야 등록")
    @Test
    void 프로파일_관심_분야_등록() throws Exception {

        MockHttpSession session = new MockHttpSession();

        Optional<Member> findMember = memberRepository.findById(1L);
        Member member = findMember.orElseThrow(EntityNotFoundException::new);

        setContext(member);

        mockMvc.perform(post("/profile/category")
                        .param("code", "1")
                        .param("text", "qwe")
                        .with(csrf())
                        .session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile/category"));
    }

    @DisplayName("프로파일 관심 분야 중복 등록")
    @Test
    void 프로파일_관심_분야_중복_등록() throws Exception {

        MockHttpSession session = new MockHttpSession();

        Optional<Member> findMember = memberRepository.findById(1L);
        Member member = findMember.orElseThrow(EntityNotFoundException::new);

        setContext(member);

        mockMvc.perform(post("/profile/category")
                        .param("code", "2")
                        .param("text", "qwe")
                        .with(csrf())
                        .session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile/category"));

        mockMvc.perform(post("/profile/category")
                        .param("code", "2")
                        .param("text", "qwe")
                        .with(csrf())
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("smallstudy/profile/profile_category"))
                .andExpect(model().attributeHasFieldErrors("category_form", "code"));

    }

    @DisplayName("프로파일 관심 분야 삭제")
    @Test
    void 프로파일_관심_분야_삭제() throws Exception {

        MockHttpSession session = new MockHttpSession();

        Optional<Member> findMember = memberRepository.findById(1L);
        Member member = findMember.orElseThrow(EntityNotFoundException::new);

        setContext(member);

        mockMvc.perform(post("/profile/category")
                        .param("code", "3")
                        .param("text", "qwe")
                        .with(csrf())
                        .session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile/category"));

        mockMvc.perform(get("/profile/category-delete")
                        .param("code", "3")
                        .param("text", "qwe")
                        .with(csrf())
                        .session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile/category"));

    }

}
