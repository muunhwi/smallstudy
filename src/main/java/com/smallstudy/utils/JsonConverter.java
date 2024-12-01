package com.smallstudy.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smallstudy.error.IORuntimeException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JsonConverter {
    @Getter
    private final static ObjectMapper mapper = new ObjectMapper();

    private JsonConverter() {
    }

    public static String getJsonData(Object object) {
        try {
            return JsonConverter.getMapper().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.info("toJsonData : JsonProcessingException");
            throw new IORuntimeException(e);
        }
    }

}
