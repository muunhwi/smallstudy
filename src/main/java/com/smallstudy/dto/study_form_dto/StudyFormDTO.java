package com.smallstudy.dto.study_form_dto;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Setter
@Getter
public class StudyFormDTO {

    public Long id;
    public Long studyId;
    public String title;
    public String description;
    public List<StudyFormQuestionDTO> questions;


    public StudyFormDTO(Long id, String title, String description, List<StudyFormQuestionDTO> list,  Long studyId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.questions = list;
        this.studyId = studyId;
    }

    public StudyFormDTO(Long studyId, String title, String description, List<StudyFormQuestionDTO> questions) {
        this.studyId = studyId;
        this.title = title;
        this.description = description;
        this.questions = questions;
    }

    public void addStudyFormItem(StudyFormQuestionDTO item) {
        questions.add(item);
    }
}
