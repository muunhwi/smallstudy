package com.smallstudy.domain.study_form_entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@DynamicUpdate
public class StudyFormQuestionItem {

    @GeneratedValue
    @Id
    private Long id;
    private String item;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_form_question_id")
    private StudyFormQuestion studyFormQuestion;

    public StudyFormQuestionItem(String item) {
        this.item = item;
    }

    public void editItem(String item) {
        this.item = item;
    }

}
