package com.smallstudy.dto.member_dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EmailSendErrorDTO {

    private int errorCode;
    private String msg;
}
