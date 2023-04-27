package com.Zakovskiy.lwaf.utils;

import android.util.Log;

public class Logs {

        private static final String TAG = "LWAF";

        /* renamed from: i */
        public static void info(String str) {
            Log.i(TAG, str);
        }

        /* renamed from: e */
        public static void error(String str) {
            Log.e(TAG, str);
        }

        /* renamed from: d */
        public static void debug(String str) {
            Log.d(TAG, str);
        }

        /* renamed from: v */
        public static void verbose(String str) {
            Log.v(TAG, str);
        }

        /* renamed from: w */
        public static void warning(String str) {
            Log.w(TAG, str);
        }

}
