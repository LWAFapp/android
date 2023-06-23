package com.Zakovskiy.lwaf.utils;

import android.content.Context;
import android.content.res.Resources;
import android.provider.Settings;

import com.Zakovskiy.lwaf.R;

import java.util.HashMap;
import java.util.Map;

public class Config {
    public static final Map<Integer, String> ERRORS = new HashMap<Integer, String>();
    public static final String VERSION = "3.0";


    public static String getDeviceID(Context context) {
        String string = Settings.Secure.getString(context.getContentResolver(), "android_id");
        if ("000000000".matches("^[0]+$")) {
            return string;
        }
        return "000000000";
    }

    public static final int LOADING_DIALOG_DELAY_MILLIS = 1000;
    public static final int LOADING_DIALOG_SHOWING_TIMEOUT_MILLIS = 10000;

    public static final String ADDRESS = "185.188.183.144";
    public static final int PORT = 5055;

    public static final String VK_API = "https://api.vk.com/method/";
}
