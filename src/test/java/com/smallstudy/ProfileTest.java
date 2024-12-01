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
import org.springframework.mock.web.MockMultipartFile;
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
public class ProfileTest {


    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MemberRepository memberRepository;

    @BeforeAll
    static void signup(@Autowired MemberRepository memberRepository) throws IOException {
        PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

        if(!memberRepository.existsById(1L)) {
            Member saveMember = new Member("test@test.com", "nickname", encoder.encode("random123!"));
            saveMember.setEmailValid();
            memberRepository.save(saveMember);
        }

        if(!memberRepository.existsById(2L)) {
            Member saveMember2 = new Member("test2@test.com", "nickname2", encoder.encode("random123!"));
            saveMember2.setEmailValid();
            memberRepository.save(saveMember2);
        }
    }

    private void setContext(Member member) {
        UserDetails userDetails = new CustomUser(member);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @DisplayName("프로파일 화면 GET 미인증")
    @Test
    void 프로파일_미인증() throws Exception {

        mockMvc.perform(get("/profile"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));

    }

    @DisplayName("프로파일 화면 GET 인증")
    @Test
    void 프로파일_인증() throws Exception {

        MockHttpSession session = new MockHttpSession();

        Optional<Member> findMember = memberRepository.findById(1L);
        Member member = findMember.orElseThrow(EntityNotFoundException::new);

        setContext(member);

        mockMvc.perform(get("/profile").session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("smallstudy/profile/profile_member"))
                .andExpect(model().attributeExists("form", "type"));
    }

    @DisplayName("프로파일 화면 수정 - 정상 처리")
    @Test
    void 프로파일_수정() throws Exception {

        MockHttpSession session = new MockHttpSession();

        Optional<Member> findMember = memberRepository.findById(1L);
        Member member = findMember.orElseThrow(EntityNotFoundException::new);

        setContext(member);

        MockMultipartFile mockFile = new MockMultipartFile(
                "profileImage",
                "logo.jpg",
                "image/jpg",
                "This is the file content".getBytes()
        );

        mockMvc.perform(multipart("/profile")
                .file(mockFile)
                .param("nickname", "admin2")
                .param("message", "halo")
                .with(csrf())
                .session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile"));
    }

    @DisplayName("프로파일 화면 수정 - 지원하지 않는 파일 타입")
    @Test
    void 프로파일_수정_지원하지_않는_파일타입() throws Exception {

        MockHttpSession session = new MockHttpSession();

        Optional<Member> findMember = memberRepository.findById(1L);
        Member member = findMember.orElseThrow(EntityNotFoundException::new);

        setContext(member);

        MockMultipartFile mockFile = new MockMultipartFile(
                "profileImage",
                "logo.svg",
                "image/svg",
                "This is the file content".getBytes()
        );

        mockMvc.perform(multipart("/profile")
                        .file(mockFile)
                        .param("nickname", "admin2")
                        .param("message", "halo")
                        .with(csrf())
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("image_type_error"));

    }


    @DisplayName("프로파일 화면 수정 - 확장자가 없는 파일")
    @Test
    void 프로파일_수정_확장자가_없는_파일() throws Exception {

        MockHttpSession session = new MockHttpSession();

        Optional<Member> findMember = memberRepository.findById(1L);
        Member member = findMember.orElseThrow(EntityNotFoundException::new);

        setContext(member);

        MockMultipartFile mockFile = new MockMultipartFile(
                "profileImage",
                "logo",
                "image/png",
                "This is the file content".getBytes()
        );

        mockMvc.perform(multipart("/profile")
                        .file(mockFile)
                        .param("nickname", "admin2")
                        .param("message", "halo")
                        .with(csrf())
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("image_type_error"));

    }


    @DisplayName("프로파일 화면 수정 - 저장 내용 없음")
    @Test
    void 프로파일_수정_저장_내용_없음() throws Exception {

        MockHttpSession session = new MockHttpSession();

        Optional<Member> findMember = memberRepository.findById(1L);
        Member member = findMember.orElseThrow(EntityNotFoundException::new);

        setContext(member);

        MockMultipartFile mockFile = new MockMultipartFile(
                "profileImage",
                "",
                "",
                new byte[0]
        );

        mockMvc.perform(multipart("/profile")
                        .file(mockFile)
                        .param("nickname", "nickname")
                        .param("message", "")
                        .with(csrf())
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("smallstudy/profile/profile_member"));

    }

    @DisplayName("프로파일 화면 수정 - 닉네임 4자 미만")
    @Test
    void 프로파일_수정_닉네임_4자_미만() throws Exception {

        MockHttpSession session = new MockHttpSession();

        Optional<Member> findMember = memberRepository.findById(1L);
        Member member = findMember.orElseThrow(EntityNotFoundException::new);

        setContext(member);

        MockMultipartFile mockFile = new MockMultipartFile(
                "profileImage",
                "",
                "",
                new byte[0]
        );

        mockMvc.perform(multipart("/profile")
                        .file(mockFile)
                        .param("nickname", "123")
                        .param("message", "")
                        .with(csrf())
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("smallstudy/profile/profile_member"))
                .andExpect(model().attributeHasFieldErrors("form","nickname"));

    }

    @DisplayName("프로파일 화면 수정 - 닉네임 12자 초과")
    @Test
    void 프로파일_수정_닉네임_12자_초과() throws Exception {

        MockHttpSession session = new MockHttpSession();

        Optional<Member> findMember = memberRepository.findById(1L);
        Member member = findMember.orElseThrow(EntityNotFoundException::new);

        setContext(member);

        MockMultipartFile mockFile = new MockMultipartFile(
                "profileImage",
                "",
                "",
                new byte[0]
        );

        mockMvc.perform(multipart("/profile")
                        .file(mockFile)
                        .param("nickname", "1234567890abc")
                        .param("message", "")
                        .with(csrf())
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("smallstudy/profile/profile_member"))
                .andExpect(model().attributeHasFieldErrors("form","nickname"));

    }

    @DisplayName("프로파일 화면 수정 - 메세지50자 초과")
    @Test
    void 프로파일_수정_메세지_50자_초과() throws Exception {

        MockHttpSession session = new MockHttpSession();

        Optional<Member> findMember = memberRepository.findById(1L);
        Member member = findMember.orElseThrow(EntityNotFoundException::new);

        setContext(member);

        MockMultipartFile mockFile = new MockMultipartFile(
                "profileImage",
                "",
                "",
                new byte[0]
        );

        StringBuilder b = new StringBuilder(51);
        for(int i = 0; i <= 50; ++i)
            b.append(i);

        mockMvc.perform(multipart("/profile")
                        .file(mockFile)
                        .param("nickname", "admin")
                        .param("message", b.toString())
                        .with(csrf())
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("smallstudy/profile/profile_member"))
                .andExpect(model().attributeHasFieldErrors("form","message"));

    }

    @DisplayName("프로파일 화면 수정 - 닉네임 중복")
    @Test
    void 프로파일_수정_닉네임_중복() throws Exception {

        MockHttpSession session = new MockHttpSession();

        Optional<Member> findMember = memberRepository.findById(1L);
        Member member = findMember.orElseThrow(EntityNotFoundException::new);

        setContext(member);
        MockMultipartFile mockFile = new MockMultipartFile(
                "profileImage",
                "",
                "",
                new byte[0]
        );

        mockMvc.perform(multipart("/profile")
                        .file(mockFile)
                        .param("nickname", "nickname2")
                        .param("message", "")
                        .with(csrf())
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("smallstudy/profile/profile_member"))
                .andExpect(model().attributeHasFieldErrors("form","nickname"));

    }





}
