package com.Zakovskiy.lwaf.models.enums;

public enum Sex {
    MALE(0),
    FEMALE(1);

    private Integer type;
    private Sex(Integer type) {
        this.type = type;
    }
}
