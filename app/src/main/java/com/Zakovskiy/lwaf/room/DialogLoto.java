package com.Zakovskiy.lwaf.room;

import android.app.Dialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.HandlerThread;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.Zakovskiy.lwaf.ABCActivity;
import com.Zakovskiy.lwaf.R;
import com.Zakovskiy.lwaf.application.Application;
import com.Zakovskiy.lwaf.models.enums.CommentReaction;
import com.Zakovskiy.lwaf.network.SocketHelper;
import com.Zakovskiy.lwaf.room.adapters.LotoItemsAdapter;
import com.Zakovskiy.lwaf.room.models.LotoItem;
import com.Zakovskiy.lwaf.utils.JsonUtils;
import com.Zakovskiy.lwaf.utils.Logs;
import com.Zakovskiy.lwaf.utils.PacketDataKeys;
import com.fasterxml.jackson.databind.JsonNode;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class DialogLoto extends Dialog implements SocketHelper.SocketListener {

    private Context context;
    private List<LotoItem> lotoItems = new ArrayList<>();
    private LotoItemsAdapter lotoItemsAdapter;
    private TextView tvLotoTime;
    private RecyclerView rvLotoItems;
    private SocketHelper socketHelper = SocketHelper.getSocketHelper();
    private LinearLayout llTableLotoWinnerSize;
    private Button btnQuestion;
    private LinearLayout llButtonSubmit;

    public DialogLoto(Context context) {
        super(context);
        this.socketHelper.subscribe(this);
        this.context = context;
        this.lotoItemsAdapter = new LotoItemsAdapter(context, lotoItems);
    }

    public void changesItems(List<LotoItem> lotoItems) {
        this.lotoItems.clear();
        this.lotoItems.addAll(lotoItems);
        this.lotoItemsAdapter.notifyDataSetChanged();
    }

    private void sendNumbers(List<Integer> numbers) {
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < numbers.size(); i++) {
            sb.append(numbers.get(i));
            if(i != numbers.size() - 1) {
                sb.append(",");
            }
        }
        String pickedNumbers = sb.toString();
        HashMap<String, Object> dataMessage = new HashMap<>();
        dataMessage.put(PacketDataKeys.TYPE_EVENT, PacketDataKeys.LOTO_SEND_NUMBERS);
        dataMessage.put(PacketDataKeys.LOTO_NUMBERS, pickedNumbers);
        socketHelper.sendData(new JSONObject(dataMessage));
    }

    public void getBalance() {
        HashMap<String, Object> dataMessage = new HashMap<>();
        dataMessage.put(PacketDataKeys.TYPE_EVENT, PacketDataKeys.GET_BALANCE);
        socketHelper.sendData(new JSONObject(dataMessage));
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        requestWindowFeature(1);
        getWindow().setBackgroundDrawableResource(R.color.transparent);
        setContentView(R.layout.dialog_loto);
        setCancelable(true);
        this.rvLotoItems = findViewById(R.id.rvLotoItems);
        this.llTableLotoWinnerSize = findViewById(R.id.llTableLotoWinnerSize);
        this.btnQuestion = findViewById(R.id.btn_questionLoto);
        this.btnQuestion.setOnClickListener((view)->{
            boolean pressed = this.llTableLotoWinnerSize.getVisibility() == View.VISIBLE;
            this.llTableLotoWinnerSize.setVisibility(pressed ? View.GONE : View.VISIBLE);
            this.rvLotoItems.setVisibility(pressed ? View.VISIBLE : View.GONE);
            this.btnQuestion.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(pressed ? R.color.black_primary : R.color.white)));
            this.btnQuestion.setTextColor(context.getResources().getColor(pressed ? R.color.white : R.color.black_primary));
        });
        this.llButtonSubmit = findViewById(R.id.button_submit);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 6);
        rvLotoItems.setAdapter(lotoItemsAdapter);
        rvLotoItems.setLayoutManager(gridLayoutManager);
        rvLotoItems.getRecycledViewPool().setMaxRecycledViews(0, 0);
        this.tvLotoTime = findViewById(R.id.tvLotoTime);
        List<LotoItem> newLotoItems = new ArrayList<>();
        for(int i=1; i <= 30; i++) {
            newLotoItems.add(new LotoItem(i, false));
        }
        changesItems(newLotoItems);
        this.llButtonSubmit.setOnClickListener((view)->{
            newLotoItems.forEach((item)->{
                if(item.pressed) {
                    item.pressed = false;
                }
            });
            Integer countGeneratedNumbers = 1;
            Integer balance = Application.lwafCurrentUser.balance;
            if(balance >= 13) {
                countGeneratedNumbers = 13;
            } else if(balance != 0) {
                countGeneratedNumbers += balance;
            }
            List<Integer> list = new ArrayList<Integer>();
            for (int i=1; i<=30; i++) list.add(i);
            Collections.shuffle(list);
            list = list.subList(0, countGeneratedNumbers);
            Logs.info(list.toString());
            List<LotoItem> changedLotoItems = new ArrayList<>(newLotoItems);
            list.forEach((num)->{
                changedLotoItems.forEach((item)->{
                    if(Objects.equals(item.number, num)) {
                        item.pressed = true;
                    }
                });
            });
            changesItems(changedLotoItems);
        });
        new CountDownTimer(15010, 1000) { // 30 seconds timer with 1 second interval

            public void onTick(long millisUntilFinished) {  // on every tick of timer
                millisUntilFinished = millisUntilFinished / 1000;
                tvLotoTime.setText(String.format("%s %s %s", context.getString(R.string.bets_can_be_placed), millisUntilFinished, context.getString(R.string.seconds)));
            }

            public void onFinish() {  // when timer finishes
                lotoItemsAdapter.setAccess(false);
                llButtonSubmit.setClickable(false);
                List<Integer> pickedItems = new ArrayList<>();
                for(int i=0; i < lotoItems.size(); i++) {
                    if(lotoItems.get(i).pressed) {
                        pickedItems.add(lotoItems.get(i).number);
                    }
                }
                sendNumbers(pickedItems);
            }
        }.start();
    }

    @Override
    public void onConnected() {}

    @Override
    public void onDisconnected() {}

    @Override
    public void onReceive(JsonNode json) {
        if(json.has(PacketDataKeys.ROOM_TYPE_EVENT)) {
            String roomTypeEvent = json.get(PacketDataKeys.ROOM_TYPE_EVENT).asText();
            if(roomTypeEvent.equals(PacketDataKeys.LOTO_RESULT)) {
                List<Integer> lotoResultNumbers = JsonUtils.convertJsonNodeToList(json.get(PacketDataKeys.LOTO_NUMBERS), Integer.class);
                for(int i=0; i < lotoResultNumbers.size(); i++) {
                    Integer num = lotoResultNumbers.get(i);
                    List<LotoItem> newList = new ArrayList<>(lotoItems);
                    rvLotoItems.post(()->{
                        for(int il=0; il < newList.size(); il++) {
                            if(newList.get(il).number.equals(num)) {
                                newList.get(il).valid = true;
                            }
                        }
                        changesItems(newList);
                    });
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                rvLotoItems.post(()->{this.socketHelper.unsubscribe(this); getBalance(); this.dismiss();});
            }
        }
    }

    @Override
    public void onReceiveError(String str) {

    }
}
