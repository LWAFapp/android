package com.Zakovskiy.lwaf.settings;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.appcompat.widget.AppCompatButton;

import com.Zakovskiy.lwaf.ABCActivity;
import com.Zakovskiy.lwaf.DialogTextBox;
import com.Zakovskiy.lwaf.DialogVKLogIn;
import com.Zakovskiy.lwaf.R;
import com.Zakovskiy.lwaf.application.Application;
import com.Zakovskiy.lwaf.models.RatingUser;
import com.Zakovskiy.lwaf.models.enums.RatingsType;
import com.Zakovskiy.lwaf.network.SocketHelper;
import com.Zakovskiy.lwaf.ratings.RatingsActivity;
import com.Zakovskiy.lwaf.utils.Config;
import com.Zakovskiy.lwaf.utils.JsonUtils;
import com.Zakovskiy.lwaf.utils.PacketDataKeys;
import com.fasterxml.jackson.databind.JsonNode;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SettingsActivity extends ABCActivity implements SocketHelper.SocketListener {
    private final SocketHelper socketHelper = SocketHelper.getSocketHelper();
    private LinearLayout commonSettingsLayout;
    private LinearLayout privacySettingsLayout;
    private AppCompatButton commonButton;
    private AppCompatButton privacyButton;
    private AppCompatButton linkVKButton;

    @Override
    protected void onStart() {
        super.onStart();
        socketHelper.subscribe(this);
    }

    @Override
    protected void onStop() {
        socketHelper.unsubscribe(this);
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        commonSettingsLayout = findViewById(R.id.commonSettingsLayout);
        privacySettingsLayout = findViewById(R.id.privacySettingsLayout);
        commonButton = findViewById(R.id.buttonSettingsCommon);
        privacyButton = findViewById(R.id.buttonSettingsPrivacy);
        linkVKButton = findViewById(R.id.buttonSettingsLinkVK);
        if (!Application.lwafCurrentUser.vkontakteToken.isEmpty()) {
            linkVKButton.setText(getString(R.string.unlink_vk));
        }
        linkVKButton.setOnClickListener((view)->{
            if (!Application.lwafCurrentUser.vkontakteToken.isEmpty()) {
                HashMap<String, Object> data = new HashMap<>();
                data.put(PacketDataKeys.TYPE_EVENT, PacketDataKeys.VK_TOKEN_REMOVE);
                socketHelper.sendData(new JSONObject(data));
            } else {
                new DialogVKLogIn(this, 1).show();
            }
        });
        commonButton.setOnClickListener(v -> {
            commonButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFFFFF")));
            commonButton.setTextColor(Color.parseColor("#000000"));
            privacyButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#000000")));
            privacyButton.setTextColor(Color.parseColor("#FFFFFF"));
            commonSettingsLayout.setVisibility(View.VISIBLE);
            privacySettingsLayout.setVisibility(View.GONE);
        });
        privacyButton.setOnClickListener(v -> {
            commonButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#000000")));
            commonButton.setTextColor(Color.parseColor("#FFFFFF"));
            privacyButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFFFFF")));
            privacyButton.setTextColor(Color.parseColor("#000000"));
            commonSettingsLayout.setVisibility(View.GONE);
            privacySettingsLayout.setVisibility(View.VISIBLE);
        });
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
                new DialogTextBox(SettingsActivity.this, Config.ERRORS.get(json.get(PacketDataKeys.ERROR).asInt())).show();
            } else if (json.has(PacketDataKeys.TYPE_EVENT)) {
                String typeEvent = json.get(PacketDataKeys.TYPE_EVENT).asText();
                switch (typeEvent) {
                    case "vktc":
                        Application.lwafCurrentUser.vkontakteToken = json.get(PacketDataKeys.VK_TOKEN).asText();
                        Application.lwafCurrentUser.vkontakteSecret = json.get(PacketDataKeys.VK_SECRET).asText();
                        Application.lwafCurrentUser.vkontakteId = json.get(PacketDataKeys.VK_ID).asInt();
                        linkVKButton.setText(getString(R.string.unlink_vk));
                        break;
                    case "vktr":
                        Application.lwafCurrentUser.vkontakteToken = "";
                        linkVKButton.setText(getString(R.string.link_vk));
                        break;
                }
            }
        });
    }

    @Override
    public void onReceiveError(String str) {

    }
}
