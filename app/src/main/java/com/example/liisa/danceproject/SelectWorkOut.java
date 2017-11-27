package com.example.liisa.danceproject;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SelectWorkOut extends FragmentActivity implements NetworkCallBack {

    private SimpleAdapter adpt;
    private NetworkFragment mNetworkFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_work_out);
        mNetworkFragment = NetworkFragment.getInstance(getSupportFragmentManager());

        adpt = new SimpleAdapter(new ArrayList<Dance>(), this);
        final ListView lView = findViewById(R.id.listview);
        lView.setAdapter(adpt);
        AdapterView.OnItemClickListener listener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int index, long arg3) {
                Dance o = (Dance) lView.getItemAtPosition(index);
                startWorkOutActivity(o);
            }
        };
        lView.setOnItemClickListener(listener);

        try {
            mNetworkFragment.listDances(this);
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
            this.adpt.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void startWorkOutActivity(Dance dance) {
        Intent intent = new Intent(this, Workout.class);
        intent.putExtra("pk", "" + dance.pk);
        intent.putExtra("name", "" + dance.name);
        startActivity(intent);
    }
}