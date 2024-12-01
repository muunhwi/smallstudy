package com.smallstudy.dto.member_study_form_dto;


import lombok.*;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Setter
@Getter
public class MemberStudyFormDTO {
    public Long studyId;
    public Long formId;
    public Long memberId;
    public Long questionCount;

    public List<MemberAnswerDTO> answers;


    public MemberStudyFormDTO(Long studyId, Long formId, Long memberId) {
        this.studyId = studyId;
        this.formId = formId;
        this.memberId = memberId;
    }

    public void addMemberAnswerDTO(MemberAnswerDTO answer) {

        if(answers == null)
            answers = new ArrayList<>();

        answers.add(answer);
    }

}
