package com.example.liisa.danceproject;


import android.os.AsyncTask;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

class NetworkCallTask extends AsyncTask<String, Void, NetworkCallTask.Result> {

    private final NetworkCallBack callBack;

    public NetworkCallTask() {
        callBack = null;
    }

    public NetworkCallTask(NetworkCallBack callBack) {
        this.callBack = callBack;
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
        String url = strings[0];
        String json = strings[1];
        System.out.println(json);
        HttpURLConnection urlConnection;
        try {
            urlConnection = (HttpURLConnection) new URL(url).openConnection();
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
            int responseCode = urlConnection.getResponseCode();
            if (responseCode != HttpsURLConnection.HTTP_OK) {
                throw new IOException("HTTP error code: " + responseCode);
            }
            InputStream stream = urlConnection.getInputStream();
            if (stream != null) {
                // Converts Stream to String with max length of 500.
                String result = readStream(stream, 500);
                return new Result(result);
            }
            return new Result("ok");

        } catch (IOException e) {
            e.printStackTrace();
            return new Result(e);
        } finally {
            urlConnection.disconnect();
        }
    }

    public String readStream(InputStream stream, int maxReadSize)
            throws IOException, UnsupportedEncodingException {
        Reader reader = null;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] rawBuffer = new char[maxReadSize];
        int readSize;
        StringBuffer buffer = new StringBuffer();
        while (((readSize = reader.read(rawBuffer)) != -1) && maxReadSize > 0) {
            if (readSize > maxReadSize) {
                readSize = maxReadSize;
            }
            buffer.append(rawBuffer, 0, readSize);
            maxReadSize -= readSize;
        }
        return buffer.toString();
    }

    @Override
    protected void onPostExecute(Result result) {
        if (result != null && callBack != null) {
            callBack.onCallBack(result);
        }
    }
}
