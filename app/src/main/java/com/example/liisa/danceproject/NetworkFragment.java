package com.example.liisa.danceproject;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class NetworkFragment extends Fragment {

    public static final String TAG = "NetworkFragment";

    public static NetworkFragment getInstance(FragmentManager fragmentManager) {
        NetworkFragment networkFragment = new NetworkFragment();
        fragmentManager.beginTransaction().add(networkFragment, TAG).commit();
        return networkFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void sendSpeedToServer(URL url, float[] speed) throws IOException {
        NetworkCallTask networkCallTask = new NetworkCallTask();
        String json = String.format("{\"%s\": %f, \"%s\": %f, \"%s\": %f }", "x", speed[0], "y", speed[1], "z", speed[2]);
        networkCallTask.execute(url.toString(), json);
    }

    public void addRecordToDance(float[] speed, Integer dance, long time) throws IOException {
        URL url = new URL("https://sleepy-basin-85659.herokuapp.com/backend/record");
        NetworkCallTask networkCallTask = new NetworkCallTask();
        String json = String.format("{\"%s\": %f, \"%s\": %f, \"%s\": %f, \"dance\": %d, \"time\": %d }",
                "x", speed[0], "y", speed[1], "z", speed[2], dance, time);
        networkCallTask.execute(url.toString(), json);
    }

    public void createDance(URL url, String name, NetworkCallBack callBack) throws IOException {
        NetworkCallTask networkCallTask = new NetworkCallTask(callBack);
        String json = String.format("{\"%s\": \"%s\"}", "name", name);
        networkCallTask.execute(url.toString(), json);
    }

    public void listDances(URL url, NetworkCallBack callBack) throws IOException {
        NetworkCallTask networkCallTask = new NetworkCallTask(callBack);
        networkCallTask.execute(url.toString());
    }

    public void sendMovement(URL url, float[] speed, long time, int dance, Workout workout) {
        NetworkCallTask networkCallTask = new NetworkCallTask(workout);
        String json = String.format("{\"%s\": %f, \"%s\": %f, \"%s\": %f, \"time\": %d, \"dance\": %d }",
                "x", speed[0], "y", speed[1], "z", speed[2], time, dance);
        System.out.println(json);
        networkCallTask.execute(url.toString(), json);
    }

}