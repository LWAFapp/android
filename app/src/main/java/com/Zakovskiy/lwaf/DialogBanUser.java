package com.Zakovskiy.lwaf;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import com.Zakovskiy.lwaf.models.ShortUser;
import com.Zakovskiy.lwaf.network.SocketHelper;
import com.Zakovskiy.lwaf.utils.PacketDataKeys;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONObject;

import java.util.HashMap;

public class DialogBanUser extends Dialog {

    private Context context;
    private SocketHelper socketHelper = SocketHelper.getSocketHelper();
    private ShortUser user;

    public DialogBanUser(Context context, ShortUser user) {
        super(context);
        this.context = context;
        this.user = user;
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        requestWindowFeature(1);
        getWindow().setBackgroundDrawableResource(R.color.transparent);
        setContentView(R.layout.dialog_ban_user);
        setCancelable(true);
        ((TextView)findViewById(R.id.tvNickname)).setText(String.format("%s", user.nickname));
        TextInputLayout seconds = findViewById(R.id.editBanSeconds);
        findViewById(R.id.button_submit).setOnClickListener(view -> {
            HashMap<String, Object> data = new HashMap<>();
            data.put(PacketDataKeys.TYPE_EVENT, PacketDataKeys.ADMIN_BAN_USER);
            data.put(PacketDataKeys.USER_ID, user.userId);
            data.put(PacketDataKeys.TIMESTAMP, (System.currentTimeMillis()/1000) + Integer.parseInt(seconds.getEditText().getText().toString()));
            this.socketHelper.sendData(new JSONObject(data));
            DialogBanUser.this.dismiss();
        });
    }
}
