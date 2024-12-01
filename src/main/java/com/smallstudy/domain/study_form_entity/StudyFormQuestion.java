package com.smallstudy.domain.study_form_entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;


import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@DynamicUpdate
public class StudyFormQuestion {

    @GeneratedValue
    @Id
    private Long id;

    private String question;

    @Enumerated(EnumType.STRING)
    private QuestionType type;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_form_id")
    private StudyForm studyForm;

    @OneToMany(mappedBy = "studyFormQuestion", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StudyFormQuestionItem> studyFormQuestionItems = new ArrayList<>();

    public void editQuestion(String question) {
        this.question = question;
    }

    public void editType(QuestionType questionType) {
        this.type = questionType;
    }

    public StudyFormQuestion(String question, QuestionType type) {
        this.question = question;
        this.type = type;
    }

    public void addQuestionItem(StudyFormQuestionItem item) {
        item.setStudyFormQuestion(this);
        studyFormQuestionItems.add(item);
    }


}
