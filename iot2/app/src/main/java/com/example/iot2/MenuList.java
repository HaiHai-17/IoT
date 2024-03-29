package com.example.iot2;

import android.widget.Button;
import android.widget.Switch;

public class MenuList {
    String name_setting;

    public MenuList(String name_setting) {
        this.name_setting = name_setting;
    }

    public String getName_setting() {
        return name_setting;
    }

    public void setName_setting(String name_setting) {
        this.name_setting = name_setting;
    }

}
