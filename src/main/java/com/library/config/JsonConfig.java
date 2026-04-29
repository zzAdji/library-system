package com.library.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class JsonConfig {

    private static ObjectMapper objectMapper;

    private JsonConfig() {
        // Private constructor to prevent instantiation
    }

    public static ObjectMapper getObjectMapper() {
        if (objectMapper == null) {
            objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        }
        return objectMapper;
    }
}
