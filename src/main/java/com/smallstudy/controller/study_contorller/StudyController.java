package com.smallstudy.controller.study_contorller;


import com.smallstudy.domain.study_entity.Study;
import com.smallstudy.domain.study_entity.StudyCategoryItem;
import com.smallstudy.dto.study_dto.StudyDTO;
import com.smallstudy.dto.profile_dto.CategoryItemDTO;
import com.smallstudy.dto.profile_dto.RegionDTO;
import com.smallstudy.security.CustomUser;
import com.smallstudy.service.*;
import com.smallstudy.utils.MyLocalCache;
import com.smallstudy.validator.GlobalValidationService;
import com.smallstudy.validator.StudyValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MaxUploadSizeExceededException;


import java.time.LocalDate;
import java.util.*;

@Controller
@Slf4j
@RequiredArgsConstructor
public class StudyController {

    private final StudyService studyService;
    private final CategoryItemService categoryItemService;
    private final InterestRegionService interestRegionService;
    private final MessageSource messageSource;
    private final StudyValidator studyValidator;
    private final ApplicationService applicationService;


    @GetMapping("/study")
    public String studyGet(@AuthenticationPrincipal CustomUser member,
                           Model model) {

        setSelectedCategoriesAndRegions(model, member.getId());
        model.addAttribute("form", new StudyDTO());
        model.addAttribute("today", LocalDate.now());
        return "smallstudy/study/study";
    }

    @PostMapping("/study")
    public ResponseEntity<Map<String, String>> studyPost(@ModelAttribute("form") StudyDTO dto,
                                                         @AuthenticationPrincipal CustomUser member) {
        ResponseEntity<Map<String, String>> errors = validStudyDTO(dto);
        if (errors != null) return errors;

        Long studyId = studyService.createStudy(member.getId(), dto);
        return ResponseEntity.ok(Map.of("studyId", String.valueOf(studyId)));

    }

    @GetMapping("/study/{studyId}")
    public String studyGetWithId(@PathVariable("studyId") Long studyId,
                                 @AuthenticationPrincipal CustomUser member,
                                 Model model) {

        Study study = studyService.findStudyByStudyId(studyId);
        Long authorMemberId = study.getMember().getId();

        boolean isAuthor = Objects.nonNull(member) && member.getId().equals(authorMemberId);
        boolean isUser   = Objects.nonNull(member);

        boolean canApply = true;
        if(Objects.nonNull(member)) {
            canApply = !applicationService.isMemberAlreadyApplied(member.getId(), studyId);
        }

        if(canApply && LocalDate.now().isAfter(study.getEndDate())) {
            canApply = false;
        }

        model.addAttribute("isAuthor", isAuthor);
        model.addAttribute("isUser", isUser);
        model.addAttribute("canApply", canApply);
        model.addAttribute("study", studyService.toStudyViewDTO(study));
        model.addAttribute("member", study.getMember());

        return "smallstudy/study/study_view";
    }

    @GetMapping("/study/{studyId}/update")
    public String studyUpdateWithId(@PathVariable("studyId") Long studyId,
                                    @AuthenticationPrincipal CustomUser member,
                                    Model model) {


        Study study = studyService.findStudyWithFilesAndStudyCategoryItemByStudyId(studyId);

        GlobalValidationService.isAuthor(study.getMember().getId(), member.getId());

        setSelectedCategoriesAndRegions(model, member.getId());
        model.addAttribute("studySelectedCategories", StudyCategoryItem.convertToLongList(study.getStudyCategoryItems()));
        model.addAttribute("studySelectedRegion", InterestRegionService.toRegionDTO(study.getInterestRegion()).getCode());
        model.addAttribute("study",  studyService.toStudyViewDTO(study));

        return "smallstudy/study/study_update";
    }

    @PostMapping("/study/{studyId}/update")
    public ResponseEntity<Map<String, String>> studyUpdatePostWithId(@PathVariable("studyId") Long studyId,
                                                                     @ModelAttribute("form") StudyDTO dto) {
        ResponseEntity<Map<String, String>> errors = validStudyDTO(dto);
        if (errors != null) return errors;
        Long updateId = studyService.updateStudy(studyId, dto);
        return ResponseEntity.ok(Map.of("studyId", String.valueOf(updateId)));
    }

    @GetMapping("/study/{studyId}/delete")
    public String studyDeletePostWithId(@PathVariable("studyId") Long studyId,
                                        @AuthenticationPrincipal CustomUser member) {

        Study study = studyService.findStudyByStudyId(studyId);
        GlobalValidationService.isAuthor(study.getMember().getId(), member.getId());

        studyService.deleteStudy(studyId);

        return "redirect:/main";
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Map<String, String>> handleMaxUploadSizeExceededException() {
        Map<String, String> error= new HashMap<>();
        error.put("error_contents", messageSource.getMessage("unsupported.file.size", null, Locale.getDefault()));
        return ResponseEntity.badRequest().body(error);
    }

    private ResponseEntity<Map<String, String>> validStudyDTO(StudyDTO dto) {
        Map<String, String> errors = new HashMap<>();
        boolean isOk = studyValidator.validStudyDTO(dto, errors);
        if(!isOk) return ResponseEntity.badRequest().body(errors);
        return null;
    }

    private void setSelectedCategoriesAndRegions(Model model,Long memberId) {
        List<CategoryItemDTO> selectedCategories = categoryItemService.findCategoryItemDTOsByMemberId(memberId);
        List<RegionDTO> selectedRegions = interestRegionService.getMemberSelectedRegions(memberId);

        model.addAttribute("categories", selectedCategories.isEmpty()
                ? MyLocalCache.categoryItems
                : CategoryItemService.filteredCategoryDTOs(MyLocalCache.categoryItems, selectedCategories));

        model.addAttribute("regions", selectedRegions.isEmpty()
                ? MyLocalCache.regions
                : InterestRegionService.filteredRegionDTOs(MyLocalCache.regions, selectedRegions));

        model.addAttribute("selectedCategories", selectedCategories);
        model.addAttribute("selectedRegions", selectedRegions);
    }


}
