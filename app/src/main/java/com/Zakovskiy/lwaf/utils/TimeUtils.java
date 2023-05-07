package com.Zakovskiy.lwaf.utils;

import com.Zakovskiy.lwaf.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

public class TimeUtils {

    public static String getTime(long timestamp) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        GregorianCalendar gregorianCalendar = new GregorianCalendar(TimeZone.getDefault());
        gregorianCalendar.setTimeInMillis(timestamp);
        return simpleDateFormat.format(gregorianCalendar.getTime());
        //SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm", new Locale("ru"));
        //return simpleDateFormat.format(new Date());
    }
    public static String getDateAndTime(long timestamp) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());
        GregorianCalendar gregorianCalendar = new GregorianCalendar(TimeZone.getDefault());
        gregorianCalendar.setTimeInMillis(timestamp);
        return simpleDateFormat.format(gregorianCalendar.getTime());
        //SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm", new Locale("ru"));
        //return simpleDateFormat.format(new Date());
    }
}
