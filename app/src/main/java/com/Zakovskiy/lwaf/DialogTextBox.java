package com.Zakovskiy.lwaf;

import android.app.*;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DialogTextBox extends Dialog {

    private String mMessage;
    private String mTitle;

    public DialogTextBox(Context context, String message) {
        super(context);
        initDialog(null, message);
    }

    public DialogTextBox(Context context, String title, String message) {
        super(context);
        initDialog(title, message);
    }

    private void initDialog(String title, String message) {
        this.mTitle = title == null ? getContext().getString(R.string.information) : title;
        this.mMessage = message;
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        requestWindowFeature(1);
        getWindow().setBackgroundDrawableResource(R.color.transparent);
        setContentView(R.layout.dialog_text_box);
        setCancelable(true);
        ((TextView) findViewById(R.id.titleBox)).setText(this.mTitle);
        ((TextView) findViewById(R.id.textBox)).setText(this.mMessage);
        ((LinearLayout) findViewById(R.id.button_submit)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                DialogTextBox.this.dismiss();
            }
        });
    }

}
