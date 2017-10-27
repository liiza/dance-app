package com.example.liisa.danceproject;


import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class SelectWorkOut extends FragmentActivity implements NetworkCallBack{

    private SimpleAdapter adpt;
    private NetworkFragment mNetworkFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        adpt  = new SimpleAdapter(new ArrayList<Dance>(), this);
        ListView lView = (ListView) findViewById(R.id.listview);

        lView.setAdapter(adpt);

        mNetworkFragment = NetworkFragment.getInstance(getSupportFragmentManager());
        try {
            mNetworkFragment.listDances(new URL("https://sleepy-basin-85659.herokuapp.com/backend/dance"), this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCallBack(NetworkCallTask.Result result) {
        try {
            JSONArray jsonArray = new JSONArray(result.mResultValue);
            List<Dance> dances = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                dances.add(new Dance(object.getInt("pk"), object.getString("name")));
            }
            this.adpt.setItemList(dances);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}