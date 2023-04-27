package com.Zakovskiy.lwaf;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.Zakovskiy.lwaf.utils.Logs;

public class ABCActivity extends AppCompatActivity {

    public void newActivity(Class activity, Bundle instance) {
        newActivity(activity, false, instance);
    }

    public void newActivity(Class activity) {
        newActivity(activity, false, null);
    }

    public void newActivity(Class activity, boolean hot, Bundle instance) {
        Intent intent = new Intent(getApplicationContext(), activity);
        if (hot)
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        if (instance != null)
            intent.putExtras(instance);
        startActivity(intent);
    }
}
