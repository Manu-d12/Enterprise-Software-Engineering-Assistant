package org.aiassistant.ai.utils;

import org.springframework.ai.converter.BeanOutputConverter;

public class Helper {

    public static <T> BeanOutputConverter<T> buildConverter(Class<T> type) {
        return new BeanOutputConverter<>(type);
    }
}
