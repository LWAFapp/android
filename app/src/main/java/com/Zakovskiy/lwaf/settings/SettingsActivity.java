package com.Zakovskiy.lwaf.settings;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.appcompat.widget.AppCompatButton;

import com.Zakovskiy.lwaf.ABCActivity;
import com.Zakovskiy.lwaf.R;
import com.Zakovskiy.lwaf.application.Application;
import com.Zakovskiy.lwaf.network.SocketHelper;
import com.fasterxml.jackson.databind.JsonNode;

public class SettingsActivity extends ABCActivity implements SocketHelper.SocketListener {
    private final SocketHelper socketHelper = SocketHelper.getSocketHelper();
    private LinearLayout commonSettingsLayout;
    private LinearLayout privacySettingsLayout;
    private AppCompatButton commonButton;
    private AppCompatButton privacyButton;
    private AppCompatButton linkVKButton;

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

    }

    @Override
    public void onReceiveError(String str) {

    }
}
