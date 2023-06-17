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
        int minutes = (int)(seconds/60);
        String minutes_ = minutes < 10 ? "0"+String.valueOf(minutes) : String.valueOf(minutes);
        int secs = seconds-minutes*60;
        String seconds_ = secs < 10 ? "0"+String.valueOf(secs) : String.valueOf(secs);
        String res = minutes_ + ":" + seconds_;
        return res;
    }
}
