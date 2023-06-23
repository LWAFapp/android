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
    static {
        ERRORS.put(0, Resources.getSystem().getString(R.string.error_0));
        ERRORS.put(1, Resources.getSystem().getString(R.string.error_1));
        ERRORS.put(2, Resources.getSystem().getString(R.string.error_2));
        ERRORS.put(3, Resources.getSystem().getString(R.string.error_3));
        ERRORS.put(4, Resources.getSystem().getString(R.string.error_4));
        ERRORS.put(5, Resources.getSystem().getString(R.string.error_5));
        ERRORS.put(6, Resources.getSystem().getString(R.string.error_6));
        ERRORS.put(7, Resources.getSystem().getString(R.string.error_7));
        ERRORS.put(8, Resources.getSystem().getString(R.string.error_8));
        ERRORS.put(11, Resources.getSystem().getString(R.string.error_11));
        ERRORS.put(13, Resources.getSystem().getString(R.string.error_13));
        ERRORS.put(14, Resources.getSystem().getString(R.string.error_14));
        ERRORS.put(20, Resources.getSystem().getString(R.string.error_20));
        ERRORS.put(21, Resources.getSystem().getString(R.string.error_21));
        ERRORS.put(30, Resources.getSystem().getString(R.string.error_30));
        ERRORS.put(40, Resources.getSystem().getString(R.string.error_40));
        ERRORS.put(50, Resources.getSystem().getString(R.string.error_50));
        ERRORS.put(51, Resources.getSystem().getString(R.string.error_51));
        ERRORS.put(52, Resources.getSystem().getString(R.string.error_52));
        ERRORS.put(53, Resources.getSystem().getString(R.string.error_53));
        ERRORS.put(60, Resources.getSystem().getString(R.string.error_60));
        ERRORS.put(61, Resources.getSystem().getString(R.string.error_61));
        ERRORS.put(62, Resources.getSystem().getString(R.string.error_62));
        ERRORS.put(63, Resources.getSystem().getString(R.string.error_63));
        ERRORS.put(70, Resources.getSystem().getString(R.string.error_70));
        ERRORS.put(80, Resources.getSystem().getString(R.string.error_80));
        ERRORS.put(91, Resources.getSystem().getString(R.string.error_91));
        ERRORS.put(92, Resources.getSystem().getString(R.string.error_92));
        ERRORS.put(93, Resources.getSystem().getString(R.string.error_93));
        ERRORS.put(94, Resources.getSystem().getString(R.string.error_94));
        ERRORS.put(100, Resources.getSystem().getString(R.string.error_100));
        ERRORS.put(110, Resources.getSystem().getString(R.string.error_110));
        ERRORS.put(111, Resources.getSystem().getString(R.string.error_111));
        ERRORS.put(112, Resources.getSystem().getString(R.string.error_112));
        ERRORS.put(120, Resources.getSystem().getString(R.string.error_120));
        ERRORS.put(131, Resources.getSystem().getString(R.string.error_131));
        ERRORS.put(132, Resources.getSystem().getString(R.string.error_132));
        ERRORS.put(140, Resources.getSystem().getString(R.string.error_140));
        ERRORS.put(141, Resources.getSystem().getString(R.string.error_141));
        ERRORS.put(150, Resources.getSystem().getString(R.string.error_150));
        ERRORS.put(170, Resources.getSystem().getString(R.string.error_170));
        ERRORS.put(190, Resources.getSystem().getString(R.string.error_190));
        ERRORS.put(200, Resources.getSystem().getString(R.string.error_200));
        ERRORS.put(210, Resources.getSystem().getString(R.string.error_210));
        ERRORS.put(211, Resources.getSystem().getString(R.string.error_211));
        ERRORS.put(220, Resources.getSystem().getString(R.string.error_220));
        ERRORS.put(230, Resources.getSystem().getString(R.string.error_230));
        ERRORS.put(231, Resources.getSystem().getString(R.string.error_231));
        ERRORS.put(240, Resources.getSystem().getString(R.string.error_240));
        ERRORS.put(250, Resources.getSystem().getString(R.string.error_250));
        ERRORS.put(260, Resources.getSystem().getString(R.string.error_260));
        ERRORS.put(261, Resources.getSystem().getString(R.string.error_261));
        ERRORS.put(262, Resources.getSystem().getString(R.string.error_262));
        ERRORS.put(263, Resources.getSystem().getString(R.string.error_263));
        ERRORS.put(270, Resources.getSystem().getString(R.string.error_270));
        ERRORS.put(271, Resources.getSystem().getString(R.string.error_271));
        ERRORS.put(272, Resources.getSystem().getString(R.string.error_272));
        ERRORS.put(273, Resources.getSystem().getString(R.string.error_273));
        ERRORS.put(274, Resources.getSystem().getString(R.string.error_274));
        ERRORS.put(280, Resources.getSystem().getString(R.string.error_280));
        ERRORS.put(290, Resources.getSystem().getString(R.string.error_290));
        ERRORS.put(300, Resources.getSystem().getString(R.string.error_300));
        ERRORS.put(310, Resources.getSystem().getString(R.string.error_310));
        ERRORS.put(9999, Resources.getSystem().getString(R.string.error_9999));
        ERRORS.put(10001, Resources.getSystem().getString(R.string.error_10001));
        ERRORS.put(10101, Resources.getSystem().getString(R.string.error_10101));
        ERRORS.put(10102, Resources.getSystem().getString(R.string.error_10102));
        ERRORS.put(10103, Resources.getSystem().getString(R.string.error_10103));
    };


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
