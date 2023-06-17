package com.Zakovskiy.lwaf.utils;

import android.content.Context;

import com.Zakovskiy.lwaf.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class TimeUtils {

    public static String getTime(long timestamp) {
        return getTime(timestamp, "HH:mm");
    }

    public static String getTime(long timestamp, String pattern) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern, Locale.getDefault());
        GregorianCalendar gregorianCalendar = new GregorianCalendar(TimeZone.getDefault());
        gregorianCalendar.setTimeInMillis(timestamp);
        return simpleDateFormat.format(gregorianCalendar.getTime());
        //SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm", new Locale("ru"));
        //return simpleDateFormat.format(new Date());
    }
    public static String getDateAndTime(long timestamp) {
        return getTime(timestamp, "dd.MM.yyyy HH:mm");
    }

    public static String convertToWords(Context context, int month, boolean forSystemMsg) {
        String[] months = context.getResources().getStringArray(R.array.months);
        String[] monthsFSM = context.getResources().getStringArray(R.array.months_fsm);
        return forSystemMsg ? monthsFSM[month-1] : months[month-1];
    }

    public static String secondsToDur(int seconds) {
        Integer durationMinutes = (Integer) seconds / 60;
        Integer durationSeconds = (Integer) seconds % 60;
        return String.format("%02d:%02d", durationMinutes, durationSeconds);
    }
}
