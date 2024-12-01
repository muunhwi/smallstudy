package com.smallstudy.dto.profile_dto;

import com.smallstudy.domain.region_entity.Region;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RegionDTO {

    Long code;
    String text;

    public RegionDTO(Long id, Region region) {
        code = id;
        text = region.toString();
    }
}
