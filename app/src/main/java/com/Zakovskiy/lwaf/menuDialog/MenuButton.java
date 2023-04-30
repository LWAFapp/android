package com.Zakovskiy.lwaf.menuDialog;

import android.view.View;

import java.util.concurrent.Callable;

public class MenuButton {
    public String name;
    public String hexColor;
    public View.OnClickListener callable;

    public MenuButton(String name, String hexColor, View.OnClickListener callable) {
        this.name = name;
        this.hexColor = hexColor;
        this.callable = callable;
    }
}
