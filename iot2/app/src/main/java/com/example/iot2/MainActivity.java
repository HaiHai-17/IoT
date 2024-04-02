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
import android.widget.ProgressBar;
import android.widget.Toast;

import com.github.lzyzsd.circleprogress.CircleProgress;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    ImageView pump1, pump2, rain, human;
    CircleProgress humi_bar, temp_bar;
    int valueHumi, valueTemp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseApp.initializeApp(this);

        pump1 = findViewById(R.id.imgPump1);
        pump2 = findViewById(R.id.imgPump2);
        rain = findViewById(R.id.imgRain);
        human = findViewById(R.id.imgHuman);
        humi_bar = findViewById(R.id.barHumi);
        temp_bar = findViewById(R.id.barTemp);

        registerForContextMenu(pump1);
        registerForContextMenu(pump2);

        Notification.createNotificationChannel(MainActivity.this);

        DatabaseReference databaseHumi = FirebaseDatabase.getInstance().getReference("/dht11/humidity");
        DatabaseReference databaseTemp = FirebaseDatabase.getInstance().getReference("/dht11/temperature");
        DatabaseReference databaseRain = FirebaseDatabase.getInstance().getReference("/sensor/rain");
        DatabaseReference databaseHuman = FirebaseDatabase.getInstance().getReference("/sensor/human");

        databaseHumi.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                valueHumi = snapshot.getValue(Integer.class);
                humi_bar.setProgress(valueHumi);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                humi_bar.setProgress(0);
                Notification.showNotification(MainActivity.this, "ESP32 IOT", "Lỗi cảm biến nhiệt độ!!!");
            }
        });

        databaseTemp.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                valueTemp = snapshot.getValue(Integer.class);
                temp_bar.setProgress(valueTemp);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                temp_bar.setProgress(0);
                Notification.showNotification(MainActivity.this, "ESP32 IOT", "Lỗi cảm biến nhiệt độ!!!");
            }
        });

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

        databaseRain.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String value = snapshot.getValue(String.class);
                    if(value.equals("co")) {
                        rain.setImageResource(R.drawable.light_rain);
                        Notification.showNotification(MainActivity.this, "ESP32 IOT", "Trời đang có mưa!!!");
                    }
                    else {
                        rain.setImageResource(R.drawable.rain_day);
                        Notification.showNotification(MainActivity.this, "ESP32 IOT", "Trời không có mưa!!!");
                    }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý khi có lỗi xảy ra trong quá trình đọc dữ liệu từ Firebase
                rain.setImageResource(R.drawable.caution);
                Notification.showNotification(MainActivity.this, "ESP32 IOT", "Lỗi cảm biến mưa!!!");
            }
        });


        databaseHuman.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String value = snapshot.getValue(String.class);
                    if(value.equals("co")) {
                        human.setImageResource(R.drawable.human);
                        Notification.showNotification(MainActivity.this, "ESP32 IOT", "Có người vào vườn!!!");
                    }
                    else
                        human.setImageResource(R.drawable.nohuman);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý khi có lỗi xảy ra trong quá trình đọc dữ liệu từ Firebase
                human.setImageResource(R.drawable.caution);
                Notification.showNotification(MainActivity.this, "ESP32 IOT", "Lỗi cảm biến người!!!");
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

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        getMenuInflater().inflate(R.menu.menu_pump, menu);
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        AdapterView.AdapterContextMenuInfo info =(AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
//        int position = info.position;
        int id = item.getItemId();
        if(id == R.id.hengio)
            Toast.makeText(MainActivity.this, "Đây là chức năng hẹn giờ.", Toast.LENGTH_SHORT).show();
        else if (id == R.id.datlich)
            Toast.makeText(MainActivity.this, "Đây là chức năng đặt lịch.", Toast.LENGTH_SHORT).show();
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
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference("/pump/pump1");
            if(status.equals("Đóng")) {
                myRef.setValue(0);
            } else if (status.equals("Mở")) {
                myRef.setValue(1);
            }
        } else {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference("/pump/pump2");
            if(status.equals("Đóng")) {
                myRef.setValue(0);
            } else if (status.equals("Mở")) {
                myRef.setValue(1);
            }
        }
    }
}

