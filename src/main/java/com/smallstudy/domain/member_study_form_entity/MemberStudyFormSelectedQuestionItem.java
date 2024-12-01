package com.smallstudy.domain.member_study_form_entity;

import com.smallstudy.domain.study_form_entity.StudyFormQuestion;
import com.smallstudy.domain.study_form_entity.StudyFormQuestionItem;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class MemberStudyFormSelectedQuestionItem {

    @GeneratedValue
    @Id
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_study_form_id")
    private MemberStudyForm memberStudyForm;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_form_question_id")
    private StudyFormQuestion studyFormQuestion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_form_question_item_id")
    private StudyFormQuestionItem studyFormQuestionItem;

    public MemberStudyFormSelectedQuestionItem(MemberStudyForm memberStudyForm, StudyFormQuestion studyFormQuestion, StudyFormQuestionItem studyFormQuestionItem) {
        this.memberStudyForm = memberStudyForm;
        this.studyFormQuestion = studyFormQuestion;
        this.studyFormQuestionItem = studyFormQuestionItem;
    }
}
