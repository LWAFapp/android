package com.Zakovskiy.lwaf;

import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.widget.AppCompatButton;

import com.Zakovskiy.lwaf.application.Application;
import com.Zakovskiy.lwaf.authorization.LoginActivity;
import com.Zakovskiy.lwaf.authorization.RegisterActivity;
import com.Zakovskiy.lwaf.dashboard.DashboardFragment;
import com.Zakovskiy.lwaf.models.User;
import com.Zakovskiy.lwaf.network.SocketHelper;
import com.Zakovskiy.lwaf.utils.Config;
import com.Zakovskiy.lwaf.utils.JsonUtils;
import com.Zakovskiy.lwaf.utils.PacketDataKeys;
import com.fasterxml.jackson.databind.JsonNode;

public class WelcomeActivity extends ABCActivity implements SocketHelper.SocketListener, View.OnClickListener {

    private SocketHelper socketHelper = SocketHelper.getSocketHelper();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        ((AppCompatButton)findViewById(R.id.buttonLogInVK)).setOnClickListener(this);
    }

    @Override
    protected void onStop() {
        this.socketHelper.unsubscribe(this);
        super.onStop();
    }

    @Override
    protected void onStart() {
        this.socketHelper.subscribe(this);
        super.onStart();
    }

    public void onLogin(View v) {
        newActivity(LoginActivity.class);
    }

    public void onRegistration(View v) {
        newActivity(RegisterActivity.class);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.buttonLogInVK) {
            new DialogVKLogIn(this, 0).show();
        }
    }

    @Override
    public void onConnected() {}

    @Override
    public void onDisconnected() {
        newActivity(BaseActivity.class, true, new Bundle());
    }

    @Override
    public void onReceiveError(String str) {
        newActivity(BaseActivity.class, true, new Bundle());
    }

    @Override
    public void onReceive(JsonNode json) {
        this.runOnUiThread(() -> {
            if (json.has(PacketDataKeys.ERROR)) {
                new DialogTextBox(WelcomeActivity.this, Config.ERRORS.get(json.get(PacketDataKeys.ERROR).asInt())).show();
            } else if (json.has(PacketDataKeys.TYPE_EVENT)) {
                String typeEvent = json.get(PacketDataKeys.TYPE_EVENT).asText();
                if (typeEvent.equals(PacketDataKeys.ACCOUNT_SIGN_VK)) {
                    Application.lwafCurrentUser = (User) JsonUtils.convertJsonNodeToObject(json.get(PacketDataKeys.ACCOUNT), User.class);
                    SharedPreferences sPref = getSharedPreferences("lwaf_user", 0);
                    SharedPreferences.Editor ed = sPref.edit();
                    ed.putString("access_token", Application.lwafCurrentUser.accessToken);
                    ed.apply();
                    newActivity(DashboardFragment.class, true, null);
                }
            }
        });
    }
}
