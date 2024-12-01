package com.smallstudy.dto.member_study_form_dto;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Setter
@Getter
public class MemberAnswerDTO {
    public Long questionId;
    public List<Long> answerItemIds;
    public String text;
}
