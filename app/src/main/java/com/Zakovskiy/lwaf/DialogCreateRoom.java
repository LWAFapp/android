package com.Zakovskiy.lwaf;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.Zakovskiy.lwaf.application.Application;
import com.Zakovskiy.lwaf.dashboard.DashboardFragment;
import com.Zakovskiy.lwaf.network.SocketHelper;
import com.Zakovskiy.lwaf.utils.Config;
import com.Zakovskiy.lwaf.utils.PacketDataKeys;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONObject;

import java.util.HashMap;

public class DialogCreateRoom extends Dialog {

    private Context context;
    private SocketHelper socketHelper = SocketHelper.getSocketHelper();

    public DialogCreateRoom(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        requestWindowFeature(1);
        getWindow().setBackgroundDrawableResource(R.color.transparent);
        setContentView(R.layout.dialog_create_room);
        setCancelable(true);
        String last_room_name = this.context.getSharedPreferences("lwaf_user", 0).getString("last_room_name", "");
        Integer last_players_count_size = this.context.getSharedPreferences("lwaf_user", 0).getInt("last_room_players_count_size", 5);
        TextInputLayout roomName = findViewById(R.id.editRoomTitle);
        roomName.getEditText().setText(last_room_name);
        TextInputLayout roomPlayersCountSize = findViewById(R.id.editRoomPlayersCount);
        roomPlayersCountSize.getEditText().setText(String.valueOf(last_players_count_size));
        TextInputLayout roomPassword = findViewById(R.id.editRoomPassword);
        findViewById(R.id.button_submit).setOnClickListener(view -> {
            HashMap<String, Object> data = new HashMap<>();
            data.put(PacketDataKeys.TYPE_EVENT, PacketDataKeys.ROOM_CREATE);
            data.put(PacketDataKeys.ROOM_NICKNAME, roomName.getEditText().getText().toString());
            data.put(PacketDataKeys.ROOM_PASSWORD, roomPassword.getEditText().getText().toString());
            data.put(PacketDataKeys.ROOM_PLAYERS_COUNT, Integer.valueOf(roomPlayersCountSize.getEditText().getText().toString()));
            this.socketHelper.sendData(new JSONObject(data));
            SharedPreferences sPref = this.context.getSharedPreferences("lwaf_user", 0);
            SharedPreferences.Editor ed = sPref.edit();
            ed.putString("last_room_name", roomName.getEditText().getText().toString());
            ed.putInt("last_room_players_count_size", Integer.parseInt(roomPlayersCountSize.getEditText().getText().toString()));
            ed.apply();
            DialogCreateRoom.this.dismiss();
        });
    }
}
