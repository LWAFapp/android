package com.Zakovskiy.lwaf.menuDialog.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.Zakovskiy.lwaf.R;
import com.Zakovskiy.lwaf.globalConversation.adapters.MessagesAdapter;
import com.Zakovskiy.lwaf.menuDialog.MenuButton;
import com.Zakovskiy.lwaf.models.ShortUser;
import com.Zakovskiy.lwaf.utils.Logs;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class MenuButtonAdapter extends ArrayAdapter<MenuButton> {
    private Context context;
    private List<MenuButton> buttons;

    public MenuButtonAdapter(Context context, List<MenuButton> buttons) {
        super(context, R.layout.item_menu_button, buttons);
        this.context = context;
        this.buttons = buttons;
    }

    public int getCount() {
        return this.buttons.size();
    }

    @Override
    public MenuButton getItem(int position) {
        return this.buttons.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        MenuButton menuButton = getItem(position);
        Logs.info("MenuButtonAdapter RUNNING"+menuButton.name);
        View view = inflater.inflate(R.layout.item_menu_button, parent, false);
        MaterialButton button = (MaterialButton)view.findViewById(R.id.buttonMenu);
        button.setText(menuButton.name);
        button.setStrokeColor(ColorStateList.valueOf(Color.parseColor(menuButton.hexColor)));
        button.setTextColor(ColorStateList.valueOf(Color.parseColor(menuButton.hexColor)));
        button.setOnClickListener(menuButton.callable);
        return view;
    }
}
