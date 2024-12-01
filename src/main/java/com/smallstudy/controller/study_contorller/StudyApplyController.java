package com.smallstudy.controller.study_contorller;

import com.smallstudy.domain.member_entity.Member;
import com.smallstudy.domain.study_entity.Study;
import com.smallstudy.dto.member_study_form_dto.MemberStudyFormDTO;
import com.smallstudy.error.BadRequestException;
import com.smallstudy.error.RuntimeAccessDeniedException;
import com.smallstudy.security.CustomUser;
import com.smallstudy.service.ApplicationService;
import com.smallstudy.service.MemberStudyFormService;
import com.smallstudy.service.StudyFormService;
import com.smallstudy.service.StudyService;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


@Controller
@Slf4j
@RequiredArgsConstructor
public class StudyApplyController {

    private final StudyFormService studyFormService;
    private final StudyService studyService;
    private final StudyValidator studyValidator;
    private final MemberStudyFormService memberStudyFormService;
    private final ApplicationService applicationService;


    @GetMapping("/study/apply")
    public String studyApply(@RequestParam("studyId") Long studyId,
                             @RequestParam("formId") Long formId,
                             @AuthenticationPrincipal CustomUser member,
                             RedirectAttributes redirectAttributes,
                             Model model){

        Study study = studyFormService.findStudyByFormId(formId);

        if(isOwnApplication(member, study))
            throw new BadRequestException("잘못된 요청입니다.");

        if(applicationService.isMemberAlreadyApplied(member.getId(), studyId))
            throw new BadRequestException("이미 등록 되었습니다.");

        if(LocalDate.now().isAfter(study.getEndDate())) {
            redirectAttributes.addFlashAttribute("deadLine", "yes");
            return "redirect:/study/" + studyId;
        }

        String data = JsonConverter.getJsonData(studyFormService.toStudyFormDTO(study.getStudyForm(), studyId));
        model.addAttribute("form", data);
        model.addAttribute("deadLine", "no");


        return "smallstudy/study_form/study_form_view";


    }

    @GetMapping("/study/apply/no-form")
    public String studyApplyNoForm(@RequestParam("studyId") Long studyId,
                                   @AuthenticationPrincipal CustomUser member,
                                   RedirectAttributes redirectAttributes
    ){

        Study study = studyService.findStudyByStudyId(studyId);

        if(isOwnApplication(member, study))
            throw new BadRequestException("잘못된 요청입니다.");

        if(applicationService.isMemberAlreadyApplied(member.getId(), studyId))
            throw new BadRequestException("잘못된 요청입니다.");

        if(LocalDate.now().isAfter(study.getEndDate())) {
            redirectAttributes.addFlashAttribute("deadLine", "yes");
        } else {
            redirectAttributes.addFlashAttribute("deadLine", "no");
            applicationService.applied(member.getId(), studyId);
        }

        return "redirect:/study/" + studyId;
    }

    @PostMapping("/study/apply")
    public ResponseEntity<Map<String, String>> studyApplyPost(@RequestBody MemberStudyFormDTO memberStudy,
                                                              @AuthenticationPrincipal CustomUser member)
    {

        ResponseEntity<Map<String, String>> errors = validMemberStudyDTO(memberStudy);
        if (errors != null) return errors;

        if(Objects.isNull(member)) {
            log.info("studyApplyPost memberId is null");
            throw new BadRequestException();
        }

        memberStudyFormService.save(memberStudy, member.getId());
        applicationService.applied(member.getId(), memberStudy.studyId);

        return ResponseEntity.ok(Map.of("studyId", String.valueOf(memberStudy.getStudyId())));
    }

    @GetMapping("/study/apply/view")
    public String studyApplyView(@RequestParam("formId") Long formId,
                                 @RequestParam("memberId") Long memberId,
                                 @RequestParam("page") Long pageNumber,
                                 Model model) {

        String[] jsonData = memberStudyFormService.getJsonDataStudyFormAndAnswer(memberId, formId);
        model.addAttribute("form", jsonData[0]);
        model.addAttribute("answers", jsonData[1]);
        model.addAttribute("page", pageNumber);

        return "smallstudy/study_form/study_form_apply";
    }

