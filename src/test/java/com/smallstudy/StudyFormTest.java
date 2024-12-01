package com.smallstudy;


import com.smallstudy.domain.member_entity.Member;
import com.smallstudy.domain.study_entity.Study;
import com.smallstudy.domain.study_form_entity.StudyForm;
import com.smallstudy.dto.study_dto.StudyDTO;
import com.smallstudy.dto.study_form_dto.StudyFormDTO;
import com.smallstudy.dto.study_form_dto.StudyFormQuestionDTO;
import com.smallstudy.dto.study_form_dto.StudyFormQuestionItemDTO;
import com.smallstudy.repo.member_repo.MemberRepository;
import com.smallstudy.security.CustomUser;
import com.smallstudy.service.StudyFormService;
import com.smallstudy.service.StudyService;
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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class StudyFormTest {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    private StudyService studyService;
    @Autowired
    private StudyFormService studyFormService;


    @Autowired
    private MockMvc mockMvc;
    /*
    * GET
    * /study/form 스터디 폼 생성 화면
    * /study/form/edit 생성된 스터디 폼 변경
    * /study/form/view 생성된 스터디 단순 보기 뷰
    *
    * POST
    * /study/form
    * /study/form/edit
    * /study/form/delete
    * */
    private static Long STUDY_ID;
    @BeforeAll
    static void signup(@Autowired MemberRepository memberRepository,
                       @Autowired StudyService studyService) throws IOException {

        Member member;

        if (!memberRepository.existsById(1L)) {
            PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
            member = new Member("test@test.com", "nickname", encoder.encode("random123!"));

            member.setEmailValid();
            memberRepository.save(member);
            SetSecuritySession(member);
        } else {

            Optional<Member> findMember = memberRepository.findById(1L);
            member = findMember.orElseThrow(EntityNotFoundException::new);
            SetSecuritySession(member);
        }

        StudyDTO studyDTO = new StudyDTO("title", "[{\"insert\":\"서비스: 보드게임 웹 서비스\"}]", LocalDate.now(), 15, 1L, Arrays.asList(1L, 2L, 3L));
        STUDY_ID = studyService.createStudy(member.getId(), studyDTO);

    }

    private static void SetSecuritySession(Member save) {
        UserDetails userDetails = new CustomUser(save);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @DisplayName("스터디 폼 생성 화면 GET")
    @Test
    @Transactional
    void 스터디_폼_생성_화면_GET() throws Exception {

        MockHttpSession session = new MockHttpSession();

        mockMvc.perform(get("/study/form").session(session)
                        .param("studyId", String.valueOf(STUDY_ID)))
                .andExpect(status().isOk())
                .andExpect(view().name("smallstudy/study_form/study_form"))
                .andExpect(model().attributeExists("form"));
    }

    @DisplayName("스터디 폼 생성")
    @Test
    @Transactional
    void 스터디_폼_생성() throws Exception {

        MockHttpSession session = new MockHttpSession();

        StudyFormDTO studyFormDTO = getStudyFormDTO();
        Long studyId = studyFormService.save(studyFormDTO);

        Study study = studyService.findStudyByStudyId(studyId);
        StudyForm studyForm = study.getStudyForm();

        Assertions.assertNotNull(studyForm);
        Assertions.assertEquals(studyFormDTO.getTitle(), studyForm.getTitle());
        Assertions.assertEquals(studyFormDTO.getDescription(), studyForm.getDescription());

        StudyFormDTO findForm = studyFormService.toStudyFormDTO(studyForm, studyId);

        equalStudyFormDTO(findForm, studyFormDTO);
    }

    @DisplayName("스터디 폼 변경")
    @Test
    @Transactional
    void 스터디_폼_변경() throws Exception {

        MockHttpSession session = new MockHttpSession();

        StudyFormDTO studyFormDTO = getStudyFormDTO();
        Long studyId = studyFormService.save(studyFormDTO);

        Study study = studyService.findStudyWithFilesAndStudyCategoryItemByStudyId(studyId);
        StudyForm studyForm = study.getStudyForm();

        StudyFormDTO newForm = getStudyFormDTO();
        newForm.setId(studyForm.getId());
        studyFormService.editForm(newForm);

        StudyForm editForm = studyFormService.findStudyFormById(studyForm.getId());
        StudyFormDTO findForm = studyFormService.toStudyFormDTO(editForm, studyId);

        equalStudyFormDTO(findForm, newForm);
    }


    @DisplayName("스터디 폼 삭제")
    @Test
    @Transactional
    void 스터디_폼_삭제() throws Exception {

        MockHttpSession session = new MockHttpSession();

        StudyFormDTO studyFormDTO = getStudyFormDTO();
        Long studyId = studyFormService.save(studyFormDTO);

        studyFormService.deleteForm(studyId);
        Study study = studyService.findStudyByStudyId(studyId);
        Assertions.assertNull(study.getStudyForm());
    }


    private StudyFormDTO getStudyFormDTO() {
        ArrayList<StudyFormQuestionDTO> list = new ArrayList<>();
        int random = (int) (Math.random() % 3) + 1;

        if(random % 2 == 0) {
            list.add(getStudyFormQuestionDTO());
        } else {
            list.add(getStudyFormQuestionDTOWithText());
        }
        return new StudyFormDTO(STUDY_ID, "FORM", "DESCRIPTION", list);
    }
    private StudyFormQuestionDTO getStudyFormQuestionDTO() {

        ArrayList<StudyFormQuestionItemDTO> list = new ArrayList<>();
        int random = (int) (Math.random() % 10) + 1;

        for(int i = 0; i < random; ++i) {
            list.add(new StudyFormQuestionItemDTO("item" + i));
        }

        String type;
        if(random%2 ==0)
            type = "RADIO";
        else
            type = "CHECKBOX";

        return new StudyFormQuestionDTO("title" + random, list, type);
    }

    private StudyFormQuestionDTO getStudyFormQuestionDTOWithText() {
        int random = (int) (Math.random() % 10) + 1;
        return new StudyFormQuestionDTO("title", List.of(new StudyFormQuestionItemDTO("text" + random)), "RADIO");
    }

    private void equalStudyFormDTO(StudyFormDTO findForm, StudyFormDTO studyFormDTO) {
        findForm.questions.forEach(findQuestion -> {
            Optional<StudyFormQuestionDTO> first = studyFormDTO.questions.stream()
                    .filter(question -> (question.getTitle() + question.getType().toLowerCase()).equals(findQuestion.getTitle() + findQuestion.getType().toLowerCase()))
                    .findFirst();

            Assertions.assertFalse(first.isEmpty(), "Question not found : " + findQuestion);
            StudyFormQuestionDTO studyFormQuestionDTO = first.get();

            List<StudyFormQuestionItemDTO> findItems = findQuestion.getItems();
            List<StudyFormQuestionItemDTO> items = studyFormQuestionDTO.getItems();

            findItems.forEach(findItem -> {
                boolean isOk = items.stream().anyMatch(item -> findItem.getContent().equals(item.getContent()));
                Assertions.assertTrue(isOk, "Question Item not found : " + findItem);
            });
        });
    }



}
