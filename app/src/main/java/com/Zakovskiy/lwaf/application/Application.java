package com.Zakovskiy.lwaf.application;

import com.Zakovskiy.lwaf.models.User;

public class Application extends android.app.Application {
    public static User lwafCurrentUser;

    @Override
    public void onCreate() {
        super.onCreate();
    }
}
