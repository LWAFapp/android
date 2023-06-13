package com.Zakovskiy.lwaf;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.Zakovskiy.lwaf.models.User;
import com.Zakovskiy.lwaf.network.SocketHelper;
import com.Zakovskiy.lwaf.utils.PacketDataKeys;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONObject;

import java.util.HashMap;

public class SetNicknameDialog extends Dialog {
    private SocketHelper socketHelper = SocketHelper.getSocketHelper();
    private Context context;
    private Integer type;

    public SetNicknameDialog(@NonNull Context context, Integer type) {
        super(context);
        this.context = context;
        this.type = type;
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        requestWindowFeature(type);
        getWindow().setBackgroundDrawableResource(R.color.transparent);
        setContentView(R.layout.dialog_set_nickname);
        setCancelable(true);
        TextInputLayout tilNewNickname = findViewById(R.id.tilNewNickname);
        TextView tvHelpText = findViewById(R.id.tvHelpText);
        if(type == 0) {
            tvHelpText.setVisibility(View.GONE);
        }
        findViewById(R.id.button_submit).setOnClickListener((view)->{
            String newNickname = tilNewNickname.getEditText().getText().toString();
            if(newNickname.isEmpty()) return;
            HashMap<String, Object> data = new HashMap<>();
            data.put(PacketDataKeys.TYPE_EVENT, PacketDataKeys.CHANGE_NICKNAME);
            data.put(PacketDataKeys.NICKNAME, newNickname);
            this.socketHelper.sendData(new JSONObject(data));
            dismiss();
        });
    }
}