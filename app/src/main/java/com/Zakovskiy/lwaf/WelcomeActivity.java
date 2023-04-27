package com.Zakovskiy.lwaf;

import android.os.Bundle;
import android.view.View;

import com.Zakovskiy.lwaf.authorization.LoginActivity;

public class WelcomeActivity extends ABCActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
    }

    public void onLogin(View v) {
        newActivity(LoginActivity.class);
    }

    public void onRegistration(View v) {
        newActivity(LoginActivity.class);
    }
}
