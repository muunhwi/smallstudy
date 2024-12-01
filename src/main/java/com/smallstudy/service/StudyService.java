package com.smallstudy.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smallstudy.domain.member_entity.Application;
import com.smallstudy.domain.member_entity.ApplicationStatus;
import com.smallstudy.domain.study_entity.*;
import com.smallstudy.domain.member_entity.Member;
import com.smallstudy.domain.region_entity.InterestRegion;
import com.smallstudy.domain.study_form_entity.StudyForm;
import com.smallstudy.dto.*;
import com.smallstudy.dto.member_dto.MemberDTO;
import com.smallstudy.dto.profile_dto.ProfileTableDTO;
import com.smallstudy.dto.study_dto.StudyDTO;
import com.smallstudy.dto.study_dto.StudySearchDTO;
import com.smallstudy.dto.study_dto.StudyViewDTO;
import com.smallstudy.error.IORuntimeException;
import com.smallstudy.repo.InterestRegionRepository;
import com.smallstudy.repo.study_repo.StudyCategoryItemRepository;
import com.smallstudy.repo.study_repo.StudyFileRepository;
import com.smallstudy.repo.category_repo.CategoryItemRepository;
import com.smallstudy.repo.member_repo.MemberRepository;
import com.smallstudy.repo.study_repo.StudyRepository;
import com.smallstudy.utils.FileService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.*;

import static com.smallstudy.domain.member_entity.ApplicationStatus.APPLIED;
import static com.smallstudy.domain.member_entity.ApplicationStatus.REJECTED;


@Service
@RequiredArgsConstructor
@Slf4j
public class StudyService {

    private final FileService fileService;
    private final ObjectMapper objectMapper;
    private final StudyRepository studyRepository;
    private final MemberRepository memberRepository;
    private final InterestRegionRepository interestRegionRepository;
    private final CategoryItemRepository categoryItemRepository;
    private final StudyCategoryItemRepository studyCategoryItemRepository;
    private final StudyFileRepository studyFileRepository;

    @Transactional(readOnly = true)
    public Study findStudyByStudyId(Long studyId) {
        Optional<Study> findStudy = studyRepository.findStudyWithMemberById(studyId);
        return findStudy.orElseThrow(EntityNotFoundException::new);
    }

    @Transactional(readOnly = true)
    public Study findStudyWithFilesAndStudyCategoryItemByStudyId(Long studyId) {
        Optional<Study> findStudy = studyRepository.findStudyWithFilesAndStudyCategoryItemByStudyId(studyId);
        Study study = findStudy.orElseThrow(EntityNotFoundException::new);

        study.getStudyFiles();
        List<StudyCategoryItem> studyCategoryItems = study.getStudyCategoryItems();
        studyCategoryItems.forEach(StudyCategoryItem::getCategoryItem);

        return study;
    }

    @Transactional(readOnly = true)
    public Page<StudyDTO> findAllStudiesWithPagination(StudySearchDTO condition, Pageable pageable) {

        Page<StudyDTO> list = studyRepository.findAllStudiesWithPagination(condition, pageable);
        for (StudyDTO dto : list)
            dto.contents = convertToSimpleString(convertToObject(dto.contents));

        return list;
    }

    @Transactional(readOnly = true)
    public Page<ProfileTableDTO> findStudiesWithPaginationByMemberId(Long memberId, Pageable pageable) {
        List<Study> studies = studyRepository.findStudiesByMemberIdOrderByApplicationStatusApplied(memberId, pageable);

        Long count = studyRepository.countStudyByMemberId(memberId);

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


        return new PageImpl<>(list, pageable, count);

    }

    @Transactional
    public Long createStudy(Long memberId, StudyDTO dto) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(EntityNotFoundException::new);

        Study study = new Study(dto.title, dto.groupSize, dto.endDate, member);
        createContentWithImages(study, dto.contents);

        InterestRegion region = interestRegionRepository.findById(dto.region)
                .orElseThrow(EntityNotFoundException::new);
        study.setInterestRegion(region);

        addStudyCategoryItems(study, dto.categories);

