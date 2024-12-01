package com.smallstudy.event;

import com.smallstudy.dto.study_dto.StudyDTO;
import com.smallstudy.event.type.StudyInitializedEvent;
import com.smallstudy.service.StudyService;
import com.smallstudy.utils.MyLocalCache;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class StudyInitializedEventHandler {

    private final StudyService studyService;
    private final EntityManager entityManager;
    private final PlatformTransactionManager transactionManager;
    private static String[] contents = {
            "[{\"insert\":\"\uD83E\uDD26\u200D♀\uFE0F: 데이터 분석하려면 SQL이 필수네.. 난 문과라 코딩은 못하는데..\\n\uD83E\uDD26: 데이터 직무로 취업하고 싶어서 SQL 활용 역량을 갖추고 싶어..\\n\\nSQLD 합격과 실무 SQL 스킬을 모두 배울 수 있는 온라인 스터디\\n스파르타 SQL 스터디에서 함께 SQL 공부하실 분을 구합니다!\\n\\n\uD83D\uDCCC 스파르타 SQL 스터디는!\\n1\uFE0F⃣ 4주 동안 SQLD 합격과 실무 SQL 공부에 도전해요\\n2\uFE0F⃣ 카카오 데이터 엔지니어와 함께 실무 SQL 스킬까지 확실하게\\n3\uFE0F⃣ 스터디 참가비 무료, 완료하면 최대 10만원 상당의 선물을 드려요.\\n\\n의지가 부족해도, SQL은 처음이라도 괜찮아요!\\n스파르타 SQL 스터디는 잘 참여만 해도 쉽게 SQL을 배울 수 있어요.\\n\\n\uD83D\uDC49 스터디 신청하기: \"},{\"attributes\":{\"link\":\"https://bit.ly/3NqUbd1\"},\"insert\":\"https://bit.ly/3NqUbd1\"},{\"insert\":\"\\n\uD83D\uDCCC 스터디 안내\\n난이도: 왕초보 (초심자 환영)\"},{\"attributes\":{\"list\":\"bullet\"},\"insert\":\"\\n\"},{\"insert\":\"모집 마감: 10/21 (월) [오늘 마감!]\"},{\"attributes\":{\"list\":\"bullet\"},\"insert\":\"\\n\"},{\"insert\":\"모집 인원: 선착순 100명\"},{\"attributes\":{\"list\":\"bullet\"},\"insert\":\"\\n\"},{\"insert\":\"스터디 기간: 10/22(화) ~ 11/16(일) / 4주\"},{\"attributes\":{\"list\":\"bullet\"},\"insert\":\"\\n\"},{\"insert\":\"참가비: 무료 (자부담금 4.9만원 스터디 완료 후 환급)\"},{\"attributes\":{\"list\":\"bullet\"},\"insert\":\"\\n\"},{\"insert\":\"장소: 온라인\"},{\"attributes\":{\"list\":\"bullet\"},\"insert\":\"\\n\"},{\"insert\":\"\\n\"}]\t",
            "[{\"attributes\":{\"bold\":true},\"insert\":\"프로젝트 소개\"},{\"attributes\":{\"header\":2},\"insert\":\"\\n\"},{\"insert\":\"하이파이브 프로젝트 팀원 모집\"},{\"attributes\":{\"header\":3},\"insert\":\"\\n\"},{\"insert\":\"안녕하세요!\\n저희는 \"},{\"attributes\":{\"bold\":true},\"insert\":\"하이파이브 프로젝트\"},{\"insert\":\"를 통해 혁신적인 퀀트 투자 플랫폼을 개발하고 있는 핀테크팀 입니다. 이 플랫폼은 사용자에게 빅데이터를 기반으로 \"},{\"attributes\":{\"link\":\"https://namu.wiki/w/%ED%80%80%ED%8A%B8\"},\"insert\":\"퀀트\"},{\"insert\":\" 모델링 방식 과 투자 전략을 제공하고 큰 틀로는 고객이 스스로 원하는 목적이나 방향(기술적,기본적)변수들을 클릭 해가며 포트폴리오를 시각화하여 만들어보고 이를 통해, 실시간 시장 데이터와 API를 통해 즉각적인 투자매매나 투자 결정을 지원하는 것을 목표로 하고 있습니다. 큰 꿈이긴하지만 더 나아가 회사가 커진다면 증권사가 될수도 있겠죠?\\n\\n1. 사업 개요\"},{\"attributes\":{\"header\":4},\"insert\":\"\\n\"},{\"attributes\":{\"bold\":true},\"insert\":\"프로젝트명\"},{\"insert\":\":하이파이브\"},{\"attributes\":{\"list\":\"bullet\"},\"insert\":\"\\n\"},{\"attributes\":{\"bold\":true},\"insert\":\"목표\"},{\"insert\":\": 사용자에게 개인화된 투자 전략을 시각화하여 시장에서의 성공 확률을 높이고, 실시간 데이터와 연결된 투자 결정을 지원하는 플랫폼 개발.\"},{\"attributes\":{\"list\":\"bullet\"},\"insert\":\"\\n\"},{\"attributes\":{\"bold\":true},\"insert\":\"특징\"},{\"insert\":\": 개인화 경험, 직관적인 도구, 알고리즘 기반 콘텐츠 추천 및 학습 시스템 결합.\"},{\"attributes\":{\"list\":\"bullet\"},\"insert\":\"\\n\"},{\"insert\":\"2. 사업의 필요성 및 시작 동기\"},{\"attributes\":{\"header\":4},\"insert\":\"\\n\"},{\"attributes\":{\"bold\":true},\"insert\":\"필요성\"},{\"insert\":\": 현대 투자 시장은 복잡하고 정보가 넘쳐나며, 초보 투자자들은 맞춤형 교육과 전략 부족으로 어려움을 겪고 있습니다.\"},{\"attributes\":{\"list\":\"bullet\"},\"insert\":\"\\n\"},{\"attributes\":{\"bold\":true},\"insert\":\"시작 동기\"},{\"insert\":\":전략하나를 만드는데 여러 변수와 이것 저것 맞춰가면서 최적화 작업과 백테스팅을 해야하는데.기존 플랫폼의 일반화된 데이터의 전략에 큰 한계를 느꼇고, 개인 투자자의 구체적인 요구를 반영한 맞춤형 투자 전략을 제공 해주는데 너무 큰돈을 요구해 화나서 직접 만들고자 합니다.\"},{\"attributes\":{\"list\":\"bullet\"},\"insert\":\"\\n\"},{\"insert\":\"(백테스팅을 엉청 많이 해봐야지 실력이나 운이 터짐) 이 과정이 시간이나 돈을 엉청 많이 잡아먹음\"},{\"attributes\":{\"list\":\"bullet\"},\"insert\":\"\\n\"},{\"insert\":\"3. 사업 목표 및 비전\"},{\"attributes\":{\"header\":4},\"insert\":\"\\n\"},{\"attributes\":{\"bold\":true},\"insert\":\"사업 목표\"},{\"insert\":\": 회사에서 어느 정도 조합이나 통계학적으로 나 객관적으로 모델링된 효율성이 입증된 덩어리 즉(mass)들을 가지고 있고 이를 제공해줌 으로서 투자자들이 자신의 성향에 맞는 최적의 투자 전략과 도구를 선택할 수 있고 통계학을 바탕으로 누구나쉽게 포트폴리오나 전략을 모델링 할 수 있도록 돕는 플랫폼으로 성장.\"},{\"attributes\":{\"list\":\"bullet\"},\"insert\":\"\\n\"},{\"attributes\":{\"bold\":true},\"insert\":\"사업 비전\"},{\"insert\":\":\\n\"},{\"attributes\":{\"bold\":true},\"insert\":\"개인화된 투자 경험 제공\"},{\"insert\":\": 모든 사용자가 자신의 투자 성향과 목표에 맞는 맞춤형 전략을 쉽게 찾고 활용할 수 있도록 하여, 개인 투자자들이 자신만의 투자 여정을 주도할 수 있게 합니다.\"},{\"attributes\":{\"list\":\"ordered\"},\"insert\":\"\\n\"},{\"attributes\":{\"bold\":true},\"insert\":\"데이터 기반 의사결정 지원\"},{\"insert\":\": \"},{\"attributes\":{\"link\":\"https://namu.wiki/w/%ED%80%80%ED%8A%B8\"},\"insert\":\"퀀트\"},{\"insert\":\" 모델링 데이터들과 고급 분석 도구를 통해 투자자들이 정보에 기반한 결정을 내릴 수 있도록 지원하며, 이를 통해 시장의 변동성에 효과적으로 대응할 수 있는 능력을 배양합니다.\"},{\"attributes\":{\"list\":\"ordered\"},\"insert\":\"\\n\"},{\"attributes\":{\"bold\":true},\"insert\":\"교육과 학습의 통합\"},{\"insert\":\": 알고리즘 기반의 콘텐츠 추천 시스템을 통해 사용자에게 필요한 교육 자료와 학습 경로를 제공하여, 초보 투자자들이 투자 지식을 쌓고 실력을 향상시킬 수 있도록 합니다.\"},{\"attributes\":{\"list\":\"ordered\"},\"insert\":\"\\n\"},{\"attributes\":{\"bold\":true},\"insert\":\"투자 커뮤니티 구축\"},{\"insert\":\": 사용자들이 서로의 경험과 전략을 공유할 수 있는 플랫폼을 제공하여, 협업과 소통을 통해 더 나은 투자 결정을 내릴 수 있는 환경을 조성합니다.\"},{\"attributes\":{\"list\":\"ordered\"},\"insert\":\"\\n\"},{\"attributes\":{\"bold\":true},\"insert\":\"지속 가능한 성장 지원\"},{\"insert\":\": 투자자들이 장기적으로 안정적인 수익을 추구할 수 있도록 돕고, 이를 통해 개인의 재정적 자유와 안정성을 증진시키는 것을 목표로 합니다.\"},{\"attributes\":{\"list\":\"ordered\"},\"insert\":\"\\n\"},{\"insert\":\"\\n\\n4. 플랫폼 주요 기능\"},{\"attributes\":{\"header\":4},\"insert\":\"\\n\"},{\"attributes\":{\"bold\":true},\"insert\":\"개인화된 투자 전략 제공\"},{\"insert\":\": 사용자의 투자 성향을 분석해 맞춤형 전략들과모델링 추천.\"},{\"attributes\":{\"list\":\"bullet\"},\"insert\":\"\\n\"},{\"attributes\":{\"bold\":true},\"insert\":\"직관적인 UI/UX\"},{\"insert\":\": 누구나 쉽게 사용할 수 있는 디자인 제공.\"},{\"attributes\":{\"list\":\"bullet\"},\"insert\":\"\\n\"},{\"attributes\":{\"bold\":true},\"insert\":\"온디맨드 투자 교육\"},{\"insert\":\": 사용자 맞춤형 학습 콘텐츠 제공.\"},{\"attributes\":{\"list\":\"bullet\"},\"insert\":\"\\n\"},{\"attributes\":{\"bold\":true},\"insert\":\"커뮤니티 및 정보 공유\"},{\"insert\":\": 사용자 간의 경험 공유 및 의견 교환 공간 마련.\"},{\"attributes\":{\"list\":\"bullet\"},\"insert\":\"\\n\"},{\"attributes\":{\"bold\":true},\"insert\":\"실시간 시장 데이터 및 알림 기능\"},{\"insert\":\": 주요 변동 사항에 대한 빠른 대응 지원.\"},{\"attributes\":{\"list\":\"bullet\"},\"insert\":\"\\n\"},{\"attributes\":{\"bold\":true},\"insert\":\"데이터 기반 의사결정 지원\"},{\"insert\":\": 다양한 시각화 도구 제공.\"},{\"attributes\":{\"list\":\"bullet\"},\"insert\":\"\\n\"},{\"insert\":\"5. 모집 분야\"},{\"attributes\":{\"header\":4},\"insert\":\"\\n\"},{\"insert\":\"저희 팀은 아래와 같은 분야의 인재를 모집하고 있습니다\\n\"},{\"attributes\":{\"bold\":true},\"insert\":\"디자이너\"},{\"insert\":\": 사용자 경험(UX) 및 인터페이스(UI) 디자인\"},{\"attributes\":{\"list\":\"bullet\"},\"insert\":\"\\n\"},{\"attributes\":{\"bold\":true},\"insert\":\"통계학 전문가\"},{\"insert\":\": 데이터 분석 및 모델링\"},{\"attributes\":{\"list\":\"bullet\"},\"insert\":\"\\n\"},{\"attributes\":{\"bold\":true},\"insert\":\"AI 전문가\"},{\"insert\":\": 머신러닝 및 데이터 처리\"},{\"attributes\":{\"list\":\"bullet\"},\"insert\":\"\\n\"},{\"attributes\":{\"bold\":true},\"insert\":\"데이터 전문가\"},{\"insert\":\": 데이터 수집, 처리 및 분석\"},{\"attributes\":{\"list\":\"bullet\"},\"insert\":\"\\n\"},{\"insert\":\"풀스텍 ,프론,벡\"},{\"attributes\":{\"list\":\"bullet\"},\"insert\":\"\\n\"},{\"insert\":\"서버\"},{\"attributes\":{\"list\":\"bullet\"},\"insert\":\"\\n\"},{\"insert\":\"마케팅\"},{\"attributes\":{\"list\":\"bullet\"},\"insert\":\"\\n\"},{\"insert\":\"기획\"},{\"attributes\":{\"list\":\"bullet\"},\"insert\":\"\\n\"},{\"insert\":\"web\"},{\"attributes\":{\"list\":\"bullet\"},\"insert\":\"\\n\"},{\"insert\":\"6. 지원 방법\"},{\"attributes\":{\"header\":4},\"insert\":\"\\n\"},{\"insert\":\"관심이 있으신 분은 아래 이메일로 문의해 주시기 바랍니다:\\n\"},{\"attributes\":{\"link\":\"mailto:pauloppo@naver.com\"},\"insert\":\"pauloppo@naver.com\"},{\"attributes\":{\"list\":\"bullet\"},\"insert\":\"\\n\"},{\"attributes\":{\"link\":\"mailto:paul9504@gmail.com\"},\"insert\":\"paul9504@gmail.com\"},{\"attributes\":{\"list\":\"bullet\"},\"insert\":\"\\n\"},{\"insert\":\"7. 기대 효과\"},{\"attributes\":{\"header\":4},\"insert\":\"\\n\"},{\"insert\":\"맞춤형 투자 모델링 제공으로 투자 성공률 향상.\"},{\"attributes\":{\"list\":\"bullet\"},\"insert\":\"\\n\"},{\"insert\":\"개인화된 투자 경험으로 시장 점유율 확대.\"},{\"attributes\":{\"list\":\"bullet\"},\"insert\":\"\\n\"},{\"insert\":\"\\n8.참고자료\\n\"},{\"attributes\":{\"link\":\"https://docs.google.com/document/d/1BnvNP8H7LuOP-NK78m4KxLiHZZha4mw_5By7aIy7e_4/edit?tab=t.0\"},\"insert\":\"https://docs.google.com/document/d/1BnvNP8H7LuOP-NK78m4KxLiHZZha4mw_5By7aIy7e_4/edit?tab=t.0\"},{\"insert\":\"\\n\\n\"},{\"attributes\":{\"link\":\"https://docs.google.com/document/d/1hTW4QMMC6XF2_IKwJ-CIZTwuoNoONn5TiihjQliyRvk/edit?tab=t.0\"},\"insert\":\"https://docs.google.com/document/d/1hTW4QMMC6XF2_IKwJ-CIZTwuoNoONn5TiihjQliyRvk/edit?tab=t.0\"},{\"insert\":\"\\n\\n\"},{\"attributes\":{\"link\":\"https://docs.google.com/document/d/1fqoR2YbzK6UCiHRlUcZGEDCBqoemd2CVkE254yRgh6Q/edit?tab=t.0#heading=h.wtxy1fveejfv\"},\"insert\":\"https://docs.google.com/document/d/1fqoR2YbzK6UCiHRlUcZGEDCBqoemd2CVkE254yRgh6Q/edit?tab=t.0#heading=h.wtxy1fveejfv\"},{\"insert\":\"\\n\\n\\n저희와 함께 혁신적인 투자 플랫폼을 만들어 나갈 열정적인 인재들의 많은 지원을 기다립니다. 감사합니다\\n\"}]\t",
            "[{\"attributes\":{\"list\":\"ordered\"},\"insert\":\"\\n\"},{\"insert\":\"✋홀로 취업 준비가 어려운 취업 준비생,\"},{\"attributes\":{\"list\":\"ordered\"},\"insert\":\"\\n\"},{\"insert\":\"✋코딩 테스트가 부담스러운 이직을 꿈꾸는 개발자,\"},{\"attributes\":{\"list\":\"ordered\"},\"insert\":\"\\n\"},{\"insert\":\"✋학습 커뮤니티가 필요하신 모든 분들께,\"},{\"attributes\":{\"list\":\"ordered\"},\"insert\":\"\\n\"},{\"insert\":\"\uD83C\uDFAF 코테 스터디 99클럽\uD83C\uDFAF을 소개합니다!\"},{\"attributes\":{\"list\":\"ordered\"},\"insert\":\"\\n\\n\"},{\"insert\":\"\uD83D\uDCCC 99클럽의 특별한 혜택\"},{\"attributes\":{\"list\":\"ordered\"},\"insert\":\"\\n\"},{\"insert\":\"취업/이직 기출 문제를 \"},{\"attributes\":{\"bold\":true},\"insert\":\"매일\"},{\"insert\":\" 제공\"},{\"attributes\":{\"list\":\"ordered\"},\"insert\":\"\\n\"},{\"insert\":\"현직 개발자의 코드 풀이법 & 코칭\"},{\"attributes\":{\"list\":\"ordered\"},\"insert\":\"\\n\"},{\"insert\":\"주 1회 특강으로 실전 노하우 전수\"},{\"attributes\":{\"list\":\"ordered\"},\"insert\":\"\\n\"},{\"insert\":\"학습 동기부여를 위한 다양한 이벤트까지! \uD83C\uDF89\"},{\"attributes\":{\"list\":\"ordered\"},\"insert\":\"\\n\\n\"},{\"insert\":\"\uD83D\uDC68\u200D\uD83D\uDCBB 스터디 진행 안내\"},{\"attributes\":{\"list\":\"ordered\"},\"insert\":\"\\n\"},{\"insert\":\"기간: 10월 28일(월) ~ 12월 2일(월)\"},{\"attributes\":{\"list\":\"ordered\"},\"insert\":\"\\n\"},{\"insert\":\"시간: 매주 목 오후 8시~10시 (정기스터디)\"},{\"attributes\":{\"list\":\"ordered\"},\"insert\":\"\\n\"},{\"insert\":\"매일 오전 11시 문제 출제 (자율스터디)\"},{\"attributes\":{\"list\":\"ordered\"},\"insert\":\"\\n\"},{\"insert\":\"형식: 온라인 환경을 활용한 코딩 테스트 문제 출제 & 풀이 진행\"},{\"attributes\":{\"list\":\"ordered\"},\"insert\":\"\\n\\n\"},{\"insert\":\"\uD83D\uDCBB 지원 언어: Python / Java / C++\"},{\"attributes\":{\"list\":\"ordered\"},\"insert\":\"\\n\"},{\"insert\":\"\uD83D\uDCC5 신청 기간: 9월 30일(수) ~ 10월 27일(일)\"},{\"attributes\":{\"list\":\"ordered\"},\"insert\":\"\\n\"},{\"insert\":\"지금 바로 신청하고 코딩 테스트 걱정을 덜어보세요! \uD83D\uDE80\"},{\"attributes\":{\"list\":\"ordered\"},\"insert\":\"\\n\\n\"},{\"insert\":\"신청하기 : \"},{\"attributes\":{\"link\":\"https://bit.ly/484Uns5\"},\"insert\":\"https://bit.ly/484Uns5\"},{\"attributes\":{\"list\":\"ordered\"},\"insert\":\"\\n\"},{\"insert\":\"\\n\"}]\t"
    };

    @EventListener(StudyInitializedEvent.class)
    public void handler() {

        DefaultTransactionDefinition def = new DefaultTransactionDefinition();

        def.setName("StudyInitializedEventHandler");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);

        TransactionStatus status = transactionManager.getTransaction(def);

        try {
            log.info("StudyInitializedEventHandler call");
            for(int i = 0; i < 1000; ++i) {
                String title = UUID.randomUUID().toString().substring(0, 10);
                String test_contents = contents[(int)(Math.random() * 3)];
                LocalDate endDate = LocalDate.now().plusDays((int)(Math.random() * 10));
                int groupSize = (int)(Math.random() * 20) + 1;
                Long region =  (long)(Math.random() * MyLocalCache.regions.size()) + 1;
                List<Long> categories = new ArrayList<>();
                long categoryNum = (long) (Math.random() * MyLocalCache.categoryItems.size()) + 1;
                for(long j = 0; j < categoryNum; ++j)
                    categories.add((long) (Math.random() *  MyLocalCache.categoryItems.size()) + 1);
                long id = (long) (Math.random() * 3) + 1;
                log.info("member id {}", id);
                studyService.createStudy(id, new StudyDTO(title, test_contents, endDate, groupSize, region, categories));

            }
            transactionManager.commit(status);

        } catch (Exception e) {
            transactionManager.rollback(status);
            throw e;
        }

    }
}
