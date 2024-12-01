package com.smallstudy.dto.profile_dto;

import com.smallstudy.utils.StaticFormatter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationDTO {

    String title;
    String applicationDateString;
    String status;
    String statusString;
    Long studyId;
    Long formId;
    Long memberId;

    LocalDateTime applicationDate;

    public ApplicationDTO(String title, LocalDateTime applicationDate, String status, Long studyId, Long formId, Long memberId) {
        this.title = title;
        this.applicationDateString = StaticFormatter.TIME_FORMATTER.format(applicationDate);
        this.applicationDate = applicationDate;
        this.status = status;
        this.studyId = studyId;
        this.formId = formId;
        this.memberId = memberId;
    }

    private void statusToString(String status) {
        switch (status) {
            case "REJECTED":
                this.statusString = "거부";
                break;
            case "APPROVED":
                this.statusString = "승인";
                break;
            case "APPLIED":
                this.statusString = "신청";
                break;
        }
    }
}
