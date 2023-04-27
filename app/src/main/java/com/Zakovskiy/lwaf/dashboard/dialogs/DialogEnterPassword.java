package com.Zakovskiy.lwaf.dashboard.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.EditText;

import com.Zakovskiy.lwaf.R;
import com.Zakovskiy.lwaf.network.SocketHelper;
import com.Zakovskiy.lwaf.utils.PacketDataKeys;

import org.json.JSONObject;

import java.util.HashMap;

public class DialogEnterPassword extends Dialog {
    private Context context;
    private String roomId;
    private SocketHelper socketHelper = SocketHelper.getSocketHelper();

    public DialogEnterPassword(Context context, String roomId) {
        super(context);
        this.context = context;
        this.roomId = roomId;
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        requestWindowFeature(1);
        getWindow().setBackgroundDrawableResource(R.color.transparent);
        setContentView(R.layout.dialog_room_password);
        setCancelable(true);
        EditText roomPassword = findViewById(R.id.editRoomPassword);
        findViewById(R.id.button_submit).setOnClickListener(view -> {
            HashMap<String, Object> data = new HashMap<>();
            data.put(PacketDataKeys.TYPE_EVENT, PacketDataKeys.ROOM_JOIN);
            data.put(PacketDataKeys.ROOM_IDENTIFICATOR, this.roomId);
            data.put(PacketDataKeys.ROOM_PASSWORD, roomPassword);
            socketHelper.sendData(new JSONObject(data));
            DialogEnterPassword.this.dismiss();
        });
    }
}
