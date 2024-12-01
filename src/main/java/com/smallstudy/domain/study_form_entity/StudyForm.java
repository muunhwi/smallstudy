package com.smallstudy.domain.study_form_entity;

import com.smallstudy.domain.member_study_form_entity.MemberStudyForm;
import com.smallstudy.domain.study_entity.Study;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@DynamicUpdate
public class StudyForm {

    @GeneratedValue @Id
    private Long id;
    private String title;
    private String description;

    @OneToOne(mappedBy = "studyForm")
    private Study study;

    @OneToMany(mappedBy = "studyForm", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StudyFormQuestion> studyFormQuestions = new ArrayList<>();

    @OneToMany(mappedBy = "studyForm", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MemberStudyForm> memberStudyForms = new ArrayList<>();

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdDate;

    @LastModifiedDate
    private LocalDateTime lastModifiedDate;

    public StudyForm(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public void editForm(String title, String description) {
        this.title =title;
        this.description= description;
    }

    public void addStudyFormQuestion(StudyFormQuestion question) {
        question.setStudyForm(this);
        studyFormQuestions.add(question);
    }
}
