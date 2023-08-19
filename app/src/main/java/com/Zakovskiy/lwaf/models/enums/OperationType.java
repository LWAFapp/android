package com.Zakovskiy.lwaf.models.enums;

import com.Zakovskiy.lwaf.R;

public enum OperationType {
    SUPERLIKE(0, R.string.set_superlike, R.drawable.rock),
    OUT_TRACK(1, R.string.track_out_of_turn, R.drawable.ic_queue),
    DAILY_BONUS(2, R.string.track_out_of_turn, R.drawable.ic_wheel_fortune_black),
    CHANGE_NICKNAME(3, R.string.changes_nickname, R.drawable.ic_change_nickname),
    LOTO(4, R.string.loto, R.drawable.ic_loto_black),
    PLAYED_TRACK(4, R.string.played_track, R.drawable.ic_audiotrack_outlined),
    PROMOCODE(5, R.string.promocode, R.drawable.ic_gift_black);

    public Integer id;
    public Integer title;
    public Integer resId;

    OperationType(Integer id, Integer title, Integer resId) {
        this.id = id;
        this.title = title;
        this.resId = resId;
    }
}
