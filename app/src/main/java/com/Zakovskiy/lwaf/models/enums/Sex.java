package com.Zakovskiy.lwaf.models.enums;

public enum Sex {
    UNDEFINED(0),
    MALE(1),
    FEMALE(2);

    private Integer type;
    private Sex(Integer type) {
        this.type = type;
    }
}
