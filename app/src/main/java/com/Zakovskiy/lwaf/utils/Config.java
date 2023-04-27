package com.Zakovskiy.lwaf.utils;

import android.content.Context;
import android.provider.Settings;

import java.util.HashMap;
import java.util.Map;

public class Config {

    public static final Map<Integer, String> ERRORS = new HashMap<Integer, String>();
    public static final String VERSION = "3.0";
    static {
            ERRORS.put(1, "Неизвестная ошибка");
            ERRORS.put(2, "Нет данных");
            ERRORS.put(3, "Неизвестные данные");
            ERRORS.put(4, "Вы забыли указать параметры");
            ERRORS.put(5, "Неизвестный девайс айди");
            ERRORS.put(6, "Ошибка авторизации");
            ERRORS.put(7, "Вы не авторизованны");
            ERRORS.put(11, "Неправильный пароль");
            ERRORS.put(13, "Неправильный ник");
            ERRORS.put(14, "Ник уже занят");
            ERRORS.put(20, "Неизвестный тип авторизации");
            ERRORS.put(21, "Неверный пользователь");
            ERRORS.put(30, "Неизвестный тип пользователя");
            ERRORS.put(40, "Вы уже состоите в комнате");
            ERRORS.put(50, "Вы не состоите в комнате");
            ERRORS.put(51, "Неверное значение игроков");
            ERRORS.put(60, "Такая комната не существует");
            ERRORS.put(61, "Вы уже состоите в комнате. Попробуйте перезайти");
            ERRORS.put(62, "Нет свободных мест");
            ERRORS.put(63, "Неверный пароль");
            ERRORS.put(70, "Длинное сообщение");
            ERRORS.put(80, "Длинный поисковый запрос");
            ERRORS.put(91, "Длинный трек");
            ERRORS.put(92, "Вы уже поставили трек в очередь");
            ERRORS.put(93, "Неизвестный трек");
            ERRORS.put(94, "Неизвестная ошибка");
            ERRORS.put(100, "Неизвестный трек");
            ERRORS.put(110, "Вы уже привязали ВК");
            ERRORS.put(111, "Неправильный токен");
            ERRORS.put(120, "Вы не привязалали ВК");
            ERRORS.put(131, "Недостаточно монет. Нужно: 5");
            ERRORS.put(132, "Неизвестный тип реакции");
            ERRORS.put(140, "Неверный пользователь");
            ERRORS.put(141, "Вы уже являетесь друзьями");
            ERRORS.put(150, "Такого друга не существует");
            ERRORS.put(190, "Не существует такое сообщение");
            ERRORS.put(200, "Вы уже состоите в глобальном чате");
            ERRORS.put(210, "Вы не состоите в глобальном чате");
            ERRORS.put(9999, "Недостаточно прав");
            ERRORS.put(10001, "Вас выкинул модератор");
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
    public static final int PORT = 5555;
}
