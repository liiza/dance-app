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

public class Workout extends FragmentActivity implements NetworkCallBack, SensorEventListener {

    private String pk;
    private String name;
    private NetworkFragment mNetworkFragment;
    private long latestEventTimeStamp;
    private float[] speed = {0.0f, 0.0f, 0.0f};
    private long startime;
    private SensorManager sensorManager;
    private float diffX = 0.0f;
    private float diffY = 0.0f;
    private float diffZ = 0.0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout);

        this.pk = getIntent().getStringExtra("pk");
        TextView tv1 = findViewById(R.id.workout_pk);
        tv1.setText(this.pk);

        this.name = getIntent().getStringExtra("name");
        TextView tv2 = findViewById(R.id.workout_name);
        tv2.setText(this.name);

        mNetworkFragment = NetworkFragment.getInstance(getSupportFragmentManager());


        this.startime = milliToNanoSeconds(SystemClock.uptimeMillis());
        this.latestEventTimeStamp = this.startime;

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
    public void onCallBack(NetworkCallTask.Result result) {
        if (result.mResultValue != null) {
            System.out.println(result.mResultValue);
            try {
                JSONObject jObject = new JSONObject(result.mResultValue);
                boolean ended = jObject.getBoolean("ended");
                if (ended) {
                    this.name = getIntent().getStringExtra("name");
                    TextView tv2 = findViewById(R.id.workout_name);
                    String diff = String.format("%f, %f, %f", diffX, diffY, diffZ);
                    tv2.setText(diff);
                    return;
                }
                diffX += Float.valueOf(jObject.getString("diff_x"));
                diffY += Float.valueOf(jObject.getString("diff_y"));
                diffZ += Float.valueOf(jObject.getString("diff_z"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println(result.mException);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

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
        sendMovement(speed, nanoToMilliSeconds(event.timestamp - this.startime));
    }

    public void sendMovement(float[] speed, long time) {
        try {
            mNetworkFragment.sendMovement(
                    speed, time, Integer.parseInt(this.pk), this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
