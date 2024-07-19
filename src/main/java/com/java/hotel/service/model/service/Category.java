package com.java.hotel.service.model.service;

import lombok.Getter;

import java.io.Serializable;

@Getter
public enum Category implements Serializable {
    FOOD("FOOD"),
    TECHNIC("TECHNIC"),
    BATHROOM_ACCESSORIES("BATHROOM_ACCESSORIES");

    private final String value;

    Category(String value) {
        this.value = value;
    }

    public static Category fromValue(String value) {
        for (Category category : values()) {
            if (category.value.equals(value)) {
                return category;
            }
        }
        throw new IllegalArgumentException("Недопустимое значение для Category: " + value);
    }
}
