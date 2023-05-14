package com.Zakovskiy.lwaf.authorization;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavHostController;
import androidx.navigation.Navigation;

import com.Zakovskiy.lwaf.ABCActivity;
import com.Zakovskiy.lwaf.DialogTextBox;
import com.Zakovskiy.lwaf.R;
import com.Zakovskiy.lwaf.application.Application;
import com.Zakovskiy.lwaf.dashboard.DashboardFragment;
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

public class RegisterActivity extends ABCActivity implements SocketHelper.SocketListener {

    private SocketHelper socketHelper = SocketHelper.getSocketHelper();
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        this.socketHelper.subscribe(this);
        this.navController = Navigation.findNavController(findViewById(R.id.fragmentContainerView));
    }

    @Override
    public void onConnected() {

    }

    @Override
    public void onDisconnected() {

    }

    @Override
    public void onDestroy() {
        Logs.debug("RA DR");
        this.socketHelper.unsubscribe(this);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        Logs.debug("RA BP");
        this.socketHelper.unsubscribe(this);
        super.onBackPressed();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (navController == null) {
            navController = Navigation.findNavController(findViewById(R.id.fragmentContainerView));
        }
    }

    @Override
    public void onReceive(JsonNode json) {
        this.runOnUiThread(() -> {
            if (json.has(PacketDataKeys.ERROR)) {
                String error = Config.ERRORS.get(json.get(PacketDataKeys.ERROR).asInt());
                new DialogTextBox(RegisterActivity.this, error).show();
            } else if (json.has(PacketDataKeys.TYPE_EVENT)){
                String event = json.get(PacketDataKeys.TYPE_EVENT).asText();
                if (event.equals(PacketDataKeys.ACCOUNT_SIGNUP)) {
                    runOnUiThread(() -> {
                        this.navController.navigate(R.id.action_reg_nickname_password_to_registerSexFragment);
                    });
                    Application.lwafCurrentUser = (User) JsonUtils.convertJsonNodeToObject(json.get(PacketDataKeys.ACCOUNT), User.class);
                    SharedPreferences sPref = getSharedPreferences("lwaf_user", 0);
                    SharedPreferences.Editor ed = sPref.edit();
                    ed.putString("access_token", Application.lwafCurrentUser.accessToken);
                    ed.apply();
                }
            }
            Logs.info("RECV RA " + json.toString());
        });
    }

    @Override
    public void onReceiveError(String str) {

    }

}
