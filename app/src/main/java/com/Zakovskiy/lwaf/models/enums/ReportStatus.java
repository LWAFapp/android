package com.Zakovskiy.lwaf.models.enums;

import com.Zakovskiy.lwaf.R;

public enum ReportStatus {
    OPEN(0, R.string.opened, R.color.green),
    CLOSE(1, R.string.closed, R.color.red);

    public Integer number;
    public int title;
    public int color;

    ReportStatus(Integer number, int title, int color) {
        this.number = number;
        this.title = title;
        this.color = color;
    }
}
