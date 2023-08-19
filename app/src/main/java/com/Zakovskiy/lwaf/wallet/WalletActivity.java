package com.Zakovskiy.lwaf.wallet;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.view.menu.ActionMenuItemView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.Zakovskiy.lwaf.ABCActivity;
import com.Zakovskiy.lwaf.DialogTextBox;
import com.Zakovskiy.lwaf.DialogVKLogIn;
import com.Zakovskiy.lwaf.R;
import com.Zakovskiy.lwaf.SetNicknameDialog;
import com.Zakovskiy.lwaf.application.Application;
import com.Zakovskiy.lwaf.dashboard.DashboardFragment;
import com.Zakovskiy.lwaf.dashboard.dialogs.DialogWheel;
import com.Zakovskiy.lwaf.models.Operation;
import com.Zakovskiy.lwaf.models.User;
import com.Zakovskiy.lwaf.models.enums.FriendType;
import com.Zakovskiy.lwaf.network.SocketHelper;
import com.Zakovskiy.lwaf.profileDialog.dialogs.WalletInfoBottomDialog;
import com.Zakovskiy.lwaf.utils.Config;
import com.Zakovskiy.lwaf.utils.JsonUtils;
import com.Zakovskiy.lwaf.utils.Logs;
import com.Zakovskiy.lwaf.utils.PacketDataKeys;
import com.Zakovskiy.lwaf.wallet.adapters.OperationsAdapter;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WalletActivity  extends ABCActivity implements SocketHelper.SocketListener {

    private final SocketHelper socketHelper = SocketHelper.getSocketHelper();
    private MaterialToolbar materialToolbar;
    private TextView tvDailyBonus;
    private NestedScrollView nsvContent;
    private OperationsAdapter operationsAdapter;
    private ConstraintLayout btnDailyBonus;
    private RecyclerView rvOperations;
    private ActionMenuItemView miInfoWallet;
    private TextInputLayout tilPromocode;
    private List<Operation> operations = new ArrayList<>();
    private boolean canGetNewOperations = true;
    private boolean loadingNewOperations = false;

    @Override
    protected void onStart() {
        super.onStart();
        socketHelper.subscribe(this);
        HashMap<String, Object> data = new HashMap<>();
        data.put(PacketDataKeys.TYPE_EVENT, PacketDataKeys.GET_BALANCE);
        this.socketHelper.sendData(new JSONObject(data));
        getOperations();
    }

    public void refreshOperations(List<Operation> list) {
        if(list.size() == 0 || operations.size() == list.size() + 1) {
            this.canGetNewOperations = false;
        }
        this.operations.clear();
        this.operations.addAll(list);
        this.operationsAdapter.notifyDataSetChanged();
    }

    public void getOperations() {
        this.operations.add(null);
        this.operationsAdapter.notifyItemInserted(this.operations.size() - 1);
        this.loadingNewOperations = true;
        HashMap<String, Object> data = new HashMap<>();
        data.put(PacketDataKeys.TYPE_EVENT, PacketDataKeys.GET_OPERATIONS);
        data.put(PacketDataKeys.SIZE, 20);
        data.put(PacketDataKeys.OFFSET, operations.size() - 1);
        this.socketHelper.sendData(new JSONObject(data));
    }

    @Override
    protected void onStop() {
        socketHelper.unsubscribe(this);
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.materialToolbar = findViewById(R.id.toolbar);
        this.btnDailyBonus = findViewById(R.id.btnDailyBonus);
        this.tvDailyBonus = findViewById(R.id.tvDailyBonus);
        this.rvOperations = findViewById(R.id.rvHistoryOperations);
        this.nsvContent = findViewById(R.id.nsvContent);
        this.tilPromocode = findViewById(R.id.tilPromocode);
        this.miInfoWallet = findViewById(R.id.information);
        this.miInfoWallet.setOnClickListener((v) -> {
            new WalletInfoBottomDialog().show(getSupportFragmentManager(), "WalletInfo");
        });
        this.tilPromocode.setEndIconOnClickListener((v) -> {
            EditText editText = this.tilPromocode.getEditText();
            String content = editText.getText().toString();
            if (content.isEmpty())
                return;
            HashMap<String, Object> data = new HashMap<>();
            data.put(PacketDataKeys.TYPE_EVENT, PacketDataKeys.PROMOCODE_ENTER);
            data.put(PacketDataKeys.CONTENT, content);
            this.socketHelper.sendData(new JSONObject(data));
            editText.setText("");

        });
        operationsAdapter = new OperationsAdapter(this, operations);
        this.rvOperations.setAdapter(operationsAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        this.nsvContent.getViewTreeObserver().addOnScrollChangedListener(() -> {
            int totalItemCount = linearLayoutManager.getItemCount();
            int lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
            if (!loadingNewOperations && totalItemCount <= (lastVisibleItem + 3) && canGetNewOperations) {
                getOperations();
            }
        });
        this.rvOperations.setLayoutManager(linearLayoutManager);
        if(Application.lwafCurrentUser.wheelCount == 0) {
            int secondsVia = (int) ((System.currentTimeMillis() / 1000) - Application.lwafCurrentUser.wheelTimestamp);
            int hoursVia = 24 - (secondsVia / 3600);
            int minutesVia = 60 - ((secondsVia % 3600) / 60);
            this.tvDailyBonus.setText(Html.fromHtml(String.format("<b>%s</b><br>%s %s %s %s", getString(R.string.daily_bonus_via), hoursVia, getString(R.string.hours), minutesVia, getString(R.string.minutes))));
        } else {
            this.tvDailyBonus.setText(getString(R.string.get_daily_bonus));
            this.btnDailyBonus.setOnClickListener((v) -> {
                new DialogWheel( WalletActivity.this).show();
            });
        }
        this.materialToolbar.setNavigationOnClickListener((v) -> {
            finish();
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
                new DialogTextBox(this, Config.ERRORS.get(json.get(PacketDataKeys.ERROR).asInt())).show();
            } else if (json.has(PacketDataKeys.TYPE_EVENT)) {
                String typeEvent = json.get(PacketDataKeys.TYPE_EVENT).asText();
                switch (typeEvent) {
                    case PacketDataKeys.GET_BALANCE:
                        Integer balance = json.get(PacketDataKeys.BALANCE).asInt();
                        ((TextView) findViewById(R.id.tvBalance)).setText(String.valueOf(balance));
                        break;
                    case PacketDataKeys.GET_OPERATIONS:
                        List<Operation> newOperations = new ArrayList<>(operations);
                        if(loadingNewOperations) {
                            newOperations.remove(operations.size() - 1);
                            loadingNewOperations = false;
                        }
                        newOperations.addAll(JsonUtils.convertJsonNodeToList(json.get(PacketDataKeys.OPERATIONS), Operation.class));
                        refreshOperations(newOperations);
                        break;
                    case PacketDataKeys.PROMOCODE_ENTER:
                        Integer value = json.get(PacketDataKeys.VALUE).intValue();
                        new DialogTextBox(this, String.format("%s%s %s!", getString(R.string.successfully_activate_promocode), value, getString(R.string.of_coins))).show();
                        break;
                }
            }
        });
    }

    @Override
    public void onReceiveError(String str) {

    }
}