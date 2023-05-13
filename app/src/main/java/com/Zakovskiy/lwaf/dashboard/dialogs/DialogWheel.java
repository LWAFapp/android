package com.Zakovskiy.lwaf.dashboard.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

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
import com.Zakovskiy.lwaf.utils.PacketDataKeys;
import com.Zakovskiy.lwaf.wheelspin.SpinningWheelView;
import com.fasterxml.jackson.databind.JsonNode;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DialogWheel extends Dialog implements SocketHelper.SocketListener {
    private Context context;
    private ABCActivity activity;
    private List<String> items = new ArrayList<>();
    private SocketHelper socketHelper = SocketHelper.getSocketHelper();
    private int elementFromStart = 1;
    private final int angleForElement = 360/7;
    private SpinningWheelView wheelView;

    public DialogWheel(ABCActivity activity) {
        super(activity.getApplicationContext());
        this.context = activity.getApplicationContext();
        this.activity = activity;
    }
    @Override
    public void onCreate(Bundle bundle) {
        // -- создание лояута --
        super.onCreate(bundle);
        requestWindowFeature(1);
        getWindow().setBackgroundDrawableResource(R.color.transparent);
        setContentView(R.layout.dialog_wheel);
        setCancelable(true);
        // --  --
        wheelView = (SpinningWheelView) findViewById(R.id.wheel);
        items.add("0 монет");items.add("1 монета");items.add("1 вращение");items.add("3 монеты");items.add("10 монет");items.add("3 вращения");items.add("100 монет");
        wheelView.setItems(items);
        wheelView.setWheelArrowColor(Color.rgb(255,255,255));
        wheelView.setEnabled(false);
        findViewById(R.id.button_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Application.lwafCurrentUser.wheelCount--;
                HashMap<String, Object> dataMessage = new HashMap<>();
                dataMessage.put(PacketDataKeys.TYPE_EVENT, PacketDataKeys.WHEEL_SPIN);
                socketHelper.sendData(new JSONObject(dataMessage));


            }
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
        this.activity.runOnUiThread(() -> {
            if (json.has(PacketDataKeys.ERROR)) {
                new DialogTextBox(this.context, Config.ERRORS.get(json.get(PacketDataKeys.ERROR).asInt())).show();
            } else if (json.has(PacketDataKeys.TYPE_EVENT)) {
                String typeEvent = json.get(PacketDataKeys.TYPE_EVENT).asText();
                switch (typeEvent) {
                    case "wc":
                        Application.lwafCurrentUser.wheelCount = (int) JsonUtils.convertJsonNodeToObject(json.get(PacketDataKeys.WHEEL_COUNT), int.class);
                        elementFromStart = (int) JsonUtils.convertJsonNodeToObject(json.get(PacketDataKeys.WHEEL_ID), int.class);
                        Application.lwafCurrentUser.balance = (int) JsonUtils.convertJsonNodeToObject(json.get(PacketDataKeys.BALANCE), int.class);
                        int interval = 10;
                        int duration = 3000;
                        float anglePerInterval = 20;
                        wheelView.rotate(anglePerInterval*interval, duration+interval, interval);
                        wheelView.setListener(new DialogListener() {
                            @Override
                            public void onStop(String item) {
                                if (wheelView.angle > angleForElement*elementFromStart)
                                    wheelView.rotate(wheelView.angle - angleForElement*elementFromStart);
                                else if (wheelView.angle < angleForElement*elementFromStart)
                                    wheelView.rotate( angleForElement*elementFromStart - wheelView.angle);
                                if (Application.lwafCurrentUser.wheelCount <= 0) {
                                    dismiss();
                                }
                            }
                        });
                        break;

                }
            }
        });
    }

    @Override
    public void onReceiveError(String str) {

    }

    public interface DialogListener {
        void onStop(String item);
    }
}
