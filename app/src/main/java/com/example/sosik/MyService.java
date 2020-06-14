package com.example.sosik;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.os.Vibrator;
import androidx.core.app.NotificationCompat;

public class MyService extends Service implements SensorEventListener {

    private static final int NOTIF_ID = 1;
    private static final String NOTIF_CHANNEL_ID = "Sosik";

    private SensorManager mSensorManager;
    private Sensor accelerormeterSensor;
    private Vibrator vibrator;

    private long lastTime;
    private long lastShake;
    private int shakeCount;
    private float speed;
    private float lastX;
    private float lastY;
    private float lastZ;
    private float x, y, z;

    private static final int SHAKE_THRESHOLD = 800;   //스피드
    private static final int SHAKE_DURATION = 1000;   //기간
    private static final int SHAKE_TIME = 2;           //횟수
    private static final int DATA_X = SensorManager.DATA_X;
    private static final int DATA_Y = SensorManager.DATA_Y;
    private static final int DATA_Z = SensorManager.DATA_Z;

    public MyService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            return Service.START_STICKY; //서비스 종료시 재실행
        } else {
            startForeground();
            mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
            Sensor sensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            mSensorManager.registerListener(this, accelerormeterSensor, SensorManager.SENSOR_DELAY_NORMAL);
            return Service.START_STICKY;
        }
    }

    @Override
    public void onCreate() {
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerormeterSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            long currentTime = System.currentTimeMillis();
            long gabOfTime = (currentTime - lastTime);
            if (gabOfTime > 100) {
                x = event.values[SensorManager.DATA_X];
                y = event.values[SensorManager.DATA_Y];
                z = event.values[SensorManager.DATA_Z];
                speed = Math.abs(x + y + z - lastX - lastY - lastZ) / gabOfTime * 10000;

                if (speed > SHAKE_THRESHOLD) {
                    shakeCount++;
                    long gabOfShake = currentTime - lastShake;
                    if (gabOfShake < SHAKE_DURATION ){
                        lastShake = currentTime;
                        if (shakeCount >= SHAKE_TIME){
                            shakeCount = 0;
                            showIntent();
                        }
                    } else {
                        lastShake = currentTime;
                        shakeCount = 0;
                    }
                }
                lastTime = currentTime;
                lastX = event.values[DATA_X];
                lastY = event.values[DATA_Y];
                lastZ = event.values[DATA_Z];
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    private void startForeground() {
        Intent notificationIntent = new Intent(this, MainActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        notificationChannel();

        startForeground(NOTIF_ID, new NotificationCompat.Builder(this,
                NOTIF_CHANNEL_ID) // don't forget create a notification channel first
                .setOngoing(true)
                .setSmallIcon(R.drawable.ic_noticon)
                .setContentTitle(getString(R.string.app_name))
                .setContentText("2~3회 흔들면 앱이 실행됩니다.")
                .setContentIntent(pendingIntent)
                .build());
    }

    private void notificationChannel() {
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        CharSequence name = getString(R.string.channel_name);
        int importance = NotificationManager.IMPORTANCE_LOW;
        NotificationChannel mChannel = new NotificationChannel(NOTIF_CHANNEL_ID, name, importance);
        mNotificationManager.createNotificationChannel(mChannel);
    }

    protected void showIntent() {
        Intent showIntent = new Intent(getApplicationContext(), MainActivity.class);

        if (!MainActivity.isActivityVisible()){
            showIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            vibrator.vibrate(100);
            startActivity(showIntent);
        }
    }

}