package com.example.liisa.danceproject;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

class SimpleAdapter extends ArrayAdapter<Dance> {

    private List<Dance> itemList;
    private final Context context;

    SimpleAdapter(List<Dance> itemList, Context ctx) {
        super(ctx, android.R.layout.simple_list_item_1, itemList);
        this.itemList = itemList;
        this.context = ctx;
    }

    public int getCount() {
        if (itemList != null)
            return itemList.size();
        return 0;
    }

    public Dance getItem(int position) {
        if (itemList != null)
            return itemList.get(position);
        return null;
    }

    public long getItemId(int position) {
        if (itemList != null)
            return itemList.get(position).hashCode();
        return 0;
    }

    public void setItemList(List<Dance> itemList) {
        this.itemList = itemList;
    }

    public List<Dance> getItemList() {
        return itemList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.list_item, null);
        }

        Dance c = itemList.get(position);
        TextView text = (TextView) v.findViewById(R.id.name);
        text.setText(c.name);

        TextView text1 = (TextView) v.findViewById(R.id.pk);
        text1.setText("" + c.pk);

        return v;
    }
}
