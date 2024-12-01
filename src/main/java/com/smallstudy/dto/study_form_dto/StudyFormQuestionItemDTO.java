package com.smallstudy.dto.study_form_dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Setter
@Getter
public class StudyFormQuestionItemDTO {
    public Long id;
    public String content;

    public StudyFormQuestionItemDTO(String content) {
        this.content = content;
    }
}
