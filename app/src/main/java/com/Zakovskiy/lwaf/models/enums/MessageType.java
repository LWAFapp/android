package com.Zakovskiy.lwaf.models.enums;

public enum MessageType {
    UNDEFINED(0),
    TEXT(1),
    JOIN(2),
    LEFT(3);

    private Integer type;

    private MessageType(Integer type) {
        this.type = type;
    }
}
