package com.example.liisa.danceproject;


import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.List;

public class SimpleAdapter extends ArrayAdapter<Dance> {

    private List<Dance> itemList;
    private final Context context;

    public SimpleAdapter(List<Dance> itemList, Context ctx) {
        super(ctx, android.R.layout.simple_list_item_1, itemList);
        this.itemList = itemList;
        this.context = ctx;
    }

    public void setItemList(List<Dance> itemList) {
        this.itemList = itemList;
    }
}
