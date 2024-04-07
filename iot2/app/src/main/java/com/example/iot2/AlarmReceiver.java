package com.example.iot2;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Notification.createNotificationChannel(context);
        Notification.showNotification(context, "ESP32 IOT", "Báo thức đã được kích hoạt!");
    }
}

