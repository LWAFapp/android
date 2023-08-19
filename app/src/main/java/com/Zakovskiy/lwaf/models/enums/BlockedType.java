package com.Zakovskiy.lwaf.models.enums;

import com.Zakovskiy.lwaf.R;

public enum BlockedType {
    UNBLOCKED(0, R.string.will_block),
    BLOCKED(1, R.string.will_unblock);

    public Integer type;
    public Integer title;

    BlockedType(Integer type, Integer title) {
        this.type = type;
        this.title = title;
    }

    public static BlockedType fromInteger(int x) {
        switch(x) {
            case 0:
                return UNBLOCKED;
            case 1:
                return BLOCKED;
        }
        return null;
    }
}
