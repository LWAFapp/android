package com.Zakovskiy.lwaf;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.Zakovskiy.lwaf.models.User;
import com.Zakovskiy.lwaf.network.SocketHelper;
import com.Zakovskiy.lwaf.utils.PacketDataKeys;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONObject;

import java.util.HashMap;

public class DialogReport extends Dialog {
    private SocketHelper socketHelper = SocketHelper.getSocketHelper();
    private Context context;
    private User user;

    public DialogReport(@NonNull Context context, User user) {
        super(context);
        this.context = context;
        this.user = user;
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        requestWindowFeature(1);
        getWindow().setBackgroundDrawableResource(R.color.transparent);
        setContentView(R.layout.dialog_report);
        setCancelable(true);
        TextView text = findViewById(R.id.dialogReportMainText);
        TextInputLayout tilReason = findViewById(R.id.tilReasonReport);
        text.setText(context.getString(R.string.report_text) + " " + this.user.nickname);
        findViewById(R.id.button_submit).setOnClickListener(v -> {
            HashMap<String, Object> data = new HashMap<>();
            data.put(PacketDataKeys.TYPE_EVENT, PacketDataKeys.REPORT_USER_SEND);
            data.put(PacketDataKeys.CONTENT, tilReason.getEditText().getText().toString());
            data.put(PacketDataKeys.TO_ID, this.user.userId);
            this.socketHelper.sendData(new JSONObject(data));
            DialogReport.this.dismiss();
        });
    }
}
