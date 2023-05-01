package com.Zakovskiy.lwaf.models.enums;

public enum MessageType {
    UNDEFINED(0),
    TEXT(1),
    JOIN(2),
    LEFT(3),
    ADD_TRACK(4),
    SET_REACTION_LIKE(5),
    SET_REACTION_DISLIKE(6),
    SET_REACTION_SUPER_LIKE(7),
    LOTO_WINNERS(8),
    REPLACE_TRACK(9),
    DELETE_TRACK(10);

    private Integer type;

    private MessageType(Integer type) {
        this.type = type;
    }
}
