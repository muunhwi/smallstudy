package com.smallstudy;
import com.smallstudy.domain.category_entity.CategoryItem;
import com.smallstudy.domain.member_entity.Member;
import com.smallstudy.domain.member_entity.MemberCategoryItem;
import com.smallstudy.domain.member_entity.MemberInterestRegion;
import com.smallstudy.domain.region_entity.InterestRegion;
import com.smallstudy.domain.study_entity.Study;
import com.smallstudy.domain.study_entity.StudyCategoryItem;

import com.smallstudy.dto.study_dto.StudyDTO;
import com.smallstudy.repo.InterestRegionRepository;
import com.smallstudy.repo.category_repo.CategoryItemRepository;
import com.smallstudy.repo.member_repo.MemberRepository;
import com.smallstudy.security.CustomUser;
import com.smallstudy.service.CategoryItemService;
import com.smallstudy.service.InterestRegionService;
import com.smallstudy.service.StudyService;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
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
import org.springframework.test.web.servlet.MvcResult;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;



@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class StudyTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private CategoryItemRepository categoryItemRepository;
    @Autowired
    private CategoryItemService categoryItemService;
    @Autowired
    private InterestRegionService interestRegionService;
    @Autowired
    private InterestRegionRepository interestRegionRepository;

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private StudyService studyService;

    private static String contents = "[{\"insert\":\"서비스: 보드게임 웹 서비스\"},{\"attributes\":{\"header\":1},\"insert\":\"\\n\"},{\"insert\":\"\\n기술스택\"},{\"attributes\":{\"header\":2},\"insert\":\"\\n\"},{\"insert\":\"프론트 : 프론트 기술 스택은 React + TypeScript 혹은 Next.js 모집 후 결정\"},{\"attributes\":{\"list\":\"bullet\"},\"insert\":\"\\n\"},{\"insert\":\"백엔드 : kotlin + SpringBoot / 오라클 클라우드 / DB는 스키마 설계 후 결정\"},{\"attributes\":{\"list\":\"bullet\"},\"insert\":\"\\n\"},{\"insert\":\"\\n기능\"},{\"attributes\":{\"header\":2},\"insert\":\"\\n\"},{\"insert\":\"보드게임 정보 제공(EX 이미지, 설명, 인원, 가격, 장르 등) ⇒ 메인기능입니다.\"},{\"attributes\":{\"list\":\"bullet\"},\"insert\":\"\\n\"},{\"insert\":\"보드게임 파티 모집 기능\"},{\"attributes\":{\"list\":\"bullet\"},\"insert\":\"\\n\"},{\"insert\":\"(단체) 채팅 기능 / 쪽지 기능\"},{\"attributes\":{\"list\":\"bullet\"},\"insert\":\"\\n\"},{\"insert\":\"나만의 플레이리스트 추가 및 필터링\"},{\"attributes\":{\"list\":\"bullet\"},\"insert\":\"\\n\"},{\"insert\":\"해당 보드게임 별점 및 후기 작성 기능\"},{\"attributes\":{\"list\":\"bullet\"},\"insert\":\"\\n\"},{\"insert\":\"가까운 보드게임카페 찾기\"},{\"attributes\":{\"list\":\"bullet\"},\"insert\":\"\\n\"},{\"insert\":\"인공지능 챗봇\"},{\"attributes\":{\"list\":\"bullet\"},\"insert\":\"\\n\"},{\"insert\":\"\\n현재 인원 (10/18 기준)\"},{\"attributes\":{\"header\":2},\"insert\":\"\\n\"},{\"insert\":\"디자이너 : 1 / 프론트 : 4 / 백엔드 :4 / 인프라 : 1\"},{\"attributes\":{\"list\":\"bullet\"},\"insert\":\"\\n\"},{\"insert\":\"\\n모집 인원\"},{\"attributes\":{\"header\":2},\"insert\":\"\\n\"},{\"insert\":\"기획 : 2\"},{\"attributes\":{\"list\":\"bullet\"},\"insert\":\"\\n\"},{\"insert\":\"\\n최소 시작 인원\"},{\"attributes\":{\"header\":2},\"insert\":\"\\n\"},{\"insert\":\"프론트 : 2 / 백엔드 : 2\"},{\"attributes\":{\"list\":\"bullet\"},\"insert\":\"\\n\"},{\"insert\":\"\\n시작 예정\"},{\"attributes\":{\"header\":2},\"insert\":\"\\n\"},{\"insert\":\"2024.10.24\"},{\"attributes\":{\"list\":\"bullet\"},\"insert\":\"\\n\"},{\"insert\":\"\\n예상 기간\"},{\"attributes\":{\"header\":2},\"insert\":\"\\n\"},{\"insert\":\"1차 완성 및 오픈 : 3~4달\"},{\"attributes\":{\"list\":\"bullet\"},\"insert\":\"\\n\"},{\"insert\":\"모니터링, 유지 보수: 약 2달 (필참x)\"},{\"attributes\":{\"list\":\"bullet\"},\"insert\":\"\\n\"},{\"insert\":\"2차 고도화, 리팩토링: 미정 (필참x)\"},{\"attributes\":{\"list\":\"bullet\"},\"insert\":\"\\n\"},{\"insert\":\"\\n스크럼 주기:\"},{\"attributes\":{\"header\":2},\"insert\":\"\\n\"},{\"insert\":\"전체 주 2회 30분 미만 나머지 팀별 스크럼은 각 파트끼리 조율 (온라인 위주, 수도권인 경우 오프라인 가능)\"},{\"attributes\":{\"list\":\"bullet\"},\"insert\":\"\\n\"},{\"insert\":\"\\n방향성:\"},{\"attributes\":{\"header\":2},\"insert\":\"\\n\"},{\"insert\":\"흥미를 일으키는 서비스를 만들고 싶고 그게 실제로 서비스로 이어가고 싶다.\"},{\"attributes\":{\"list\":\"bullet\"},\"insert\":\"\\n\"},{\"insert\":\"서비스를 끝까지 진행해서 완료하고 싶다. ⇒ 마일스톤을 작성해서 구체적이고 계획적으로\"},{\"attributes\":{\"list\":\"bullet\"},\"insert\":\"\\n\"},{\"insert\":\"본인이 가지고 있는 열정만큼 자유롭게 개발 하되 느리더라도 계획성 있게 꾸준히 ‘팀’ 이라는 책임감을 갖자\"},{\"attributes\":{\"list\":\"bullet\"},\"insert\":\"\\n\"},{\"insert\":\"일정은 팀원 간의 조율 가능하며 큰 무리가 없는 선에선 자유롭게 운영할 계획입니다.\"},{\"attributes\":{\"list\":\"bullet\"},\"insert\":\"\\n\"},{\"insert\":\"\\n목표:\"},{\"attributes\":{\"header\":2},\"insert\":\"\\n\"},{\"insert\":\"실 서비스 런칭 + 홍보 → 가입 유저 100명 이상 목표로 운영, 고도화\"},{\"attributes\":{\"list\":\"bullet\"},\"insert\":\"\\n\"},{\"insert\":\"\\n지원자격:\"},{\"attributes\":{\"header\":2},\"insert\":\"\\n\"},{\"insert\":\"경력 무관 하며 실제 서비스를 런칭 or 구현해 본 경험이 있으면 우대\"},{\"attributes\":{\"list\":\"bullet\"},\"insert\":\"\\n\"},{\"insert\":\"\\nContact : 이메일 / 카카오톡 오픈채팅\"},{\"attributes\":{\"header\":2},\"insert\":\"\\n\"},{\"insert\":\"[rhquddnr95@naver.com](<mailto:rhquddnr95@naver.com>)\"},{\"attributes\":{\"code-block\":\"plain\"},\"insert\":\"\\n\"},{\"insert\":\"[카톡 오픈 채팅 링크]https://open.kakao.com/o/gj2O5QUg\"},{\"attributes\":{\"code-block\":\"plain\"},\"insert\":\"\\n\"},{\"insert\":\"*어떠한 영리 목적 없으며 궁금한 점 편하게 물어보시면 감사하겠습니다.\\n\\n\"}]\t";

    @BeforeAll
    static void signup(@Autowired MemberRepository memberRepository) throws IOException {

        if (!memberRepository.existsById(1L)) {
            PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
            Member member = new Member("test@test.com", "nickname", encoder.encode("random123!"));

            member.setEmailValid();
            memberRepository.save(member);
            SetSecuritySession(member);
        } else {

            Optional<Member> findMember = memberRepository.findById(1L);
            Member member = findMember.orElseThrow(EntityNotFoundException::new);
            SetSecuritySession(member);
        }

    }


    @DisplayName("스터디 생성하기")
    @Test
    void 스터디_생성_후_리다이렉트() throws Exception {

        Member member = getMember();

        MockHttpSession session = new MockHttpSession();
        SetSecuritySession(member);

        StudyDTO studyDTO = new StudyDTO("title", contents, LocalDate.now(), 15, 1L, Arrays.asList(1L, 2L, 3L));
        Long studyId = studyService.createStudy(member.getId(), studyDTO);

        mockMvc.perform(get("/study/" + studyId).session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("smallstudy/study/study_view"))
                .andExpect(model().attributeExists("isAuthor", "canApply", "study", "isUser", "member"));

    }

    @DisplayName("스터디 생성 후 업데이트 하기")
    @Test
    @Transactional
    void 스터디_생성_후_업데이트() throws Exception {

        MockHttpSession session = new MockHttpSession();
        Member member = getMember();
        SetSecuritySession(member);

        StudyDTO studyDTO = new StudyDTO("title", contents, LocalDate.now(), 15, 1L, Arrays.asList(1L, 2L, 3L));
        Long studyId = studyService.createStudy(member.getId(), studyDTO);

        mockMvc.perform(post("/study/" + studyId + "/update").session(session)
                        .param("title", "test123")
                        .param("contents", contents)
                        .param("endDate", String.valueOf(LocalDate.now()))
                        .param("groupSize", String.valueOf(12))
                        .param("region", String.valueOf(5))
                        .param("categories", "2","3","4", "6", "8", "11")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.studyId").value(String.valueOf(studyId)));

        Study study = studyService.findStudyByStudyId(studyId);
        List<StudyCategoryItem> studyCategoryItems = study.getStudyCategoryItems();
        Integer groupSize = study.getGroupSize();

        Assertions.assertEquals(12, groupSize);
        Assertions.assertEquals(5, study.getInterestRegion().getId());

        Assertions.assertEquals(6, studyCategoryItems.size());
        Assertions.assertEquals(2, studyCategoryItems.get(0).getCategoryItem().getId());
        Assertions.assertEquals(3, studyCategoryItems.get(1).getCategoryItem().getId());
        Assertions.assertEquals(4, studyCategoryItems.get(2).getCategoryItem().getId());
        Assertions.assertEquals(6, studyCategoryItems.get(3).getCategoryItem().getId());
        Assertions.assertEquals(8, studyCategoryItems.get(4).getCategoryItem().getId());
        Assertions.assertEquals(11, studyCategoryItems.get(5).getCategoryItem().getId());
    }


    @DisplayName("스터디 생성 후 삭제하기")
    @Test
    void 스터디_생성_후_삭제하기() throws Exception {


        Member member = getMember();
        SetSecuritySession(member);

        StudyDTO studyDTO = new StudyDTO("title", contents, LocalDate.now(), 15, 1L, Arrays.asList(1L, 2L, 3L));
        Long studyId = studyService.createStudy(member.getId(), studyDTO);

        mockMvc.perform(get("/study/" + studyId +"/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/main"));
    }

    @DisplayName("스터디 생성 후 다른 사용자가 update")
    @Test
    void 스터디_생성_후_다른사용자_업데이트() throws Exception {


        MockHttpSession session = new MockHttpSession();
        Member member = getMember();
        SetSecuritySession(member);

        StudyDTO studyDTO = new StudyDTO("title", contents, LocalDate.now(), 15, 1L, Arrays.asList(1L, 2L, 3L));
        Long studyId = studyService.createStudy(member.getId(), studyDTO);

        Optional<Member> findMember = memberRepository.findById(1L);
        Member newMember = findMember.orElseThrow(EntityNotFoundException::new);

        session.clearAttributes();
        SetSecuritySession(newMember);

        mockMvc.perform(post("/study/"+studyId+ "/update").session(session)
                        .param("title", "test123")
                        .param("contents", contents)
                        .param("endDate", String.valueOf(LocalDate.now()))
                        .param("groupSize", String.valueOf(15))
                        .param("region", String.valueOf(1))
                        .param("categories", "2","3","4"))
                .andExpect(status().is4xxClientError());
    }

    private Member getMemberWithMemberSelectedRegionsAndCategoryItem() {
        Member member = new Member("test2@google.com", "admin1232", "password!");
        Optional<CategoryItem> findCategoryItem = categoryItemRepository.findById(1L);
        Assertions.assertTrue(findCategoryItem.isPresent(), "ID -> 1L categoryItem은  반드시 존재한다.");

        CategoryItem categoryItem = findCategoryItem.get();
        MemberCategoryItem memberCategoryItem = new MemberCategoryItem(member, categoryItem);
        member.addMemberCategoryItem(memberCategoryItem);

        Optional<InterestRegion> findRegion = interestRegionRepository.findById(1L);
        Assertions.assertTrue(findRegion.isPresent(), "ID -> 1L InterestRegion은 반드시 존재한다.");
        InterestRegion interestRegion = findRegion.get();

        MemberInterestRegion memberInterestRegion = new MemberInterestRegion(member, interestRegion);
        member.addMemberRegion(memberInterestRegion);
        return memberRepository.save(member);
    }

    private static void SetSecuritySession(Member save) {
        UserDetails userDetails = new CustomUser(save);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    private Object getModelData(String name, MvcResult mvcResult) {
        return Objects.requireNonNull(mvcResult.getModelAndView())
                .getModel()
                .get(name);
    }

    private Member getMember() {
        Optional<Member> findMember = memberRepository.findById(1L);
        return findMember.orElseThrow(EntityNotFoundException::new);
    }

}
