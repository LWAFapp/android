package com.Zakovskiy.lwaf;

import android.os.Bundle;

import com.Zakovskiy.lwaf.application.Application;
import com.Zakovskiy.lwaf.dashboard.DashboardFragment;
import com.Zakovskiy.lwaf.models.User;
import com.Zakovskiy.lwaf.network.SocketHelper;
import com.Zakovskiy.lwaf.utils.Config;
import com.Zakovskiy.lwaf.utils.JsonUtils;
import com.Zakovskiy.lwaf.utils.PacketDataKeys;
import com.fasterxml.jackson.databind.JsonNode;

import org.json.JSONObject;

import java.util.HashMap;

public class BaseActivity extends ABCActivity implements SocketHelper.SocketListener {

    private SocketHelper socketHelper = SocketHelper.getSocketHelper();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
    }

    @Override
    protected void onStart() {
        super.onStart();
        this.socketHelper.setEnableCheckerConnectionTimer(true);
        this.socketHelper.socketConnect();
        this.socketHelper.subscribe(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        this.socketHelper.unsubscribe(this);
    }

    @Override
    public void onConnected() {
        String access_token = getSharedPreferences("lwaf_user", 0).getString("access_token", "");
        if (access_token.isEmpty()) {
            newActivity(WelcomeActivity.class, true);
        } else {
            HashMap<String, Object> data = new HashMap<>();
            data.put(PacketDataKeys.TYPE_EVENT, PacketDataKeys.ACCOUNT_SIGNIN);
            data.put(PacketDataKeys.TYPE_SIGNIN, PacketDataKeys.ACCESS_TOKEN);
            data.put(PacketDataKeys.ACCESS_TOKEN, access_token);
            data.put(PacketDataKeys.DEVICE, Config.getDeviceID(this));
            this.socketHelper.sendData(new JSONObject(data));
        }
    }

    @Override
    public void onDisconnected() {

    }

    @Override
    public void onReceive(JsonNode json) {
        this.runOnUiThread(() -> {
            if (json.has(PacketDataKeys.ERROR)) {
                new DialogTextBox(BaseActivity.this, Config.ERRORS.get(json.get(PacketDataKeys.ERROR).asInt())).show();
                newActivity(WelcomeActivity.class, true);
            } else if (json.get(PacketDataKeys.TYPE_EVENT).asText().equals(PacketDataKeys.ACCOUNT_SIGNIN)) {
                Application.lwafCurrentUser = (User) JsonUtils.convertJsonNodeToObject(json.get(PacketDataKeys.ACCOUNT), User.class);
                newActivity(DashboardFragment.class, true);
            }
        });
    }

    @Override
    public void onReceiveError(String str) {

    }


}