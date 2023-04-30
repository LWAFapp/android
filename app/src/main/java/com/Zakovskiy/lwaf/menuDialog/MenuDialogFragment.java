package com.Zakovskiy.lwaf.menuDialog;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.DialogFragment;

import com.Zakovskiy.lwaf.R;

import java.util.List;

public class MenuDialogFragment extends DialogFragment {

    private List<MenuButton> buttons;

    public MenuDialogFragment(List<MenuButton> buttons) {
        this.buttons = buttons;
    }

    @Override
    public void onCreate(Bundle instance) {
        super.onCreate(instance);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle instance) {
        View view = inflater.inflate(R.layout.dialog_room_password, container, false); // test
        return view;
    }
}
