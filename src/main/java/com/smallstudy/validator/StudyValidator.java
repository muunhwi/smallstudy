package com.smallstudy.validator;

import com.smallstudy.dto.member_study_form_dto.MemberAnswerDTO;
import com.smallstudy.dto.member_study_form_dto.MemberStudyFormDTO;
import com.smallstudy.dto.study_dto.*;
import com.smallstudy.dto.study_form_dto.StudyFormDTO;
import com.smallstudy.dto.study_form_dto.StudyFormQuestionDTO;
import com.smallstudy.dto.study_form_dto.StudyFormQuestionItemDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

@Component
@RequiredArgsConstructor
@Slf4j
public class StudyValidator {

    private final MessageSource messageSource;
    private final String quillEmpty = "[{\"insert\":\"\\n\"}]";
    public boolean validStudyDTO(StudyDTO dto, Map<String, String> error) {
        String title = dto.title;
        if(title.isBlank()) {
            error.put("error_title", messageSource.getMessage("required.form.title", null, Locale.getDefault()));
            return false;
        } else if(title.length() > 50) {
            error.put("error_title", messageSource.getMessage("invalid.form.title", null, Locale.getDefault()));
            return false;
        }

        Long region = dto.region;
        if(region == null) {
            error.put("error_region", messageSource.getMessage("required.form.region", null, Locale.getDefault()));
            return false;
        }

        List<Long> categories = dto.categories;
        if(categories == null || categories.isEmpty()) {
            error.put("error_categories", messageSource.getMessage("required.form.categories", null, Locale.getDefault()));
            return false;
        }

        String contents = dto.contents;
        if(contents == null || contents.isEmpty()) {
            error.put("error_contents", messageSource.getMessage("required.form.contents", null, Locale.getDefault()));
            return false;
        }

        if(contents.equals(quillEmpty)) {
            error.put("error_contents", messageSource.getMessage("required.form.contents", null, Locale.getDefault()));
            return false;
        }

        LocalDate endDate = dto.endDate;
        LocalDate today = LocalDate.now();
        if(endDate.isBefore(today)) {
            error.put("error_endDate", messageSource.getMessage("invalid.form.endDate", null, Locale.getDefault()));
            return false;
        }

        Integer groupSize = dto.groupSize;
        if(groupSize == null)
        {
            error.put("error_groupSize", messageSource.getMessage("required.form.groupSize", null, Locale.getDefault()));
            return false;
        }

        return true;
    }
    public boolean validStudyFormDTO(StudyFormDTO dto, Map<String, String> error) {

        if(dto.title.isBlank()) {
            error.put("error_title", messageSource.getMessage("required.form.title", null, Locale.getDefault()));
            return false;
        }

        if(dto.title.length() > 50) {
            error.put("error_title", messageSource.getMessage("invalid.form.title", null, Locale.getDefault()));
            return false;
        }

        if(dto.description.isBlank()) {
            error.put("error_description", messageSource.getMessage("required.form.description", null, Locale.getDefault()));
            return false;
        }

        if(dto.questions.isEmpty()) {
            error.put("error_form_question", messageSource.getMessage("required.form.question", null, Locale.getDefault()));
            return false;
        }

        for (StudyFormQuestionDTO question : dto.questions) {
            if(question.title.isBlank()) {
                error.put("error_form_title", messageSource.getMessage("required.form.title", null, Locale.getDefault()));
                error.put("itemSeq", question.itemSeq.toString());
                return false;
            }

            if(question.type.isBlank()) {
                error.put("error_form_type", messageSource.getMessage("required.form.type", null, Locale.getDefault()));
                error.put("itemSeq", question.itemSeq.toString());
                return false;
            }

            if(!question.type.equals("text")) {
                if(question.items.isEmpty()) {
                    error.put("error_form_contents", messageSource.getMessage("required.form.contents", null, Locale.getDefault()));
                    error.put("itemSeq", question.itemSeq.toString());
                    return false;
                }

                for (StudyFormQuestionItemDTO item : question.items) {
                    if(item.content.isBlank()) {
                        error.put("error_form_contents", messageSource.getMessage("required.form.contents", null, Locale.getDefault()));
                        error.put("itemSeq", question.itemSeq.toString());
                        return false;
                    }
                }
            }
        }

        return true;
    }
    public boolean validMemberStudyFormDTO(MemberStudyFormDTO dto, Map<String, String> error) {

        if(Objects.isNull(dto.getStudyId())) {
            error.put("error_id", messageSource.getMessage("required.form.id", null, Locale.getDefault()));
            return false;
        }

        if(Objects.isNull(dto.getFormId())) {
            error.put("error_id", messageSource.getMessage("required.form.id", null, Locale.getDefault()));
            return false;
        }

        List<MemberAnswerDTO> answers = dto.getAnswers();
        if(answers.size() != dto.getQuestionCount()) {
            error.put("error_answer", messageSource.getMessage("invalid.member.form.answer", null, Locale.getDefault()));
            return false;
        }

        for (MemberAnswerDTO answer : answers) {
            if(Objects.isNull(answer) ||  (answer.text.isBlank() && answer.answerItemIds.isEmpty())) {
                error.put("error_answer", messageSource.getMessage("invalid.member.form.answer", null, Locale.getDefault()));
                return false;
            }
        }

        return true;

    }

}
