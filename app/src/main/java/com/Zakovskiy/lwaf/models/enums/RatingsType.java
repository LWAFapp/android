package com.Zakovskiy.lwaf.models.enums;

import com.Zakovskiy.lwaf.R;

public enum RatingsType {
    SUPER_LIKES(0, R.string.of_superlikes),
    COINS(1, R.string.of_coins);

    public Integer type;
    public Integer stringId;

    RatingsType(Integer type, Integer stringId) {
        this.type = type;
        this.stringId = stringId;
    }
}
