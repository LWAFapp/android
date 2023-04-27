package com.Zakovskiy.lwaf;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.Zakovskiy.lwaf.utils.Logs;

public class ABCActivity extends AppCompatActivity {

    public void newActivity(Class activity) {
        newActivity(activity, false);
    }
    public void newActivity(Class activity, boolean hot) {
        Intent intent = new Intent(getApplicationContext(), activity);
        if (hot)
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
