package com.Zakovskiy.lwaf.utils;

import java.util.List;

public class StringUtils {

    public static String join(String[] splitted, String delimiter) {
        String result = "";
        for (int i = 0; i < splitted.length; i++) {
            if (i == 0)
                result = result.concat(ltrim(splitted[i]));
            else
                result = result.concat(splitted[i]);
            if (i != splitted.length-1) {
                result = result.concat(delimiter);
            }
        }
        return result;
    }

    public static String join_l(List<?> splitted, String delimiter) {

        String result = "";
        for (int i = 0; i < splitted.size(); i++) {
            if (i == 0)
                result = result.concat(ltrim(String.valueOf(splitted.get(i))));
            else
                result = result.concat(String.valueOf(splitted.get(i)));
            if (i != splitted.size()-1) {
                result = result.concat(delimiter);
            }

        }
        return result;
    }

    public static String ltrim(String toTrim) {
        int i = 0;
        while (i < toTrim.length() && Character.isWhitespace(toTrim.charAt(i))) {
            i++;
        }
        return toTrim.substring(i);
    }
}
