package com.Zakovskiy.lwaf;

import android.os.Bundle;

import com.Zakovskiy.lwaf.application.Application;
import com.Zakovskiy.lwaf.dashboard.DashboardFragment;
import com.Zakovskiy.lwaf.models.ServerConfig;
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
        this.socketHelper.socketDisconnect(true);
        this.socketHelper.setEnableCheckerConnectionTimer(true);
        this.socketHelper.socketConnect();
        this.socketHelper.subscribe(this);
        Config.ERRORS.put(0, getString(R.string.error_0));
        Config.ERRORS.put(1, getString(R.string.error_1));
        Config.ERRORS.put(2, getString(R.string.error_2));
        Config.ERRORS.put(3, getString(R.string.error_3));
        Config.ERRORS.put(4, getString(R.string.error_4));
        Config.ERRORS.put(5, getString(R.string.error_5));
        Config.ERRORS.put(6, getString(R.string.error_6));
        Config.ERRORS.put(7, getString(R.string.error_7));
        Config.ERRORS.put(8, getString(R.string.error_8));
        Config.ERRORS.put(11, getString(R.string.error_11));
        Config.ERRORS.put(13, getString(R.string.error_13));
        Config.ERRORS.put(14, getString(R.string.error_14));
        Config.ERRORS.put(20, getString(R.string.error_20));
        Config.ERRORS.put(21, getString(R.string.error_21));
        Config.ERRORS.put(30, getString(R.string.error_30));
        Config.ERRORS.put(40, getString(R.string.error_40));
        Config.ERRORS.put(50, getString(R.string.error_50));
        Config.ERRORS.put(51, getString(R.string.error_51));
        Config.ERRORS.put(52, getString(R.string.error_52));
        Config.ERRORS.put(53, getString(R.string.error_53));
        Config.ERRORS.put(60, getString(R.string.error_60));
        Config.ERRORS.put(61, getString(R.string.error_61));
        Config.ERRORS.put(62, getString(R.string.error_62));
        Config.ERRORS.put(63, getString(R.string.error_63));
        Config.ERRORS.put(70, getString(R.string.error_70));
        Config.ERRORS.put(80, getString(R.string.error_80));
        Config.ERRORS.put(91, getString(R.string.error_91));
        Config.ERRORS.put(92, getString(R.string.error_92));
        Config.ERRORS.put(93, getString(R.string.error_93));
        Config.ERRORS.put(94, getString(R.string.error_94));
        Config.ERRORS.put(95, getString(R.string.error_95));
        Config.ERRORS.put(100, getString(R.string.error_100));
        Config.ERRORS.put(110, getString(R.string.error_110));
        Config.ERRORS.put(111, getString(R.string.error_111));
        Config.ERRORS.put(112, getString(R.string.error_112));
        Config.ERRORS.put(120, getString(R.string.error_120));
        Config.ERRORS.put(131, getString(R.string.error_131));
        Config.ERRORS.put(132, getString(R.string.error_132));
        Config.ERRORS.put(140, getString(R.string.error_140));
        Config.ERRORS.put(141, getString(R.string.error_141));
        Config.ERRORS.put(150, getString(R.string.error_150));
        Config.ERRORS.put(170, getString(R.string.error_170));
        Config.ERRORS.put(190, getString(R.string.error_190));
        Config.ERRORS.put(200, getString(R.string.error_200));
        Config.ERRORS.put(210, getString(R.string.error_210));
        Config.ERRORS.put(211, getString(R.string.error_211));
        Config.ERRORS.put(220, getString(R.string.error_220));
        Config.ERRORS.put(230, getString(R.string.error_230));
        Config.ERRORS.put(231, getString(R.string.error_231));
        Config.ERRORS.put(240, getString(R.string.error_240));
        Config.ERRORS.put(250, getString(R.string.error_250));
        Config.ERRORS.put(260, getString(R.string.error_260));
        Config.ERRORS.put(261, getString(R.string.error_261));
        Config.ERRORS.put(262, getString(R.string.error_262));
        Config.ERRORS.put(263, getString(R.string.error_263));
        Config.ERRORS.put(270, getString(R.string.error_270));
        Config.ERRORS.put(271, getString(R.string.error_271));
        Config.ERRORS.put(272, getString(R.string.error_272));
        Config.ERRORS.put(273, getString(R.string.error_273));
        Config.ERRORS.put(274, getString(R.string.error_274));
        Config.ERRORS.put(280, getString(R.string.error_280));
        Config.ERRORS.put(290, getString(R.string.error_290));
        Config.ERRORS.put(300, getString(R.string.error_300));
        Config.ERRORS.put(310, getString(R.string.error_310));
        Config.ERRORS.put(9999, getString(R.string.error_9999));
        Config.ERRORS.put(10001, getString(R.string.error_10001));
        Config.ERRORS.put(10101, getString(R.string.error_10101));
        Config.ERRORS.put(10102, getString(R.string.error_10102));
        Config.ERRORS.put(10103, getString(R.string.error_10103));
    }

    @Override
    protected void onStop() {
        finish();
        this.socketHelper.unsubscribe(this);
        super.onStop();
    }

    @Override
    public void onConnected() {
        HashMap<String, Object> data = new HashMap<>();
        data.put(PacketDataKeys.TYPE_EVENT, PacketDataKeys.CONFIG);
        this.socketHelper.sendData(new JSONObject(data));
    }

    @Override
    public void onDisconnected() {

    }

    @Override
    public void onReceive(JsonNode json) {
        this.runOnUiThread(() -> {
            if (json.has(PacketDataKeys.ERROR)) {
                new DialogTextBox(BaseActivity.this, Config.ERRORS.get(json.get(PacketDataKeys.ERROR).asInt())).show();
                newActivity(WelcomeActivity.class, true, null);
            } else if (json.has(PacketDataKeys.TYPE_EVENT)){
                String typeEvent = json.get(PacketDataKeys.TYPE_EVENT).asText();
                if (typeEvent.equals(PacketDataKeys.ACCOUNT_SIGNIN)) {
                    Application.lwafCurrentUser = (User) JsonUtils.convertJsonNodeToObject(json.get(PacketDataKeys.ACCOUNT), User.class);
                    newActivity(DashboardFragment.class, true, null);
                } else if (typeEvent.equals(PacketDataKeys.CONFIG)) {
                    Application.lwafServerConfig = (ServerConfig) JsonUtils.convertJsonNodeToObject(json.get(PacketDataKeys.CONFIG), ServerConfig.class);
                    String access_token = getSharedPreferences("lwaf_user", 0).getString("access_token", "");
                    if (access_token.isEmpty()) {
                        newActivity(WelcomeActivity.class, true, null);
                    } else {
                        HashMap<String, Object> data = new HashMap<>();
                        data.put(PacketDataKeys.TYPE_EVENT, PacketDataKeys.ACCOUNT_SIGNIN);
                        data.put(PacketDataKeys.TYPE_SIGNIN, PacketDataKeys.ACCESS_TOKEN);
                        data.put(PacketDataKeys.ACCESS_TOKEN, access_token);
                        data.put(PacketDataKeys.DEVICE, Config.getDeviceID(this));
                        this.socketHelper.sendData(new JSONObject(data));
                    }
                }
            }
        });
    }

    @Override
    public void onReceiveError(String str) {

    }


}