package com.Zakovskiy.lwaf.models.enums;

import com.Zakovskiy.lwaf.R;

public enum Sex {
    UNDEFINED(0, R.color.white),
    MALE(1, R.color.blue),
    FEMALE(2, R.color.red);

    public Integer type;
    public Integer color;

    private Sex(Integer type, Integer color) {
        this.type = type;
        this.color = color;
    }
}
