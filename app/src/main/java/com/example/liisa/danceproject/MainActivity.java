package com.example.liisa.danceproject;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends FragmentActivity implements SensorEventListener {

    private static String SERVER_URL = "https://sleepy-basin-85659.herokuapp.com/backend/momentary-speed";
    private NetworkFragment mNetworkFragment;
    private URL serverUrl;

    private float[] speed = {0.0f, 0.0f, 0.0f};
    // The time in nanosecond at which the event happened
    private long latestEventTimeStamp = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button createWorkoutButton = findViewById(R.id.create_workout);
        createWorkoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchActivity();
            }
        });

        try {
            serverUrl = new URL(SERVER_URL);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        if (sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION) != null) {
            // success! we have an accelerometer
            Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

            mNetworkFragment = NetworkFragment.getInstance(getSupportFragmentManager());
        } else {
            // fai! we dont have an accelerometer!
        }

    }

    private void launchActivity() {
        Intent intent = new Intent(this, RecordWorkoutActivity.class);
        startActivity(intent);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // Let's sample the acceleration only about twice per second
        float minTimeBetweenSamples = 0.5f;
        long diffInNanoSeconds =  event.timestamp - latestEventTimeStamp;
        float diffInSeconds = toSeconds(diffInNanoSeconds);
        if (diffInSeconds < minTimeBetweenSamples) {
            return;
        }
        latestEventTimeStamp = event.timestamp;
        float[] linear_acceleration  = new float[3];
        linear_acceleration[0] = event.values[0];
        linear_acceleration[1] = event.values[1];
        linear_acceleration[2] = event.values[2];

        speed[0] = (linear_acceleration[0] * diffInSeconds) + speed[0];
        speed[1] = (linear_acceleration[1] * diffInSeconds) + speed[1];
        speed[2] = (linear_acceleration[2] * diffInSeconds) + speed[2];

        try {
            mNetworkFragment.sendSpeedToServer(serverUrl, speed);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private float toSeconds(long diffInNanoSeconds) {
        return (float) (diffInNanoSeconds / Math.pow(10.0, 9.0));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
