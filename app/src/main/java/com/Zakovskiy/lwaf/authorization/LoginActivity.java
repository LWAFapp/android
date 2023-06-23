package com.Zakovskiy.lwaf.authorization;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.Zakovskiy.lwaf.ABCActivity;
import com.Zakovskiy.lwaf.dashboard.DashboardFragment;
import com.Zakovskiy.lwaf.DialogTextBox;
import com.Zakovskiy.lwaf.R;
import com.Zakovskiy.lwaf.application.Application;
import com.Zakovskiy.lwaf.models.User;
import com.Zakovskiy.lwaf.network.SocketHelper;
import com.Zakovskiy.lwaf.utils.Config;
import com.Zakovskiy.lwaf.utils.JsonUtils;
import com.Zakovskiy.lwaf.utils.Logs;
import com.Zakovskiy.lwaf.utils.MD5Hash;
import com.Zakovskiy.lwaf.utils.PacketDataKeys;
import com.fasterxml.jackson.databind.JsonNode;

import org.json.JSONObject;

import java.util.HashMap;

public class LoginActivity extends ABCActivity implements SocketHelper.SocketListener {
    private SocketHelper socketHelper = SocketHelper.getSocketHelper();
    private EditText loginEdit;
    private EditText passwordEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        this.loginEdit = (EditText) findViewById(R.id.editTextLogin);
        this.passwordEdit = (EditText) findViewById(R.id.editTextPassword);
    }

    public void onLogin(View v) {
        String login = this.loginEdit.getText().toString();
        String password = this.passwordEdit.getText().toString();
        if (login.isEmpty() || password.isEmpty()) {
            new DialogTextBox(this, getString(R.string.without_parametrs)).show();
            return;
        }
        HashMap<String, Object> data = new HashMap<>();
        data.put(PacketDataKeys.TYPE_EVENT, PacketDataKeys.ACCOUNT_SIGNIN);
        data.put(PacketDataKeys.TYPE_SIGNIN, PacketDataKeys.NICKNAME);
        data.put(PacketDataKeys.NICKNAME, login);
        data.put(PacketDataKeys.PASSWORD, MD5Hash.md5Salt(password));
        data.put(PacketDataKeys.DEVICE, Config.getDeviceID(this));
        this.socketHelper.sendData(new JSONObject(data));
    }

    @Override
    protected void onStart(){
        this.socketHelper.subscribe(this);
        super.onStart();
    }

    @Override
    protected void onStop() {
        this.socketHelper.unsubscribe(this);
        super.onStop();
    }

    public void onMissedAccount(View v) {
        newActivity(RegisterActivity.class);
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
                Logs.warning(Config.ERRORS.toString());
                String error = Config.ERRORS.get(json.get(PacketDataKeys.ERROR).asInt());
                Logs.warning(error);
                new DialogTextBox(LoginActivity.this, error).show();
            } else if (json.has(PacketDataKeys.TYPE_EVENT)){
                String typeEvent = json.get(PacketDataKeys.TYPE_EVENT).asText();
                if(typeEvent.equals(PacketDataKeys.ACCOUNT_SIGNIN)) {
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
