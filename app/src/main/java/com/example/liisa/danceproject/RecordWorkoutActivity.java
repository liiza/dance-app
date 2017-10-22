package com.example.liisa.danceproject;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class RecordWorkoutActivity extends FragmentActivity implements SensorEventListener, NetworkCallBack {

    private static String SERVER_URL = "https://sleepy-basin-85659.herokuapp.com/backend/dance";
    private NetworkFragment mNetworkFragment;
    private URL serverUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_workout);
        try {
            serverUrl = new URL(SERVER_URL);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        mNetworkFragment = NetworkFragment.getInstance(getSupportFragmentManager());
        try {
            mNetworkFragment.createDance(serverUrl, "TestDance", this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public void onCallBack(NetworkCallTask.Result result) {
        if (result.mResultValue != null) {
            System.out.println(result.mResultValue);
        } else if (result.mException != null) {
            System.out.println(result.mException);
        }
    }
}
