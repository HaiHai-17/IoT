package com.example.iot2;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Switch;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import java.util.ArrayList;

public class SettingMenu extends AppCompatActivity {

    ArrayList<MenuList> data_list;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_menu_layout);


        data_list = new ArrayList<>();

        data_list.add(new MenuList("Đổi sang nền đen", false, true));
        data_list.add(new MenuList("Khác", false, false));

        ListView listView = findViewById(R.id.list_setting);
        MenuSettingAdapter menuSettingAdapter = new MenuSettingAdapter(this, R.layout.icon_menu_setting ,data_list);
        listView.setAdapter(menuSettingAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });

    }
}
