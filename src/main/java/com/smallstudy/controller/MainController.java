package com.smallstudy.controller;



import com.smallstudy.dto.study_dto.StudySearchDTO;
import com.smallstudy.service.StudyService;
import com.smallstudy.utils.MyLocalCache;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


@Controller
@RequiredArgsConstructor
@Slf4j
public class MainController {

    @Value("${file.dir}")
    private String basePath;
    private final StudyService studyService;
    private static final String IMAGE_SUB_STRING_PATH = "/image/";

    @GetMapping("/")
    String root() {
        return "redirect:/main";
    }

    @GetMapping("/main")
    private String mainGet(@ModelAttribute("form") StudySearchDTO dto,
                           @PageableDefault(size = 10) Pageable pageable,
                           Model model) {

        model.addAttribute("selected",pageable.getPageNumber() + 1);
        model.addAttribute("endDate",pageable.getPageNumber() + 1);
        model.addAttribute("list", studyService.findAllStudiesWithPagination(dto, pageable));
        model.addAttribute("maxPage", pageable.getPageSize());
        model.addAttribute("categories", MyLocalCache.categoryItems);
        model.addAttribute("regions", MyLocalCache.regions);
        model.addAttribute("form", dto);
        return "main";
    }

    @GetMapping("/image/**")
    public ResponseEntity<byte[]> getProfileImage(HttpServletRequest request) throws IOException {

        String filePath = request.getRequestURI().substring(IMAGE_SUB_STRING_PATH.length());
        Path path = Paths.get(basePath, filePath);

        if(!Files.exists(path))
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        String mimeType = Files.probeContentType(path);
        byte[] imageBytes = Files.readAllBytes(path);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf(mimeType));

        return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
    }

}
