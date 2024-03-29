package com.example.iot2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    ImageView pump1, pump2, rain, human;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pump1 = findViewById(R.id.imgPump1);
        pump2 = findViewById(R.id.imgPump2);
        rain = findViewById(R.id.imgRain);
        human = findViewById(R.id.imgHuman);

        pump1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v.isSelected()) {
                    pump1.setImageResource(R.drawable.pressure);
                    Toast.makeText(MainActivity.this, "Máy bơm 1 đã mở!", Toast.LENGTH_SHORT).show();
                    v.setSelected(false);
                }
                else{
                    pump1.setImageResource(R.drawable.pumpnocolor);
                    Toast.makeText(MainActivity.this, "Máy bơm 1 đã đóng!", Toast.LENGTH_SHORT).show();
                    v.setSelected(true);
                }
            }
        });

        pump2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v.isSelected()) {
                    pump2.setImageResource(R.drawable.pressure);
                    Toast.makeText(MainActivity.this, "Máy bơm 2 đã mở!", Toast.LENGTH_SHORT).show();
                    v.setSelected(false);
                }
                else{
                    pump2.setImageResource(R.drawable.pumpnocolor);
                    Toast.makeText(MainActivity.this, "Máy bơm 2 đã đóng!", Toast.LENGTH_SHORT).show();
                    v.setSelected(true);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_layout, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_setting) {
            Intent intent = new Intent(this, SettingMenu.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_exit) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}

