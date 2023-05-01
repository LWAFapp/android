package com.Zakovskiy.lwaf;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.widget.AppCompatButton;

import com.Zakovskiy.lwaf.application.Application;
import com.Zakovskiy.lwaf.authorization.LoginActivity;
import com.Zakovskiy.lwaf.dashboard.DashboardFragment;
import com.Zakovskiy.lwaf.globalConversation.GlobalConversationActivity;
import com.Zakovskiy.lwaf.models.Message;
import com.Zakovskiy.lwaf.models.ShortUser;
import com.Zakovskiy.lwaf.models.User;
import com.Zakovskiy.lwaf.network.SocketHelper;
import com.Zakovskiy.lwaf.utils.Config;
import com.Zakovskiy.lwaf.utils.JsonUtils;
import com.Zakovskiy.lwaf.utils.Logs;
import com.Zakovskiy.lwaf.utils.PacketDataKeys;
import com.fasterxml.jackson.databind.JsonNode;
import com.vk.api.sdk.VK;
import com.vk.api.sdk.VKApiConfig;
import com.vk.api.sdk.auth.VKAuthenticationResult;
import com.vk.api.sdk.auth.VKScope;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WelcomeActivity extends ABCActivity implements SocketHelper.SocketListener, View.OnClickListener {

    private ActivityResultLauncher authLauncher;
    private SocketHelper socketHelper = SocketHelper.getSocketHelper();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        ((AppCompatButton)findViewById(R.id.buttonLogInVK)).setOnClickListener(this);
        this.socketHelper.subscribe(this);
        this.authLauncher = VK.login(this, new ActivityResultCallback<VKAuthenticationResult>() {
            @Override
            public void onActivityResult(VKAuthenticationResult result) {
                if (result instanceof VKAuthenticationResult.Success) {
                    String vkAccessToken = ((VKAuthenticationResult.Success) result).getToken().getAccessToken();
                    Logs.debug(vkAccessToken);
                    HashMap<String, Object> data = new HashMap<>();
                    data.put(PacketDataKeys.TYPE_EVENT, PacketDataKeys.ACCOUNT_SIGN_VK);
                    data.put(PacketDataKeys.VK_TOKEN, vkAccessToken);
                    data.put(PacketDataKeys.DEVICE, Config.getDeviceID(WelcomeActivity.this));
                    socketHelper.sendData(new JSONObject(data));
                }
                if (result instanceof VKAuthenticationResult.Failed) {
                    Logs.debug(((VKAuthenticationResult.Failed) result).getException().getMessage());
                }
            }
        });

    }

    @Override
    protected void onStop() {
        this.socketHelper.unsubscribe(this);
        super.onStop();
    }

    public void onLogin(View v) {
        newActivity(LoginActivity.class);
    }

    public void onRegistration(View v) {
        newActivity(LoginActivity.class);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.buttonLogInVK) {
            List<VKScope> scopes = new ArrayList<VKScope>();
            scopes.add(VKScope.AUDIO);
            scopes.add(VKScope.OFFLINE);
            scopes.add(VKScope.STATS);
            this.authLauncher.launch(scopes);
        }
    }

    @Override
    public void onConnected() {

    }

    @Override
    public void onDisconnected() {

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

    @Override
    public void onReceiveError(String str) {

    }
}
