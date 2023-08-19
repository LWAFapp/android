package com.Zakovskiy.lwaf.models.enums;

import com.Zakovskiy.lwaf.R;

public enum ReportType {
    USER(0, R.string.report_user),
    MESSAGE(1, R.string.report_message);

    public Integer number;
    public int title;

    ReportType(Integer number, int title) {
        this.number = number;
        this.title = title;
    }
}