        return studyRepository.save(study).getId();
    }

    @Transactional
    public Long updateStudy(Long studyId, StudyDTO dto) {

        Study study = findStudyWithFilesAndStudyCategoryItemByStudyId(studyId);
        study.updateStudy(dto.title, dto.endDate, dto.groupSize);

        Study tempStudy = new Study();
        createContentWithImages(tempStudy, dto.contents);
        study.updateContentsAndFiles(tempStudy);

        Optional<InterestRegion> findRegion = interestRegionRepository.findById(dto.region);
        InterestRegion region = findRegion.orElseThrow(EntityNotFoundException::new);
        study.setInterestRegion(region);

        ArrayList<Long> newCategoryCodes = applyDiffCategoryCodes(dto, study);
        addStudyCategoryItems(study, newCategoryCodes);

        return study.getId();
    }

    @Transactional
    public void deleteStudy(Long studyId) {
        studyCategoryItemRepository.deleteByStudyId(studyId);
        studyFileRepository.deleteFilesByStudyId(studyId);
        studyRepository.deleteById(studyId);
    }

    private ArrayList<Long> applyDiffCategoryCodes(StudyDTO dto, Study study) {
        Set<Long> dtoCategorySet = toCategoryCodeSet(dto);
        Iterator<StudyCategoryItem> iterator = study.getStudyCategoryItems().iterator();

        while (iterator.hasNext()) {
            Long categoryItemCode = iterator.next().getCategoryItem().getId();

            if (dtoCategorySet.contains(categoryItemCode)) {
                dtoCategorySet.remove(categoryItemCode);
            } else {
                iterator.remove();
            }
        }
        return new ArrayList<>(dtoCategorySet);
    }

    private Set<Long> toCategoryCodeSet(StudyDTO dto) {
        return new HashSet<>(dto.categories);
    }

    private void createContentWithImages(Study study, String data) {
        List<Map<String, Object>> list = new LinkedList<>();
        List<Map<String, Object>> ops = convertToObject(data);

        for (Map<String, Object> op : ops) {
            if (op.size() < 2 && op.get("insert") instanceof Map<?, ?> map) {
                FileDTO fileDTO = fileService.saveQuillImageFile((Map<String, Object>) map, list);
                if (fileDTO != null)
                    study.addStudyFile(new StudyFile(fileDTO.path, fileDTO.uuid));
            } else {
                list.add(op);
            }
        }
        study.setContents(convertToJsonString(list));
    }

    private void addStudyCategoryItems(Study study, List<Long> categories) {
        categoryItemRepository.findCategoryItemByIds(categories).stream()
                .map(ci -> new StudyCategoryItem(study, ci))
                .forEach(study::addStudyCategoryItems);
    }

    public StudyViewDTO toStudyViewDTO(Study study) {
        StudyForm studyForm = study.getStudyForm();
        boolean isExistForm = Objects.nonNull(studyForm);

        return new StudyViewDTO(study.getId(),
                study.getTitle(),
                study.getContents(),
                study.getEndDate(),
                study.getGroupSize(),
                study.getLastModifiedDate(),
                isExistForm ? studyForm.getId() : null,
                isExistForm ? studyForm.getTitle() : ""
        );
    }

    private List<Map<String, Object>> convertToObject(String data) {

        try {
            return objectMapper.readValue(data, new TypeReference<List<Map<String, Object>>>() {});
        } catch (JsonMappingException e) {
            log.info("convertToObject : JsonMappingException");
            throw new IORuntimeException(e);
        } catch (JsonProcessingException ex) {
            log.info("convertToObject : JsonProcessingException");
            throw new IORuntimeException(ex);
        }
    }

    private String convertToJsonString(List<Map<String, Object>> deltaData) {
        try {
            return objectMapper.writeValueAsString(deltaData);
        } catch (JsonProcessingException e) {
            log.info("convertToJsonString : JsonProcessingException");
            throw new IORuntimeException(e);
        }
    }

    private String convertToSimpleString(List<Map<String, Object>> deltaData) {
        StringBuilder sb = new StringBuilder();
        for (Map<String, Object> delta : deltaData) {
            Object insert = delta.get("insert");
            if(insert instanceof String text) {
                sb.append(text);
            }
        }
        return sb.toString();
    }


}