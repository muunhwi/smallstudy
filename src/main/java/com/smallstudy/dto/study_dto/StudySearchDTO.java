package com.smallstudy.dto.study_dto;


import lombok.*;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Setter
@Getter
public class StudySearchDTO {

    public String title;
    public Long categoryCode;
    public Long regionCode;
    public LocalDate endDate;
    public Long groupSize;

}
