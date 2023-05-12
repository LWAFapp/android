package com.Zakovskiy.lwaf.utils;

import android.content.Context;
import android.provider.Settings;

import java.util.HashMap;
import java.util.Map;

public class Config {

    public static final Map<Integer, String> ERRORS = new HashMap<Integer, String>();
    public static final String VERSION = "3.0";
    static {
        ERRORS.put(0, "Обновите приложение в PlayMarket или в Telegram @lwafapp");
        ERRORS.put(1, "Неизвестная ошибка");
        ERRORS.put(2, "Нет данных");
        ERRORS.put(3, "Неизвестные данные");
        ERRORS.put(4, "Вы забыли указать параметры");
        ERRORS.put(5, "Неизвестный девайс айди");
        ERRORS.put(6, "Ошибка авторизации");
        ERRORS.put(7, "Вы не авторизованны");
        ERRORS.put(8, "Вы забанены");
        ERRORS.put(11, "Неправильный пароль");
        ERRORS.put(13, "Неправильный ник");
        ERRORS.put(14, "Ник уже занят");
        ERRORS.put(20, "Неизвестный тип авторизации");
        ERRORS.put(21, "Неверный пользователь");
        ERRORS.put(30, "Неизвестный тип пользователя");
        ERRORS.put(40, "Вы уже состоите в комнате");
        ERRORS.put(50, "Вы не состоите в комнате");
        ERRORS.put(51, "Неверное значение игроков");
        ERRORS.put(52, "Длинное название комнаты");
        ERRORS.put(53, "Комнату можно создавать раз в 20 секунд");
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
        ERRORS.put(112, "Этот аккаунт уже привязан");
        ERRORS.put(120, "Вы не привязалали ВК");
        ERRORS.put(131, "Недостаточно монет. Нужно: 5");
        ERRORS.put(132, "Неизвестный тип реакции");
        ERRORS.put(140, "Неверный пользователь");
        ERRORS.put(141, "Вы уже являетесь друзьями");
        ERRORS.put(150, "Такого друга не существует");
        ERRORS.put(170, "Такого друга не существует");
        ERRORS.put(190, "Такое сообщение не существует");
        ERRORS.put(200, "Вы уже состоите в глобальном чате");
        ERRORS.put(210, "Вы не состоите в глобальном чате");
        ERRORS.put(211, "Вас кикнули");
        ERRORS.put(220, "Такого поста не существует");
        ERRORS.put(230, "Слишком длинный текст");
        ERRORS.put(231, "Такого комментария не существует");
        ERRORS.put(240, "Пользователь не онлайн");
        ERRORS.put(250, "Репорт можно отправлять раз в 5 минут");
        ERRORS.put(260, "Недостаточно монет для ставки");
        ERRORS.put(261, "Сейчас не идёт лотерея!");
        ERRORS.put(262, "Вы уже делали ставку!");
        ERRORS.put(263, "!");
        ERRORS.put(270, "Неверная почта");
        ERRORS.put(271, "Вы не запросили код");
        ERRORS.put(272, "Неверный код");
        ERRORS.put(273, "Недостаточно монет.\nНужно: 1000");
        ERRORS.put(280, "Вы уже привязали почту");
        ERRORS.put(290, "Неизвестный тип");
        ERRORS.put(300, "У вас не осталось вращений");
        ERRORS.put(310, "Вы не ставили трек");
        ERRORS.put(9999, "Недостаточно прав");
        ERRORS.put(10101, "Данный сервис временно недоступен");
        ERRORS.put(10102, "Такого сервиса не существует");
        ERRORS.put(10103, "Такой жалобы не существует");
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
    public static final int PORT = 5055;

    public static final String VK_API = "https://api.vk.com/method/";
}
