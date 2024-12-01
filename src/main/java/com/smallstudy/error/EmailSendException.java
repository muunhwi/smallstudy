package com.smallstudy.error;

import lombok.Getter;

@Getter
public class EmailSendException extends RuntimeException {

    private Long memberId;

    public EmailSendException(Long memberId) {
        super();
        this.memberId = memberId;
    }

}
