package com.example.iot2;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.UUID;
import java.io.IOException;


public class SettingMenu extends AppCompatActivity {

    ArrayList<MenuList> data_list;
    EditText edtName, edtPass;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_menu_layout);

        data_list = new ArrayList<>();

        data_list.add(new MenuList("Thay đổi WI-FI", false, false));
        data_list.add(new MenuList("Đổi sang nền đen", false, true));
        data_list.add(new MenuList("Khác", false, false));

        ListView listView = findViewById(R.id.list_setting);
        MenuSettingAdapter menuSettingAdapter = new MenuSettingAdapter(this, R.layout.icon_menu_setting, data_list);
        listView.setAdapter(menuSettingAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    editBluetooth();
                }
            }
        });
    }

    private void editBluetooth() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Thay đổi WI-FI");

        View viewInflated = LayoutInflater.from(this).inflate(R.layout.dialog_bluetooth, null);
        edtName = viewInflated.findViewById(R.id.edtNameBluetooth);
        edtPass = viewInflated.findViewById(R.id.edtPassBluetooth);

        builder.setView(viewInflated);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name = edtName.getText().toString().trim();
                String pass = edtPass.getText().toString().trim();

                if (TextUtils.isEmpty(name)) {
                    edtName.setError("Tên không được để trống!");
                }
                if (TextUtils.isEmpty(pass)) {
                    edtPass.setError("Mật khẩu không được để trống!");
                }
        }
        });
        builder.show();
    }
}