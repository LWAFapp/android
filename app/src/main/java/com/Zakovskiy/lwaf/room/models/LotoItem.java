package com.Zakovskiy.lwaf.room.models;

public class LotoItem {

    public Integer number = 0;
    public Boolean pressed = false;
    public Boolean valid = false;

    public LotoItem(Integer number, Boolean pressed) {
        this.number = number;
        this.pressed = pressed;
        this.valid = false;
    }
}
