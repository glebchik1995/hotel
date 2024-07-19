package com.java.hotel.service.model.room;

import lombok.Getter;

import java.io.Serializable;

@Getter
public enum State implements Serializable {
    BEING_REPAIRED("BEING_REPAIRED"),
    OCCUPIED("OCCUPIED"),
    FREE("FREE");

    private final String value;

    State(String value) {
        this.value = value;
    }

    public static State fromValue(String value) {
        for (State status : values()) {
            if (status.value.equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Недопустимое значение для StatusRoom: " + value);
    }
}