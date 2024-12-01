package com.smallstudy.dto.profile_dto;

import com.smallstudy.dto.member_dto.MemberDTO;
import com.smallstudy.utils.StaticFormatter;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@ToString
@Setter
@Getter
public class ProfileTableDTO {

    public Long studyId;
    public Long formId;
    public List<MemberDTO> applicants;
    public List<MemberDTO> rejectedApplicants;
    public List<MemberDTO> approvalApplicants;


    public String title;
    public LocalDateTime lastModifiedDate;
    public String lastModifiedString;

    public LocalDate endDate;

    public ProfileTableDTO(Long studyId, Long formId,
                           List<MemberDTO> applicants,
                           List<MemberDTO> rejectedApplicants,
                           List<MemberDTO> approvalApplicants,
                           String title,
                           LocalDateTime lastModifiedDate,
                           LocalDate endDate) {
        this.studyId = studyId;
        this.formId = formId;
        this.applicants = applicants;
        this.rejectedApplicants = rejectedApplicants;
        this.approvalApplicants = approvalApplicants;
        this.title = title;
        this.lastModifiedDate = lastModifiedDate;
        this.lastModifiedString = StaticFormatter.TIME_YMD_FORMATTER.format(lastModifiedDate);
        this.endDate = endDate;
    }
}
