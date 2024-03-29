package com.example.iot2;

import android.widget.Button;
import android.widget.Switch;

public class MenuList {
    String name_setting;
    boolean isEnable, showSwitch;


    public MenuList(String name_setting, boolean isEnable, boolean showSwitch) {
        this.name_setting = name_setting;
        this.isEnable = isEnable;
        this.showSwitch = showSwitch;
    }

    public String getName_setting() {
        return name_setting;
    }

    public void setName_setting(String name_setting) {
        this.name_setting = name_setting;
    }

    public boolean isEnable() {
        return isEnable;
    }

    public void setEnable(boolean enable) {
        isEnable = enable;
    }

    public boolean isShowSwitch() {
        return showSwitch;
    }

    public void setShowSwitch(boolean showSwitch) {
        this.showSwitch = showSwitch;
    }
}
