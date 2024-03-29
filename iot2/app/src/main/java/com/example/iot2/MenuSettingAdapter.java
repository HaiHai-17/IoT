package com.example.iot2;

import android.content.Context;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;

public class MenuSettingAdapter extends BaseAdapter {
    Context context;
    int layout;
    ArrayList<MenuList> data_list;

    public MenuSettingAdapter(Context context, int layout, ArrayList<MenuList> data_list) {
        this.context = context;
        this.layout = layout;
        this.data_list = data_list;
    }

    @Override
    public int getCount() {
        return data_list.size();
    }

    @Override
    public Object getItem(int position) {
        return data_list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.icon_menu_setting, null);

        MenuList ds = data_list.get(position);
        TextView textView = convertView.findViewById(R.id.txtTheme);
        textView.setText(ds.getName_setting());

        Switch aSwitch = convertView.findViewById(R.id.swTheme);

        if(ds.isShowSwitch()){
            aSwitch.setVisibility(View.VISIBLE);
            aSwitch.setChecked(ds.isEnable());
            aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    ds.setEnable(isChecked);
                }
            });
        }
        else{
            aSwitch.setVisibility(View.GONE);
        }

        return convertView;
    }
}
