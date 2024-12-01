package com.smallstudy.repo.study_repo;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.smallstudy.domain.category_entity.CategoryItem;
import com.smallstudy.domain.member_entity.Application;
import com.smallstudy.domain.member_entity.ApplicationStatus;
import com.smallstudy.domain.member_entity.Member;
import com.smallstudy.domain.member_entity.QApplication;
import com.smallstudy.domain.study_entity.Study;
import com.smallstudy.domain.study_entity.StudyCategoryItem;
import com.smallstudy.domain.study_form_entity.StudyForm;
import com.smallstudy.dto.member_dto.MemberDTO;
import com.smallstudy.dto.profile_dto.ProfileTableDTO;
import com.smallstudy.dto.study_dto.StudyDTO;
import com.smallstudy.dto.study_dto.StudySearchDTO;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static com.smallstudy.domain.member_entity.ApplicationStatus.*;
import static com.smallstudy.domain.member_entity.QApplication.application;
import static com.smallstudy.domain.study_entity.QStudy.study;
import static com.smallstudy.domain.study_entity.QStudyCategoryItem.studyCategoryItem;

@Slf4j
public class StudyRepositoryImpl implements StudyRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    public StudyRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);

    }
    @Override
    @Transactional(readOnly = true)
    public Page<StudyDTO> findAllStudiesWithPagination(StudySearchDTO condition, Pageable pageable) {

        List<StudyDTO> studyDTOs = queryFactory
                .select(study)
                .from(study)
                .leftJoin(study.interestRegion).fetchJoin()
                .leftJoin(study.member).fetchJoin()
                .where(titleCondition(condition.getTitle()),
                        regionCodeCondition(condition.getRegionCode()),
                        endDateCondition(condition.getEndDate()),
                        groupSizeCondition(condition.getGroupSize()),
                        categoryCodeConditionBySubQuery(condition.getCategoryCode()))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(study.createdDate.desc(), study.endDate.desc())
                .fetch()
                .stream()
                .map(study -> new StudyDTO(
                        study.getId(),
                        study.getTitle(),
                        study.getContents(),
                        study.getEndDate(),
                        study.getGroupSize(),
                        study.getInterestRegion().getId(),
                        study.getStudyCategoryItems().stream()
                                .map(StudyCategoryItem::getCategoryItem)
                                .map(CategoryItem::getId)
                                .toList(),
                        study.getMember().getNickname(),
                        study.getInterestRegion().getRegion().toString(),
                        study.getStudyCategoryItems().stream().map(sci -> sci.getCategoryItem().getCategoryItemName()).toList()
                ))
                .toList();

        Long total = queryFactory.select(study.count())
                .from(study)
                .where(titleCondition(condition.getTitle()),
                        regionCodeCondition(condition.getRegionCode()),
                        endDateCondition(condition.getEndDate()),
                        groupSizeCondition(condition.getGroupSize()),
                        categoryCodeConditionBySubQuery(condition.getCategoryCode()))
                .fetchOne();


        return new PageImpl<>(studyDTOs, pageable, total);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProfileTableDTO> findStudiesWithPaginationByMemberId(Long memberId, Pageable pageable) {
        List<Study> studies = queryFactory
                .select(study)
                .from(study)
                .leftJoin(study.interestRegion).fetchJoin()
                .leftJoin(study.member).fetchJoin()
                .leftJoin(study.studyForm).fetchJoin()
                .where(study.member.id.eq(memberId))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory.select(study.count())
                .from(study)
                .where(study.member.id.eq(memberId))
                .fetchOne();

        List<ProfileTableDTO> list = studies.stream()
                .map(study -> {

                    Long formId = Optional.ofNullable(study.getStudyForm())
                            .map(StudyForm::getId)
                            .orElse(null);

                    List<MemberDTO> applicants = new ArrayList<>();
                    List<MemberDTO> rejectedApplicants = new ArrayList<>();
                    List<MemberDTO> approvalApplicants = new ArrayList<>();

                    List<Application> applications = study.getApplications();
                    for (Application application : applications) {

                        ApplicationStatus status = application.getStatus();
                        Member member = application.getMember();
                        MemberDTO memberDTO = new MemberDTO(member.getId(), member.getNickname());
                        if(status.equals(APPLIED)) {
                            applicants.add(memberDTO);
                        }
                        else if(status.equals(REJECTED)) {
                            rejectedApplicants.add(memberDTO);
                        }
                        else {
                            approvalApplicants.add(memberDTO);
                        }

                    }

                    return new ProfileTableDTO(study.getId(),
                            formId,
                            applicants,
                            rejectedApplicants,
                            approvalApplicants,
                            study.getTitle(),
                            study.getLastModifiedDate(),
                            study.getEndDate()
                    );
                })
                .toList();


        return new PageImpl<>(list, pageable, total);
    }

    private BooleanExpression titleCondition(String title) {
        return (title != null && !title.isEmpty()) ? study.title.like("%" + title + "%") : null;
    }

    private BooleanExpression regionCodeCondition(Long regionCode) {
        return regionCode != null ? study.interestRegion.id.eq(regionCode) : null;
    }

    private BooleanExpression endDateCondition(LocalDate endDate) {
        return endDate != null ? study.endDate.loe(endDate) : null;
    }

    private BooleanExpression groupSizeCondition(Long groupSize) {
        return groupSize != null ? study.groupSize.loe(Math.toIntExact(groupSize)) : null;
    }

    private BooleanExpression categoryCodeConditionBySubQuery(Long categoryCode) {
        return categoryCode != null ? JPAExpressions.selectFrom(studyCategoryItem)
                .where(studyCategoryItem.study.id.eq(study.id)
                        .and(studyCategoryItem.categoryItem.id.eq(categoryCode)))
                .exists() : null;
    }

}


