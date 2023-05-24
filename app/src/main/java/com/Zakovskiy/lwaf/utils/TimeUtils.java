package com.Zakovskiy.lwaf.utils;

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
    private static final List<String> months = Arrays.asList("январь", "февраль", "март", "апрель", "май", "июнь", "июль", "август", "сентябрь", "октябрь", "ноябрь", "декабрь");
    private static final List<String> monthsFSM = Arrays.asList("января", "февраля", "марта", "апреля", "мая", "июня", "июля", "августа", "сентября", "октября", "ноября", "декабря");

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

    public static String convertToWords(int month, boolean forSystemMsg) {
        return forSystemMsg ? monthsFSM.get(month-1) : months.get(month-1);
    }
}
