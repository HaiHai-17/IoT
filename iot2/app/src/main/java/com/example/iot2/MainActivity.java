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

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    ImageView pump1, pump2, rain, human;
    ProgressBar humi_bar, temp_bar;
    ValueEventListener valueHumi, valueTemp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseApp.initializeApp(this);

        pump1 = findViewById(R.id.imgPump1);
        pump2 = findViewById(R.id.imgPump2);
        rain = findViewById(R.id.imgRain);
        human = findViewById(R.id.imgHuman);
//        humi_bar = findViewById(R.id.barHumi);
//        temp_bar = findViewById(R.id.barTemp);

        registerForContextMenu(pump1);
        registerForContextMenu(pump2);

//        DatabaseReference databaseHumi = FirebaseDatabase.getInstance().getReference().child("humidity");
//        DatabaseReference databaseTemp = FirebaseDatabase.getInstance().getReference().child("temperature");
        DatabaseReference databaseRain = FirebaseDatabase.getInstance().getReference().child("rain");
        DatabaseReference databaseHuman = FirebaseDatabase.getInstance().getReference("human");

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
                // Kiểm tra xem dữ liệu có tồn tại không
                if (snapshot.exists()) {
                    // Lấy giá trị từ snapshot
                    String value = snapshot.getValue(String.class);
                    // Kiểm tra xem giá trị có khác null không
                    if (value != null) {
                        // So sánh giá trị với chuỗi "1"
                        if (value.equals("1")) {
                            rain.setImageResource(R.drawable.light_rain);
                        } else {
                            rain.setImageResource(R.drawable.rain_day);
                        }
                    } else {
                        // Xử lý trường hợp giá trị là null
                        // Ví dụ: Hiển thị một hình ảnh mặc định hoặc thông báo lỗi
                    }
                } else {
                    // Xử lý trường hợp không có dữ liệu trong nút "rain"
                    // Ví dụ: Hiển thị một hình ảnh mặc định hoặc thông báo lỗi
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý khi có lỗi xảy ra trong quá trình đọc dữ liệu từ Firebase
                // Ví dụ: Hiển thị một thông báo lỗi
            }
        });


        databaseHuman.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Kiểm tra xem dữ liệu có tồn tại không
                if (snapshot.exists()) {
                    // Lấy giá trị từ snapshot
                    String value = snapshot.getValue(String.class);
                    // Kiểm tra xem giá trị có khác null không
                    if (value != null) {
                        // So sánh giá trị với chuỗi "1"
                        if (value.equals("1")) {
                            human.setImageResource(R.drawable.human);
                        } else {
                            human.setImageResource(R.drawable.nohuman);
                        }
                    } else {
                        // Xử lý trường hợp giá trị là null
                        // Ví dụ: Hiển thị một hình ảnh mặc định hoặc thông báo lỗi
                    }
                } else {
                    // Xử lý trường hợp không có dữ liệu trong nút "human"
                    // Ví dụ: Hiển thị một hình ảnh mặc định hoặc thông báo lỗi
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý khi có lỗi xảy ra trong quá trình đọc dữ liệu từ Firebase
                // Ví dụ: Hiển thị một thông báo lỗi
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
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference("pump1");
            if(status.equals("Đóng")) {
                myRef.setValue(0);
            } else if (status.equals("Mở")) {
                myRef.setValue(1);
            }
        } else {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference("pump2");
            if(status.equals("Đóng")) {
                myRef.setValue(0);
            } else if (status.equals("Mở")) {
                myRef.setValue(1);
            }
        }
    }
}