    @GetMapping("/study/apply/view/readonly")
    public String studyApplyViewReadOnly(@RequestParam("formId") Long formId,
                                         @RequestParam("memberId") Long memberId,
                                 Model model) {



        String[] jsonData = memberStudyFormService.getJsonDataStudyFormAndAnswer(memberId, formId);
        model.addAttribute("form", jsonData[0]);
        model.addAttribute("answers", jsonData[1]);

        return "smallstudy/study_form/study_form_apply_view";
    }

    @GetMapping("/study/applicant/approve")
    public ResponseEntity<Map<String, Long>> approve(@RequestParam("studyId") Long studyId,
                                                     @RequestParam("memberId") Long memberId,
                                                     @AuthenticationPrincipal CustomUser member,
                                                     @RequestParam("page") Long pageNumber) {

        isAuthor(studyId, member);
        applicationService.approve(memberId, studyId);
        Map<String, Long> returnValue = new HashMap<>();
        returnValue.put("page", pageNumber);

        return ResponseEntity.ok(returnValue);
    }

    @GetMapping("/study/applicant/reject")
    public ResponseEntity<Map<String, Long>> reject(@RequestParam("studyId") Long studyId,
                                                    @RequestParam("memberId") Long memberId,
                                                    @AuthenticationPrincipal CustomUser member,
                                                    @RequestParam("page") Long pageNumber) {

        isAuthor(studyId, member);
        applicationService.reject(memberId, studyId);
        Map<String, Long> returnValue = new HashMap<>();
        returnValue.put("page", pageNumber);

        return ResponseEntity.ok(returnValue);
    }

    @GetMapping("/study/applicant/approve-view")
    public String approveHtml(@RequestParam("studyId") Long studyId,
                              @RequestParam("memberId") Long memberId,
                              @AuthenticationPrincipal CustomUser member,
                              @RequestParam("page") Long pageNumber) {

        isAuthor(studyId, member);
        applicationService.approve(memberId, studyId);
        return "redirect:/profile/table?page="+ pageNumber;
    }

    @GetMapping("/study/applicant/reject-view")
    public String rejectHtml(@RequestParam("studyId") Long studyId,
                             @RequestParam("memberId") Long memberId,
                             @AuthenticationPrincipal CustomUser member,
                             @RequestParam("page") Long pageNumber) {

        isAuthor(studyId, member);
        applicationService.reject(memberId, studyId);
        return "redirect:/profile/table?page="+ pageNumber;
    }


    @GetMapping("/study/applicant/cancel")
    public String cancel(@RequestParam("studyId") Long studyId,
                         @RequestParam("page") Long pageNumber,
                         @AuthenticationPrincipal CustomUser member) {

        Long memberId = member.getId();
        if(!applicationService.isMemberAlreadyApplied(memberId, studyId)) {
            throw new BadRequestException("신청하지 않은 사용자 요청");
        }

        applicationService.cancel(memberId, studyId);
        return "redirect:/profile/apply?page=" + pageNumber;
    }

    private void isAuthor(Long studyId, Member member) {
        Study study = studyService.findStudyByStudyId(studyId);
        if(Objects.isNull(member)) {
            throw new RuntimeAccessDeniedException("approve-view -> Access Denied");
        } else {
            GlobalValidationService.isAuthor(study.getMember().getId(), member.getId());
        }
    }

    private ResponseEntity<Map<String, String>> validMemberStudyDTO(MemberStudyFormDTO dto) {
        Map<String, String> errors = new HashMap<>();
        return studyValidator.validMemberStudyFormDTO(dto, errors)
                ? null
                : ResponseEntity.badRequest().body(errors);
    }

    private boolean isOwnApplication(CustomUser member, Study study) {
        return study.getMember().getId().equals(member.getId());
    }

}
