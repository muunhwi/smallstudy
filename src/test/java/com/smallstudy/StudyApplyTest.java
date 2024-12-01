package com.smallstudy;

import static com.smallstudy.domain.member_entity.ApplicationStatus.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;

import com.smallstudy.domain.member_entity.Application;
import com.smallstudy.domain.member_entity.Member;
import com.smallstudy.domain.member_study_form_entity.MemberStudyForm;
import com.smallstudy.domain.study_entity.Study;
import com.smallstudy.domain.study_form_entity.QuestionType;
import com.smallstudy.domain.study_form_entity.StudyForm;
import com.smallstudy.domain.study_form_entity.StudyFormQuestion;
import com.smallstudy.domain.study_form_entity.StudyFormQuestionItem;
import com.smallstudy.dto.member_study_form_dto.MemberAnswerDTO;
import com.smallstudy.dto.member_study_form_dto.MemberStudyFormDTO;
import com.smallstudy.dto.study_dto.StudyDTO;
import com.smallstudy.dto.study_form_dto.StudyFormDTO;
import com.smallstudy.dto.study_form_dto.StudyFormQuestionDTO;
import com.smallstudy.dto.study_form_dto.StudyFormQuestionItemDTO;
import com.smallstudy.repo.ApplicationRepository;
import com.smallstudy.repo.member_repo.MemberRepository;
import com.smallstudy.repo.member_study_form_repo.MemberStudyFormRepository;
import com.smallstudy.security.CustomUser;
import com.smallstudy.service.ApplicationService;
import com.smallstudy.service.MemberStudyFormService;
import com.smallstudy.service.StudyFormService;
import com.smallstudy.service.StudyService;
import com.smallstudy.utils.JsonConverter;
import com.smallstudy.validator.StudyValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
public class StudyApplyTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private StudyFormService studyFormService;
    @Autowired
    private StudyService studyService;
    @Autowired
    private StudyValidator studyValidator;
    @Autowired
    private MemberStudyFormService memberStudyFormService;
    @Autowired
    private ApplicationService applicationService;
    @Autowired
    private ApplicationRepository applicationRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private MemberStudyFormRepository memberStudyFormRepository;
    /*
    * GET /study/apply
    * GET /study/apply/no-form
    * GET /study/apply/view
    * GET /study/apply/view/readonly
    * GET /study/applicant/approve
    * GET /study/applicant/reject
    *
    * POST /study/apply
    * */

    private static Long STUDY_WITH_FORM_ID = 0L;
    private static Long STUDY_NO_FORM_ID = 0L;
    private static Long FORM_ID = 0L;

    @BeforeAll
    static void signup(@Autowired MemberRepository memberRepository,
                       @Autowired StudyService studyService,
                       @Autowired StudyFormService studyFormService
                       ) throws IOException {

        Member member;
        PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

        if (!memberRepository.existsById(1L)) {
            member = new Member("test@test.com", "nickname", encoder.encode("random123!"));
            member.setEmailValid();
            memberRepository.save(member);
        } else {
            Optional<Member> findMember = memberRepository.findById(1L);
            member = findMember.orElseThrow(EntityNotFoundException::new);
        }

        if(!memberRepository.existsById(2L)) {
            Member saveMember2 = new Member("test2@test.com", "nickname2", encoder.encode("random123!"));
            saveMember2.setEmailValid();
            memberRepository.save(saveMember2);
        }


        if(STUDY_WITH_FORM_ID == 0L) {
            StudyDTO dto = new StudyDTO("title2", "[{\"insert\":\"서비스: 보드게임 웹 서비스\"}]", LocalDate.now(), 15, 1L, Arrays.asList(1L, 2L, 3L));
            STUDY_WITH_FORM_ID = studyService.createStudy(member.getId(), dto);

            StudyFormDTO studyFormDTO = getStudyFormDTO(STUDY_WITH_FORM_ID);
            studyFormService.save(studyFormDTO);
            Study study = studyService.findStudyByStudyId(STUDY_WITH_FORM_ID);

            if(FORM_ID == 0L) {
                FORM_ID = study.getStudyForm().getId();
            }

        }

        if(STUDY_NO_FORM_ID == 0L) {
            StudyDTO dto2 = new StudyDTO("title2", "[{\"insert\":\"서비스: 보드게임 웹 서비스\"}]", LocalDate.now(), 15, 1L, Arrays.asList(1L, 2L, 3L));
            STUDY_NO_FORM_ID = studyService.createStudy(member.getId(), dto2);
        }

        SetSecuritySession(member);
    }


    @DisplayName("스터디 신청 화면")
    @Test
    void 스터디_신청_화면() throws Exception {

        Optional<Member> byEmail = memberRepository.findById(2L);
        Member member = byEmail.orElseThrow(EntityNotFoundException::new);
        SetSecuritySession(member);

        mockMvc.perform(get("/study/apply")
                        .queryParam("studyId", String.valueOf(STUDY_WITH_FORM_ID))
                        .queryParam("formId", String.valueOf(FORM_ID))
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("smallstudy/study_form/study_form_view"))
                .andExpect(model().attributeExists("form"))
                .andExpect(model().attribute("deadLine", "no"));

    }

    @DisplayName("스터디 신청 화면 자기 자신")
    @Test
    void 스터디_신청_화면_자기_자신() throws Exception {

        Optional<Member> byEmail = memberRepository.findById(1L);
        Member member = byEmail.orElseThrow(EntityNotFoundException::new);
        SetSecuritySession(member);

        mockMvc.perform(get("/study/apply")
                        .queryParam("studyId", String.valueOf(STUDY_WITH_FORM_ID))
                        .queryParam("formId", String.valueOf(FORM_ID))
                        .with(csrf()))
                .andExpect(status().is4xxClientError());
    }

    @DisplayName("스터디 신청 화면 신청 기한 만료")
    @Test
    void 스터디_신청_화면_신청_기한_만료() throws Exception {


        StudyDTO dto = new StudyDTO("title3", "[{\"insert\":\"서비스: 보드게임 웹 서비스\"}]", LocalDate.now().minusDays(1), 15, 1L, Arrays.asList(1L, 2L, 3L));
        Long studyId = studyService.createStudy(1L, dto);

        StudyFormDTO studyFormDTO = getStudyFormDTO(studyId);
        studyFormService.save(studyFormDTO);
        Study study = studyService.findStudyByStudyId(studyId);
        Long formId = study.getStudyForm().getId();

        Optional<Member> byEmail = memberRepository.findById(2L);
        Member member = byEmail.orElseThrow(EntityNotFoundException::new);
        SetSecuritySession(member);

        MvcResult mvcResult = mockMvc.perform(get("/study/apply")
                        .queryParam("studyId", String.valueOf(studyId))
                        .queryParam("formId", String.valueOf(formId)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/" + studyId))
                .andReturn();

        String deadLine = (String) mvcResult.getFlashMap().get("deadLine");
        assertEquals(deadLine, "yes");

    }

    @DisplayName("스터디 신청")
    @Test
    void 스터디_신청() throws Exception {

        Optional<Member> byEmail = memberRepository.findById(2L);
        Member member = byEmail.orElseThrow(EntityNotFoundException::new);
        SetSecuritySession(member);

        MemberStudyFormDTO memberStudyFormDTO = getMemberStudyFormDTO();
        String data = JsonConverter.getJsonData(memberStudyFormDTO);


        mockMvc.perform(post("/study/apply")
                .contentType(MediaType.APPLICATION_JSON)
                .content(data)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.studyId").value(STUDY_WITH_FORM_ID));


        Optional<MemberStudyForm> findMemberForm = memberStudyFormRepository.findByMemberIdAndStudyFormId(2L, FORM_ID);
        assertTrue(findMemberForm.isPresent());

        Optional<Application> findApplication = applicationRepository.findApplicationByMemberIdAndStudyId(2L, STUDY_WITH_FORM_ID);
        Application application = findApplication.orElseThrow(EntityNotFoundException::new);

        assertEquals(application.getStatus(), APPLIED);
    }


    @DisplayName("스터디 no-form 신청")
    @Test
    void 스터디_no_form_신청() throws Exception {

        Optional<Member> byEmail = memberRepository.findById(2L);
        Member member = byEmail.orElseThrow(EntityNotFoundException::new);
        SetSecuritySession(member);

        MvcResult mvcResult = mockMvc.perform(get("/study/apply/no-form")
                        .param("studyId", String.valueOf(STUDY_NO_FORM_ID))
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/study/" + STUDY_NO_FORM_ID))
                .andReturn();

        String deadLine = (String) mvcResult.getFlashMap().get("deadLine");
        assertEquals(deadLine, "no");

        Optional<Application> findApplication = applicationRepository.findApplicationByMemberIdAndStudyId(member.getId(), STUDY_NO_FORM_ID);
        Application application = findApplication.orElseThrow(EntityNotFoundException::new);
        assertEquals(application.getStatus(), APPLIED);
    }

    @DisplayName("스터디 신청 취소")
    @Test
    void 스터디_신청_취소() throws Exception {
        Optional<Member> byEmail = memberRepository.findById(2L);
        Member member = byEmail.orElseThrow(EntityNotFoundException::new);
        SetSecuritySession(member);

        MemberStudyFormDTO memberStudyFormDTO = getMemberStudyFormDTO();
        String data = JsonConverter.getJsonData(memberStudyFormDTO);

        mockMvc.perform(post("/study/apply")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(data)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.studyId").value(STUDY_WITH_FORM_ID));

        mockMvc.perform(get("/study/applicant/cancel")
                .param("studyId", String.valueOf(STUDY_WITH_FORM_ID))
                .param("page", String.valueOf(0)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile/apply?page=" + 0));

        Optional<Application> findApplication = applicationRepository.findApplicationByMemberIdAndStudyId(member.getId(), STUDY_WITH_FORM_ID);
        assertTrue(findApplication.isEmpty());

        Optional<MemberStudyForm> memberForm = memberStudyFormRepository.findByMemberIdAndStudyFormId(member.getId(), FORM_ID);
        assertTrue(memberForm.isEmpty());
    }


    private static StudyFormDTO getStudyFormDTO(Long studyId) {
        ArrayList<StudyFormQuestionDTO> list = new ArrayList<>();
        int random = (int) (Math.random() % 3) + 1;

        if(random % 2 == 0) {
            list.add(getStudyFormQuestionDTO());
        } else {
            list.add(getStudyFormQuestionDTOWithText());
        }
        return new StudyFormDTO(studyId, "FORM", "DESCRIPTION", list);
    }
    private static StudyFormQuestionDTO getStudyFormQuestionDTO() {

        ArrayList<StudyFormQuestionItemDTO> list = new ArrayList<>();
        int random = (int) (Math.random() % 10) + 2;

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

    private static StudyFormQuestionDTO getStudyFormQuestionDTOWithText() {
        int random = (int) (Math.random() % 10) + 1;
        return new StudyFormQuestionDTO("title", List.of(new StudyFormQuestionItemDTO("text" + random)), "TEXT");
    }

    private MemberStudyFormDTO getMemberStudyFormDTO() {
        Study study = studyFormService.findStudyByFormId(FORM_ID);
        StudyForm studyForm = study.getStudyForm();

        List<StudyFormQuestion> studyFormQuestions = studyForm.getStudyFormQuestions();
        MemberStudyFormDTO formAnswer = new MemberStudyFormDTO(STUDY_WITH_FORM_ID, FORM_ID, 2L);

        formAnswer.setQuestionCount((long) studyFormQuestions.size());
        List<MemberAnswerDTO> answers = studyFormQuestions.stream().map(question -> {
            if (question.getType().equals(QuestionType.TEXT)) {
                Optional<String> findAnswer = question.getStudyFormQuestionItems()
                        .stream()
                        .map(StudyFormQuestionItem::getItem)
                        .findAny();
                String answer = findAnswer.orElseThrow(EntityNotFoundException::new);
                return new MemberAnswerDTO(question.getId(), null, answer);
            }
            Optional<Long> first = question.getStudyFormQuestionItems()
                    .stream()
                    .map(StudyFormQuestionItem::getId)
                    .filter(id -> id % 2 == 0)
                    .findFirst();
            Long answer = first.orElseThrow(EntityNotFoundException::new);
            return new MemberAnswerDTO(question.getId(), List.of(answer), null);
        }).toList();

        for (MemberAnswerDTO answer : answers) {
            formAnswer.addMemberAnswerDTO(answer);
        }

        return formAnswer;
    }

    private static void SetSecuritySession(Member save) {
        UserDetails userDetails = new CustomUser(save);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }


}
