package com.smallstudy.service;

import com.smallstudy.domain.member_entity.Application;
import com.smallstudy.domain.member_entity.Member;
import com.smallstudy.domain.member_study_form_entity.MemberStudyForm;
import com.smallstudy.domain.study_entity.Study;
import com.smallstudy.domain.study_form_entity.StudyForm;
import com.smallstudy.dto.profile_dto.ApplicationDTO;
import com.smallstudy.error.BadRequestException;
import com.smallstudy.repo.ApplicationRepository;
import com.smallstudy.repo.member_repo.MemberRepository;
import com.smallstudy.repo.member_study_form_repo.MemberStudyFormRepository;
import com.smallstudy.repo.study_repo.StudyRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.smallstudy.domain.member_entity.ApplicationStatus.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class ApplicationService {

    private final StudyRepository studyRepository;
    private final MemberRepository memberRepository;
    private final ApplicationRepository applicationRepository;
    private final MemberStudyFormRepository memberStudyFormRepository;

    @Transactional
    public void cancel(Long memberId, Long studyId) {
        Optional<Application> findApplication = applicationRepository.findApplicationByMemberIdAndStudyId(memberId, studyId);
        Application application = findApplication.orElseThrow(EntityNotFoundException::new);

        if(application.getStatus().equals(REJECTED)) {
            throw new BadRequestException();
        }

        Study study = application.getStudy();
        StudyForm studyForm = study.getStudyForm();

        if(Objects.nonNull(studyForm)) {
            Optional<MemberStudyForm> findMemberStudyForm = memberStudyFormRepository.findByMemberIdAndStudyFormId(memberId, studyForm.getId());
            MemberStudyForm memberStudyForm = findMemberStudyForm.orElseThrow(EntityNotFoundException::new);
            memberStudyFormRepository.deleteById(memberStudyForm.getId());
        }

        applicationRepository.deleteById(application.getId());
    }


    @Transactional(readOnly = true)
    public Page<ApplicationDTO> getApplicationsByMemberId(Long memberId, Pageable pageable) {

        List<Application> applications =
                applicationRepository.findApplicationByMemberIdOrderByLastModifiedDate(memberId, pageable);

        List<ApplicationDTO> list = applications.stream()
                .map(a -> {
                    Study study = a.getStudy();
                    StudyForm form = study.getStudyForm();
                    Long formId = null;

                    if(Objects.nonNull(form)) {
                        formId = form.getId();
                    }

                    return new ApplicationDTO(study.getTitle(), a.getApplicationDate(), a.getStatus().toString(), study.getId(), formId, memberId);
                }).toList();

        Long count = applicationRepository.countApplicationByMemberId(memberId);

        return new PageImpl<>(list, pageable, count);
    }

    @Transactional
    public void applied(Long memberId, Long studyId) {
        Optional<Study> findStudy = studyRepository.findById(studyId);
        Study study = findStudy.orElseThrow(EntityNotFoundException::new);

        Optional<Member> findMember = memberRepository.findById(memberId);
        Member member = findMember.orElseThrow(EntityNotFoundException::new);

        Application application = new Application(member, study, LocalDateTime.now(), APPLIED);
        applicationRepository.save(application);
    }

    @Transactional
    public void approve(Long memberId, Long studyId) {

        Optional<Application> findApplication
                = applicationRepository.findApplicationByMemberIdAndStudyId(memberId, studyId);
        Application application = findApplication.orElseThrow(EntityNotFoundException::new);
        application.setStatus(APPROVED);

    }

    @Transactional
    public void reject(Long memberId, Long studyId) {
        Optional<Application> findApplication
                = applicationRepository.findApplicationByMemberIdAndStudyId(memberId, studyId);
        Application application = findApplication.orElseThrow(EntityNotFoundException::new);
        application.setStatus(REJECTED);
    }

    @Transactional(readOnly = true)
    public boolean isMemberAlreadyApplied(Long memberId, Long studyId) {
        Long count = applicationRepository.countByMemberIdAndStudyId(memberId, studyId);
        return count > 0;
    }

}
