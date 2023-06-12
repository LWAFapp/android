package com.Zakovskiy.lwaf.utils;

import java.util.ArrayList;

public class ArrayUtils {
    public static ArrayList<Integer> range(int start, int end) {
        ArrayList<Integer> result = new ArrayList<>();
        for (int i = start; i < end; i++) {
            result.add(i);
        }
        return result;
    }
}
