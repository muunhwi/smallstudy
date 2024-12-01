package com.smallstudy.dto.study_dto;

import lombok.*;

import java.time.LocalDate;
import java.util.List;


@AllArgsConstructor
@NoArgsConstructor
@ToString
@Setter
@Getter
public class StudyDTO {
    public Long id;
    public String title;
    public String contents;
    public LocalDate endDate;
    public Integer groupSize;
    public Long region;
    public List<Long> categories;
    public String nickname;
    public String regionName;
    public List<String> categoriesName;


    public StudyDTO(String title, String contents, LocalDate endDate, Integer groupSize, Long region, List<Long> categories) {
        this.title = title;
        this.contents = contents;
        this.endDate = endDate;
        this.groupSize = groupSize;
        this.region = region;
        this.categories = categories;
    }

    public StudyDTO(String title, String contents, LocalDate endDate, Integer groupSize, Long region, List<Long> categories, String nickname) {
        this.title = title;
        this.contents = contents;
        this.endDate = endDate;
        this.groupSize = groupSize;
        this.region = region;
        this.categories = categories;
        this.nickname = nickname;
    }

    public StudyDTO(String title, String contents, LocalDate endDate, Integer groupSize, String nickname) {
        this.title = title;
        this.contents = contents;
        this.endDate = endDate;
        this.groupSize = groupSize;
        this.nickname = nickname;
    }
}
