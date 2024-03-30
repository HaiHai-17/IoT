package com.example.iot2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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

        NotificationPump1.createNotificationChannel(this);
        NotificationPump2.createNotificationChannel(this);

        registerForContextMenu(pump1);
        registerForContextMenu(pump2);

        pump1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePump(pump1);
            }
        });

        pump2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePump(pump2);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_pump, menu);
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

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        getMenuInflater().inflate(R.menu.menu_pump, menu);
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        AdapterView.AdapterContextMenuInfo info =(AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int position = info.position;
        int id =item.getItemId();
        if(id == R.id.hengio) Toast.makeText(MainActivity.this, "Đây là chức năng hẹn giờ.", Toast.LENGTH_SHORT).show();
        else if (id == R.id.datlich) Toast.makeText(MainActivity.this, "Đây là chức năng đặt lịch.", Toast.LENGTH_SHORT).show();
        return super.onContextItemSelected(item);
    }

    private void togglePump(ImageView pumpImageView) {
        if (pumpImageView.isSelected()) {
            pumpImageView.setImageResource(R.drawable.pumpnocolor);
            showToastAndNotify("Đóng", pumpImageView);
            pumpImageView.setSelected(false);
        } else {
            pumpImageView.setImageResource(R.drawable.pressure);
            showToastAndNotify("Mở", pumpImageView);
            pumpImageView.setSelected(true);
        }
    }

    private void showToastAndNotify(String status, ImageView pumpImageView) {
        String pumpName = pumpImageView == pump1 ? "Máy bơm 1" : "Máy bơm 2";
        Toast.makeText(MainActivity.this, pumpName + " đã " + status + "!", Toast.LENGTH_SHORT).show();
        if (pumpImageView == pump1) {
            NotificationPump1.showNotification(MainActivity.this, "ESP32 IOT", pumpName + ": " + status.toUpperCase());
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference(pumpName);
            myRef.setValue(status);
        } else {
            NotificationPump2.showNotification(MainActivity.this, "ESP32 IOT", pumpName + ": " + status.toUpperCase());
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference(pumpName);
            myRef.setValue(status);
        }
    }
}

