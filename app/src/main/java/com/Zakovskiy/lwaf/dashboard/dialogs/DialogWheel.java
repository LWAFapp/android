package com.Zakovskiy.lwaf.dashboard.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import com.Zakovskiy.lwaf.R;
import com.Zakovskiy.lwaf.wheelspin.SpinningWheelView;

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
        // -- создание лояута --
        super.onCreate(bundle);
        requestWindowFeature(1);
        getWindow().setBackgroundDrawableResource(R.color.transparent);
        setContentView(R.layout.dialog_wheel);
        setCancelable(true);
        // --  --
        SpinningWheelView wheelView = (SpinningWheelView) findViewById(R.id.wheel);
        items.add("Test1");items.add("Test2");items.add("Test3");
        wheelView.setItems(items);
        wheelView.setWheelArrowColor(Color.rgb(255,255,255));
        wheelView.setEnabled(false);
        findViewById(R.id.button_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wheelView.rotate(360, 3000, 50);
            }
        });
    }
}
