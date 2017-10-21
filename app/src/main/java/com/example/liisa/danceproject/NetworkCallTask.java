package com.example.liisa.danceproject;


import android.os.AsyncTask;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

class NetworkCallTask extends AsyncTask<String, Void, NetworkCallTask.Result> {

    private final float[] speed;

    NetworkCallTask(float[] speed) {
        super();
        this.speed = speed;
    }

    static class Result {
        public String mResultValue;
        public Exception mException;
        public Result(String resultValue) {
            mResultValue = resultValue;
        }
        public Result(Exception exception) {
            mException = exception;
        }
    }

    @Override
    protected NetworkCallTask.Result doInBackground(String... strings) {
        String json = String.format("\"{'%s': %f, '%s': %f, '%s': %f }\"", "x", speed[0], "y", speed[1], "z", speed[2]);
        System.out.println(json);
        HttpURLConnection urlConnection;
        try {
            urlConnection = (HttpURLConnection) new URL(strings[0]).openConnection();
        } catch (IOException e) {
            e.printStackTrace();
            return new Result(e);
        }
        try {
            urlConnection.setDoOutput(true);
            urlConnection.setFixedLengthStreamingMode(json.getBytes().length);
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestMethod("POST");

            OutputStream out = new BufferedOutputStream(urlConnection.getOutputStream());
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
            writer.write(json);
            writer.flush();
            writer.close();
            out.close();
            return new Result("ok");

        } catch (Exception e) {
            e.printStackTrace();
            return new Result(e);
        } finally {
            urlConnection.disconnect();
        }
    }
}
