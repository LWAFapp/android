package com.Zakovskiy.lwaf.dashboard.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import com.Zakovskiy.lwaf.R;
import com.Zakovskiy.lwaf.utils.Logs;
import com.Zakovskiy.lwaf.wheelspin.SpinningWheelView;

import java.util.ArrayList;
import java.util.List;

public class DialogWheel extends Dialog {
    private Context context;
    private List<String> items = new ArrayList<>();
    private final int elementFromStart = 4;
    private final int angleForElement = 360/7;

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
        items.add("Test1");items.add("Test2");items.add("Test3");items.add("Test4");items.add("Test5");items.add("Test6");items.add("Test7");
        wheelView.setItems(items);
        wheelView.setWheelArrowColor(Color.rgb(255,255,255));
        wheelView.setEnabled(false);
        findViewById(R.id.button_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int interval = 10;
                int duration = 3000;
                float anglePerInterval = 20;
                wheelView.rotate(anglePerInterval*interval, duration+interval, interval);
                wheelView.setListener(new DialogListener() {
                    @Override
                    public void onStop(String item) {
                        if (wheelView.angle > angleForElement*elementFromStart)
                            wheelView.rotate(wheelView.angle - angleForElement*elementFromStart);
                        else if (wheelView.angle < angleForElement*elementFromStart)
                            wheelView.rotate( angleForElement*elementFromStart - wheelView.angle);
                        else {}
                    }
                });

            }
        });
    }

    public interface DialogListener {
        void onStop(String item);
    }
}
