package com.Zakovskiy.lwaf.dashboard.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.Zakovskiy.lwaf.R;
import com.adefruandta.spinningwheel.SpinningWheelView;

import java.util.ArrayList;
import java.util.List;

public class DialogWheel extends Dialog {
    private Context context;
    private List<String> items = new ArrayList<>();

    public DialogWheel(Context context) {
        super(context);
        this.context = context;
    }
    @Override
    public void onCreate(Bundle bundle) {
        SpinningWheelView wheelView = (SpinningWheelView) findViewById(R.id.wheel);
        items.add("Test1");items.add("Test2");items.add("Test3");
        wheelView.setItems(items);
        wheelView.setEnabled(false);
        findViewById(R.id.button_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wheelView.rotate(360, 300, 50);
            }
        });
    }
}
