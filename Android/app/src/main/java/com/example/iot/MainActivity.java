package com.example.iot;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
public class MainActivity extends AppCompatActivity {
    MqttAndroidClient mqttAndroidClient;
    ImageView img;
    TextView txt;
    // Thông tin ThingSpeak
    private static final String THINGSPEAK_USERNAME = "HaiHai";
    private static final String THINGSPEAK_CHANNEL_ID = "2403674";
    private static final String THINGSPEAK_API_KEY = "3XG6YJ94GKAZCQDJ";
    // Field trên ThingSpeak để gửi giá trị
    private static final int THINGSPEAK_FIELD_NUMBER = 4;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        img = findViewById(R.id.imageView);
        txt = findViewById(R.id.doC);
        // Tạo MQTT client
        String serverUri = "tcp://mqtt.thingspeak.com:1883"; // Địa chỉ của ThingSpeak MQTT broker
        String clientId = MqttClient.generateClientId();
        mqttAndroidClient = new MqttAndroidClient(this.getApplicationContext(), serverUri, clientId);
        // Tạo options cho kết nối MQTT
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setUserName(THINGSPEAK_USERNAME); // Sử dụng username của bạn
        mqttConnectOptions.setPassword(THINGSPEAK_API_KEY.toCharArray()); // Sử dụng khóa API của bạn
        // Kết nối đến ThingSpeak MQTT broker
        try {
            IMqttToken token = mqttAndroidClient.connect(mqttConnectOptions);
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // Kết nối thành công
                    Toast.makeText(MainActivity.this, "Kết nối MQTT thành công", Toast.LENGTH_SHORT).show();
                    // Đăng ký để lắng nghe các thông điệp từ ThingSpeak
                    mqttAndroidClient.setCallback(new MqttCallback() {
                        @Override
                        public void connectionLost(Throwable cause) {
                            // Xử lý khi kết nối mất
                            // Thử kết nối lại sau một khoảng thời gian
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        mqttAndroidClient.connect(mqttConnectOptions, null, new IMqttActionListener() {
                                            @Override
                                            public void onSuccess(IMqttToken asyncActionToken) {
                                                // Kết nối lại thành công
                                                Toast.makeText(MainActivity.this, "Kết nối MQTT đã được thiết lập lại", Toast.LENGTH_SHORT).show();
                                            }

                                            @Override
                                            public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                                                // Xử lý khi kết nối lại thất bại
                                                Toast.makeText(MainActivity.this, "Kết nối MQTT không thể được thiết lập lại", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    } catch (MqttException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }, 5000); // Thử kết nối lại sau 5 giây
                        }

                        @Override
                        public void messageArrived(String topic, MqttMessage message) throws Exception {
                            // Xử lý khi nhận được thông điệp mới từ ThingSpeak
                            String payload = new String(message.getPayload());
                            // Kiểm tra xem thông điệp có phải từ field1 không
                            if (topic.equals("channels/" + THINGSPEAK_CHANNEL_ID + "/subscribe/fields/field1")) {
                                // Hiển thị giá trị từ field1 lên TextView
                                txt.setText(payload);
                            }
                        }

                        @Override
                        public void deliveryComplete(IMqttDeliveryToken token) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    // Hiển thị thông báo Toast
                                    Toast.makeText(MainActivity.this, "Gửi giá trị thành công", Toast.LENGTH_SHORT).show();

                                    // Thay đổi hình ảnh của ImageView
                                    img.setImageResource(R.drawable.densang); // Thay đổi sang hình ảnh mới
                                }
                            });
                        }

                    });
                }
                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Toast.makeText(MainActivity.this, "Kết nối MQTT thất bại", Toast.LENGTH_SHORT).show();
                }


            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
        // Xử lý sự kiện click cho ImageView
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Gửi giá trị 1 lên ThingSpeak
                String topic = "channels/" + THINGSPEAK_CHANNEL_ID + "/publish/fields/field" + THINGSPEAK_FIELD_NUMBER + "/" + THINGSPEAK_API_KEY;
                String payload = "1";
                try {
                    mqttAndroidClient.publish(topic, payload.getBytes(), 0, false);
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
