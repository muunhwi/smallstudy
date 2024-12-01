package com.smallstudy.domain.member_study_form_entity;

import com.smallstudy.domain.member_entity.Member;
import com.smallstudy.domain.study_form_entity.StudyForm;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class MemberStudyForm {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_form_id")
    private StudyForm studyForm;

    public MemberStudyForm(Member member, StudyForm studyForm) {
        this.member = member;
        this.studyForm = studyForm;
    }


    @OneToMany(mappedBy = "memberStudyForm", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MemberStudyFormSelectedQuestionItem> memberStudyFormSelectedQuestionItems = new ArrayList<>();

    @OneToMany(mappedBy = "memberStudyForm", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MemberStudyFormQuestionTextAnswer> memberStudyFormQuestionTextAnswers = new ArrayList<>();

    public void addMemberStudyFormSelectQuestionItem(MemberStudyFormSelectedQuestionItem item) {
        memberStudyFormSelectedQuestionItems.add(item);
    }

    public void addMemberStudyFormQuestionTextAnswer(MemberStudyFormQuestionTextAnswer answer) {
        memberStudyFormQuestionTextAnswers.add(answer);
    }

    public void clear() {
        this.memberStudyFormQuestionTextAnswers.clear();
        this.memberStudyFormSelectedQuestionItems.clear();
    }

}
