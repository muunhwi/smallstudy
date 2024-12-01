package com.smallstudy.domain.study_entity;


import com.smallstudy.domain.member_entity.Application;
import com.smallstudy.domain.member_entity.Member;
import com.smallstudy.domain.region_entity.InterestRegion;
import com.smallstudy.domain.study_form_entity.StudyForm;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Study {

    @Id @GeneratedValue
    private Long id;
    private String title;
    @Column(columnDefinition = "TEXT")
    @Setter
    private String contents;
    private Integer groupSize;

    private LocalDate endDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id")
    @Setter
    private InterestRegion interestRegion;

    @OneToMany(mappedBy = "study", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<StudyCategoryItem> studyCategoryItems = new ArrayList<>();

    @OneToMany(mappedBy = "study", cascade = CascadeType.PERSIST)
    private List<StudyFile> studyFiles = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "study_form")
    @Setter
    private StudyForm studyForm;

    @OneToMany(mappedBy = "study", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Application> applications = new ArrayList<>();

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdDate;

    @LastModifiedDate
    private LocalDateTime lastModifiedDate;

    public Study(String title, Member member) {
        this.title = title;
        this.member = member;
    }

    public Study(String title, String contents, Member member) {
        this.title = title;
        this.contents = contents;
        this.member = member;
    }

    public Study(String title, Integer groupSize, LocalDate endDate, Member member) {
        this.title = title;
        this.contents = contents;
        this.groupSize = groupSize;
        this.endDate = endDate;
        this.member = member;
    }

    public void addStudyCategoryItems(StudyCategoryItem categoryItem) {
        this.studyCategoryItems.add(categoryItem);
        categoryItem.setStudy(this);
    }

    public void addStudyFile(StudyFile file) {
        this.studyFiles.add(file);
        file.setStudy(this);
    }

    public void updateContentsAndFiles(Study study) {
        this.contents = study.contents;
        this.studyFiles.clear();
        for(StudyFile file  : study.studyFiles) {
            addStudyFile(file);
        }
    }

    public void updateStudy(String title, LocalDate endDate, Integer groupSize) {
        this.title =title;
        this.endDate =endDate;
        this.groupSize = groupSize;
    }


}
