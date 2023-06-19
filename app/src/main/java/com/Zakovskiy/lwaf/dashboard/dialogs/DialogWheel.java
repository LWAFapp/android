package com.Zakovskiy.lwaf.dashboard.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.Zakovskiy.lwaf.ABCActivity;
import com.Zakovskiy.lwaf.DialogTextBox;
import com.Zakovskiy.lwaf.R;
import com.Zakovskiy.lwaf.application.Application;
import com.Zakovskiy.lwaf.dashboard.DashboardFragment;
import com.Zakovskiy.lwaf.models.ServerConfig;
import com.Zakovskiy.lwaf.models.User;
import com.Zakovskiy.lwaf.network.SocketHelper;
import com.Zakovskiy.lwaf.utils.Config;
import com.Zakovskiy.lwaf.utils.JsonUtils;
import com.Zakovskiy.lwaf.utils.Logs;
import com.Zakovskiy.lwaf.utils.PacketDataKeys;
import com.bluehomestudio.luckywheel.LuckyWheel;
import com.bluehomestudio.luckywheel.WheelItem;
import com.fasterxml.jackson.databind.JsonNode;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DialogWheel extends Dialog implements SocketHelper.SocketListener {
    private Context context;
    private List<WheelItem> wheelItemList = new ArrayList<>();
    private SocketHelper socketHelper = SocketHelper.getSocketHelper();
    private LuckyWheel wheelView;
    private TextView tvAmountSpins;
    private TextView btnSubmitText;
    private LinearLayout btnSubmit;
    private Boolean isRotate = false;

    public DialogWheel(Context context) {
        super(context);
        this.context = context;
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
        this.tvAmountSpins = findViewById(R.id.wheelSpinsAmountTitle);
        this.btnSubmitText = findViewById(R.id.btnSubmitText);
        refreshAmountWheels(0);
        wheelView = findViewById(R.id.wheel);
        for(int i = 0; i < Application.lwafServerConfig.wheelItems.size(); i++) {
            ServerConfig.WheelItem wheelItem = Application.lwafServerConfig.wheelItems.get(i);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Bitmap bitmapFactory = BitmapFactory.decodeResource(context.getResources(), wheelItem.getDrawable());
                Bitmap bitmap = Bitmap.createScaledBitmap(bitmapFactory, 60, 60, false);;
                WheelItem newItem = new WheelItem(context.getColor(wheelItem.getColor()),
                        bitmap, String.valueOf(wheelItem.amount));
                wheelItemList.add(newItem);
            }
        }
        wheelView.addWheelItems(wheelItemList);

        socketHelper.subscribe(this);
        this.btnSubmit = findViewById(R.id.button_submit);
        this.btnSubmit.setOnClickListener(v -> {
            if(canSpin()) {
                if(!isRotate) {
                    isRotate = true;
                    HashMap<String, Object> dataMessage = new HashMap<>();
                    dataMessage.put(PacketDataKeys.TYPE_EVENT, PacketDataKeys.WHEEL_SPIN);
                    socketHelper.sendData(new JSONObject(dataMessage));
                }
            } else {
                dismiss();
            }
        });
    }

    @Override
    public void onStop() {
        socketHelper.unsubscribe(this);
        super.onStop();
    }

    @Override
    public void onConnected() {

    }

    @Override
    public void onDisconnected() {

    }

    private boolean canSpin() {
        return Application.lwafCurrentUser.wheelCount > 0;
    }

    private void refreshAmountWheels() {
        refreshAmountWheels(9000);
    }

    private void refreshAmountWheels(int delay) {
        this.tvAmountSpins.postDelayed(()->{
            this.tvAmountSpins.setText(String.format("%s %s", context.getString(R.string.you_have), Application.lwafCurrentUser.wheelCount));
            isRotate = false;
            if(!canSpin()) {
                this.btnSubmitText.setText(context.getString(R.string.cancel));
            }
        }, delay);
    }

    @Override
    public void onReceive(JsonNode json) {
        if (json.has(PacketDataKeys.TYPE_EVENT)) {
            String typeEvent = json.get(PacketDataKeys.TYPE_EVENT).asText();
            switch (typeEvent) {
                case "ws":
                    Integer wheelsCount = json.get(PacketDataKeys.WHEEL_COUNT).asInt();
                    Application.lwafCurrentUser.wheelCount = wheelsCount;
                    Integer elementFromStart = json.get(PacketDataKeys.WHEEL_ID).asInt() + 1;
                    Logs.info(String.valueOf(elementFromStart));
                    Application.lwafCurrentUser.balance = json.get(PacketDataKeys.BALANCE).asInt();
                    isRotate = true;
                    wheelView.rotateWheelTo(elementFromStart);
                    refreshAmountWheels();
                    break;

            }
        }
    }

    @Override
    public void onReceiveError(String str) {

    }

    public interface DialogListener {
        void onStop(ServerConfig.WheelItem item);
    }
}
