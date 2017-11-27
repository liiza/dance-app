package com.example.liisa.danceproject;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.FragmentActivity;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

import static com.example.liisa.danceproject.Utils.milliToNanoSeconds;
import static com.example.liisa.danceproject.Utils.nanoToMilliSeconds;
import static com.example.liisa.danceproject.Utils.nanoToSeconds;

public class RecordWorkoutActivity extends FragmentActivity implements SensorEventListener, NetworkCallBack {

    private NetworkFragment mNetworkFragment;
    private URL serverUrl;
    private SensorManager sensorManager;

    private boolean error = false;
    private Integer dance = null;
    private float[] speed = {0.0f, 0.0f, 0.0f};
    // The uptime of the system in nanoseconds at when the event happened
    private long latestEventTimeStamp;
    private long startime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_workout);
        mNetworkFragment = NetworkFragment.getInstance(getSupportFragmentManager());
        try {
            mNetworkFragment.createDance("TestDance", this);
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
            System.out.println("Dance is null");
            return;
        }
        // Let's sample the acceleration only about twice per second
        float minTimeBetweenSamples = 0.5f;
        long diffInNanoSeconds = event.timestamp - latestEventTimeStamp;
        float diffInSeconds = nanoToSeconds(diffInNanoSeconds);
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
            mNetworkFragment.addRecordToDance(
                speed,
                dance,
                nanoToMilliSeconds(event.timestamp - this.startime));
        } catch (IOException e) {
            e.printStackTrace();
        }
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
                this.latestEventTimeStamp = this.startime;
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
}
