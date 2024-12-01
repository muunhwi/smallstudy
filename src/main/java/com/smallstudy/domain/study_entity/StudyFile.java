package com.smallstudy.domain.study_entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class StudyFile {

    @Id @GeneratedValue
    Long id;

    private String imgPath;
    private String imgUuid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_id")
    @Setter
    private Study study;

    public StudyFile(String imgPath, String imgUuid) {
        this.imgPath = imgPath;
        this.imgUuid = imgUuid;
    }


}
