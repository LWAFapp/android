package com.Zakovskiy.lwaf.settings;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.appcompat.widget.AppCompatButton;

import com.Zakovskiy.lwaf.ABCActivity;
import com.Zakovskiy.lwaf.DialogTextBox;
import com.Zakovskiy.lwaf.DialogVKLogIn;
import com.Zakovskiy.lwaf.R;
import com.Zakovskiy.lwaf.SetNicknameDialog;
import com.Zakovskiy.lwaf.application.Application;
import com.Zakovskiy.lwaf.models.RatingUser;
import com.Zakovskiy.lwaf.models.User;
import com.Zakovskiy.lwaf.models.enums.RatingsType;
import com.Zakovskiy.lwaf.network.SocketHelper;
import com.Zakovskiy.lwaf.ratings.RatingsActivity;
import com.Zakovskiy.lwaf.utils.Config;
import com.Zakovskiy.lwaf.utils.JsonUtils;
import com.Zakovskiy.lwaf.utils.PacketDataKeys;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.android.material.button.MaterialButton;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SettingsActivity extends ABCActivity implements SocketHelper.SocketListener {

    private final SocketHelper socketHelper = SocketHelper.getSocketHelper();
    private AutoCompleteTextView autoCompleteTextView;
    private LinearLayout commonSettingsLayout;
    private LinearLayout privacySettingsLayout;
    private MaterialButton commonButton;
    private MaterialButton privacyButton;
    private MaterialButton linkVKButton;
    private CheckBox cbShowBalance;
    private CheckBox cbShowRecentTracks;
    private User currentUser;

    private CompoundButton.OnCheckedChangeListener onCheckedChangeListener = (buttonView, isChecked) -> {
        HashMap<String, Object> data = new HashMap<>();
        data.put(PacketDataKeys.TYPE_EVENT, PacketDataKeys.CHANGE_CONFIDENTIALITY);
        String type = "hide_balance";
        if(buttonView.getId() == R.id.checkBoxShowRecent) {
            type = "hide_lt";
        }
        data.put(PacketDataKeys.TYPE, type);
        this.socketHelper.sendData(new JSONObject(data));
    };

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
        currentUser = Application.lwafCurrentUser;
        String[] sexes = getResources().getStringArray(R.array.sexes);
        ArrayAdapter<String> sexesAdapter = new ArrayAdapter<>(this, R.layout.dropdown_sex, sexes);
        this.autoCompleteTextView = findViewById(R.id.autoCompleteSex);
        this.autoCompleteTextView.setAdapter(sexesAdapter);
        this.autoCompleteTextView.setListSelection(currentUser.sex.ordinal());
        this.autoCompleteTextView.setOnItemClickListener((parent, view, position, rowId) -> {
            HashMap<String, Object> data = new HashMap<>();
            data.put(PacketDataKeys.TYPE_EVENT, PacketDataKeys.SEX_CHANGE);
            data.put(PacketDataKeys.SEX, position);
            this.socketHelper.sendData(new JSONObject(data));
        });
        commonSettingsLayout = findViewById(R.id.commonSettingsLayout);
        privacySettingsLayout = findViewById(R.id.privacySettingsLayout);
        commonButton = findViewById(R.id.buttonSettingsCommon);
        privacyButton = findViewById(R.id.buttonSettingsPrivacy);
        linkVKButton = findViewById(R.id.buttonSettingsLinkVK);
        cbShowBalance = findViewById(R.id.checkBoxShowBalance);
        cbShowRecentTracks = findViewById(R.id.checkBoxShowRecent);
        cbShowRecentTracks.setChecked(currentUser.hidenLastTracks);
        cbShowBalance.setChecked(currentUser.hidenBalance);
        cbShowBalance.setOnCheckedChangeListener(onCheckedChangeListener);
        cbShowRecentTracks.setOnCheckedChangeListener(onCheckedChangeListener);
        findViewById(R.id.buttonSettingsChangeName).setOnClickListener((view)->{
            new SetNicknameDialog(this, 1).show();
        });
        findViewById(R.id.buttonSettingPP).setOnClickListener((view)->{
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Application.lwafServerConfig.politics));
            startActivity(browserIntent);
        });
        findViewById(R.id.buttonSettingToU).setOnClickListener((view)->{
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Application.lwafServerConfig.toU));
            startActivity(browserIntent);
        });
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
        changeTab(true);
        commonButton.setOnClickListener(v -> {
            changeTab(true);
        });
        privacyButton.setOnClickListener(v -> {
            changeTab(false);
        });
    }

    private void changeTab(boolean type) {
        if(type) {
            commonButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
            commonButton.setTextColor(getResources().getColor(R.color.black_primary));
            privacyButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.transparent)));
            privacyButton.setTextColor(ColorStateList.valueOf(getResources().getColor(R.color.white)));
            commonSettingsLayout.setVisibility(View.VISIBLE);
            privacySettingsLayout.setVisibility(View.GONE);
        } else {
            commonButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.transparent)));
            commonButton.setTextColor(ColorStateList.valueOf(getResources().getColor(R.color.white)));
            privacyButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
            privacyButton.setTextColor(getResources().getColor(R.color.black_primary));
            commonSettingsLayout.setVisibility(View.GONE);
            privacySettingsLayout.setVisibility(View.VISIBLE);
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
                new DialogTextBox(this, Config.ERRORS.get(json.get(PacketDataKeys.ERROR).asInt())).show();
            } else if (json.has(PacketDataKeys.TYPE_EVENT)) {
                String typeEvent = json.get(PacketDataKeys.TYPE_EVENT).asText();
                switch (typeEvent) {
                    case PacketDataKeys.VK_TOKEN_CHANGE:
                        Application.lwafCurrentUser.vkontakteToken = json.get(PacketDataKeys.VK_TOKEN).asText();
                        Application.lwafCurrentUser.vkontakteSecret = json.get(PacketDataKeys.VK_SECRET).asText();
                        Application.lwafCurrentUser.vkontakteId = json.get(PacketDataKeys.VK_ID).asInt();
                        linkVKButton.setText(getString(R.string.unlink_vk));
                        break;
                    case PacketDataKeys.VK_TOKEN_REMOVE:
                        Application.lwafCurrentUser.vkontakteToken = "";
                        linkVKButton.setText(getString(R.string.link_vk));
                        break;
                    case PacketDataKeys.CHANGE_NICKNAME:
                        new DialogTextBox(this, getString(R.string.successfully_change_nickname)).show();
                        break;
                }
            }
        });
    }

    @Override
    public void onReceiveError(String str) {

    }
}
