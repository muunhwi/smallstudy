package com.smallstudy.dto.study_dto;

import com.smallstudy.utils.StaticFormatter;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@NoArgsConstructor
@ToString
@Setter
@Getter
public class StudyViewDTO {
    public Long id;
    public String title;
    public String contents;
    public LocalDate endDate;
    public Integer groupSize;

    public LocalDateTime lastModifiedDate;
    public String lastModifiedString;
    public Long formId;
    public String formTitle;

    public StudyViewDTO(Long id, String title, String contents, LocalDate endDate, Integer groupSize, LocalDateTime lastModifiedDate, Long formId, String formTitle) {
        this.id = id;
        this.title = title;
        this.contents = contents;
        this.endDate = endDate;
        this.groupSize = groupSize;
        this.lastModifiedDate = lastModifiedDate;
        this.lastModifiedString = StaticFormatter.TIME_FORMATTER.format(lastModifiedDate);
        this.formId = formId;
        this.formTitle = formTitle;
    }


}
