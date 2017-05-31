package com.lunchareas.arti;

/* LogAdapter.java
 * v1.1.0
 * 2017-05-30
 *
 * Copyright (C) 2017  Vanshaj Singhania, David Zhang, Emil Tu
 * Full copyright information available in MainActivity.java
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class LogAdapter extends BaseAdapter {

    List<ArtiLog> logs;

    public LogAdapter(List<ArtiLog> logs) {
        this.logs = logs;
    }

    @Override
    public int getCount() {
        return logs.size();
    }

    @Override
    public Object getItem(int position) {
        return logs.get(logs.size() - 1 - position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        position = logs.size() - position - 1;

        LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View logItemView = inflater.inflate(R.layout.log_item, parent, false);

        TextView dateView = (TextView) logItemView.findViewById(R.id.log_date);
        dateView.setText(logs.get(position).getDate());

        TextView descView = (TextView) logItemView.findViewById(R.id.log_desc);
        descView.setText(logs.get(position).getDesc());

        TextView stockView = (TextView) logItemView.findViewById(R.id.log_stock);
        stockView.setText(logs.get(position).getStock());

        TextView monetaryView = (TextView) logItemView.findViewById(R.id.log_money_change);
        monetaryView.setText(Double.toString(logs.get(position).getMonetaryChange()));

        return logItemView;
    }
}
