package com.Zakovskiy.lwaf;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

public class DialogReport extends Dialog {
    private Context context;
    private String username;

    public DialogReport(@NonNull Context context, String username) {
        super(context);
        this.context = context;
        this.username = username;
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        requestWindowFeature(1);
        getWindow().setBackgroundDrawableResource(R.color.transparent);
        setContentView(R.layout.dialog_report);
        setCancelable(true);
        TextView text = (TextView) findViewById(R.id.dialogReportMainText);
        text.setText(context.getString(R.string.report_text) + " " + this.username);
        ((LinearLayout) findViewById(R.id.button_submit)).setOnClickListener(v -> {
            //Report here
            new DialogTextBox(context, "Жалоба успешно отправлена");
            DialogReport.this.dismiss();
        });
    }
}
