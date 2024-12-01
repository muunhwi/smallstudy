package com.smallstudy.utils;

import com.smallstudy.dto.profile_dto.CategoryItemDTO;
import com.smallstudy.dto.profile_dto.RegionDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;


import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class MyLocalCache {

    public static List<CategoryItemDTO> categoryItems;
    public static List<RegionDTO> regions;

}
