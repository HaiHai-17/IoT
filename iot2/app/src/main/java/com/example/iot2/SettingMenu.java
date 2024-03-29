package com.example.iot2;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Switch;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import java.util.ArrayList;

public class SettingMenu extends AppCompatActivity {

    ArrayList<MenuList> data_list;
    Switch aSwitch;
    View view;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_menu_layout);

        data_list = new ArrayList<>();

        data_list.add(new MenuList("Đổi sang nền đen"));
        data_list.add(new MenuList("Khác"));

        ListView listView = findViewById(R.id.list_setting);
        MenuSettingAdapter menuSettingAdapter = new MenuSettingAdapter(this, R.layout.icon_menu_setting ,data_list);
        listView.setAdapter(menuSettingAdapter);

        aSwitch = findViewById(R.id.swTheme);

        aSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Nếu Switch được bật, sử dụng Theme Dark
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                // Nếu Switch được tắt, sử dụng Theme Light
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }

            // Re-create activity để áp dụng theme mới
            recreate();
        });
    }
}
