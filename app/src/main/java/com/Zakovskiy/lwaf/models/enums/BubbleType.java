package com.Zakovskiy.lwaf.models.enums;

public enum BubbleType {
    DEFAULT(0),
    SOLID(1);

    private Integer type;

    BubbleType (Integer type) {
        this.type = type;
    }
}
