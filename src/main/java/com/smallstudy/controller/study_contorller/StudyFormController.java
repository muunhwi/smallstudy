package com.smallstudy.controller.study_contorller;

import com.smallstudy.domain.study_entity.Study;
import com.smallstudy.dto.study_form_dto.StudyFormDTO;
import com.smallstudy.security.CustomUser;
import com.smallstudy.service.StudyFormService;
import com.smallstudy.utils.JsonConverter;
import com.smallstudy.validator.GlobalValidationService;
import com.smallstudy.validator.StudyValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
@Slf4j
@RequiredArgsConstructor
public class StudyFormController {

    private final StudyFormService studyFormService;
    private final StudyValidator studyValidator;

    @GetMapping("/study/form")
    public String studyForm(@RequestParam("studyId") Long studyId, Model model){
        model.addAttribute("studyId", studyId);
        model.addAttribute("form", new StudyFormDTO());
        return "smallstudy/study_form/study_form";
    }


    @GetMapping("/study/form/view")
    public String studyFormView(@RequestParam("formId") Long formId,
                                @AuthenticationPrincipal CustomUser member,
                                Model model)  {

        Study study = studyFormService.findStudyByFormId(formId);
        GlobalValidationService.isAuthor(study.getMember().getId(), member.getId());
        StudyFormDTO dto = studyFormService.toStudyFormDTO(study.getStudyForm(), study.getId());
        model.addAttribute("form", JsonConverter.getJsonData(dto));

        return "smallstudy/study_form/study_form_sample";
    }


    @PostMapping("/study/form")
    public ResponseEntity<Map<String, String>> submitForm(@RequestBody StudyFormDTO form) {

        ResponseEntity<Map<String, String>> errors = validStudyFormDTO(form);
        if (errors != null) return errors;

        Long studyId = studyFormService.save(form);
        return ResponseEntity.ok(Map.of("studyId", String.valueOf(studyId)));
    }



    private ResponseEntity<Map<String, String>> validStudyFormDTO(StudyFormDTO dto) {
        Map<String, String> errors = new HashMap<>();
        return studyValidator.validStudyFormDTO(dto, errors)
                ? null
                : ResponseEntity.badRequest().body(errors);
    }



}
