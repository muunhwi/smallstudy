package com.smallstudy.repo.study_repo;

import com.smallstudy.dto.profile_dto.ProfileTableDTO;
import com.smallstudy.dto.study_dto.StudyDTO;
import com.smallstudy.dto.study_dto.StudySearchDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface StudyRepositoryCustom {

    Page<StudyDTO> findAllStudiesWithPagination(StudySearchDTO condition, Pageable pageable);
    Page<ProfileTableDTO> findStudiesWithPaginationByMemberId(Long memberId, Pageable pageable);

}
