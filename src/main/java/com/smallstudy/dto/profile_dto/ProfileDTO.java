package com.smallstudy.dto.profile_dto;

import lombok.AllArgsConstructor;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ProfileDTO {

    public String username;
    public String nickname;
    public String message;

    public MultipartFile profileImage;
}
