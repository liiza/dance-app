package com.example.liisa.danceproject;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.TextView;
import android.os.SystemClock;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class RecordWorkoutActivity extends FragmentActivity implements SensorEventListener, NetworkCallBack {

    private static String SERVER_URL = "https://sleepy-basin-85659.herokuapp.com/backend/dance";
    private NetworkFragment mNetworkFragment;
    private URL serverUrl;

    private SensorManager sensorManager;

    private boolean error = false;
    private Integer dance = null;
    private float[] speed = {0.0f, 0.0f, 0.0f};
    // The uptime of the system in nanoseconds at when the event happened
    private long latestEventTimeStamp = 0;
    private long startime;


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

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        if (sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION) != null) {
            // success! we have an accelerometer
            Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            // fai! we dont have an accelerometer!
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (dance == null) {
            return;
        }
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
            mNetworkFragment.addRecordToDance(serverUrl, speed, dance, event.timestamp - this.startime);
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

    @Override
    public void onCallBack(NetworkCallTask.Result result) {
        if (result.mResultValue != null) {
            this.error = false;
            System.out.println(result.mResultValue);
            try {
                JSONObject jObject = new JSONObject(result.mResultValue);
                this.startime = milliToNanoSeconds(SystemClock.uptimeMillis());
                this.dance = jObject.getInt("pk");
            } catch (JSONException e) {
                this.error = true;
                e.printStackTrace();
            }
        } else if (result.mException != null) {
            this.error = true;
            System.out.println(result.mException);
        }

        TextView tv1 = findViewById(R.id.status_text);

        if (this.error) {
            tv1.setText("Something went wrong");
        } else {
            tv1.setText("Recording started");
        }
    }

    private long milliToNanoSeconds(long milliSeconds) {
        return (long) (milliSeconds * Math.pow(10, 6));
    }
}
