package com.lunchareas.arti;

/* BankFragment.java
 * v1.1.0
 * 2017-05-30
 *
 * Copyright (C) 2017  Vanshaj Singhania, David Zhang, Emil Tu
 * Full copyright information available in MainActivity.java
 */

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BankFragment extends Fragment{

    double moneys;
    ArtiData data;
    Context context;

    LogAdapter adapter;
    TextView moneysView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        moneys = container.getContext().getSharedPreferences("PREFERENCES", Context.MODE_PRIVATE).getFloat("UserMoneys", 1000);

        View view = inflater.inflate(R.layout.bank_fragment, container, false);
        data = new ArtiData(this.getActivity());
        this.context = this.getActivity();

        try {
            setUpLog(view);
        } catch (Exception e) {
            e.printStackTrace();
        }

        moneysView = (TextView) view.findViewById(R.id.moneys);
        moneysView.setText("We have " + Double.toString(moneys) + " moneys");

        view.findViewById(R.id.runAI).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                run();
            }
        });

        return view;
    }

    private void setUpLog(View view) {
        List<ArtiLog> logs = data.returnLogs();
        adapter = new LogAdapter(logs);
        ((ListView) view.findViewById(R.id.bank_log)).setAdapter(adapter);
    }

    private void run() {
        List<String> stockStr = data.returnStocks();
        List<Stock> stocks = new ArrayList<>();
        List<Stock> todayInfo = new ArrayList<>();

        for (String s : stockStr) {
            stocks.add(data.getStock(s));
            todayInfo.add(new Stock(s, context, true));
        }

        for (int i = 0; i < stocks.size(); i++) {
            Stock localStock = stocks.get(i);
            Stock virtualStock = todayInfo.get(i);

            if (virtualStock.getDiff() < 0) {
                moneys -= virtualStock.getPrice();
                if (moneys < 0) {
                    moneys += virtualStock.getPrice();
                    continue;
                }
                data.buyStock(virtualStock.getTicker(), virtualStock.getPrice());

                Date rawDate = new Date();
                String date = new SimpleDateFormat("MM/dd").format(rawDate);

                if (date.startsWith("0")) date = date.substring(1);

                data.addLog(new ArtiLog(date, "Purchase x1", virtualStock.getTicker(), virtualStock.getPrice()));
            }

            if (virtualStock.getDiff() > 0 && localStock.totalPrice < virtualStock.getPrice() * localStock.numOwned) {
                data.sellStock(localStock.getTicker());
                moneys += (virtualStock.getPrice() * localStock.numOwned);

                Date rawDate = new Date();
                String date = new SimpleDateFormat("MM/dd").format(rawDate);

                if (date.startsWith("0")) date = date.substring(1);

                data.addLog(new ArtiLog(date, "Sale x" + localStock.numOwned, localStock.getTicker(), virtualStock.getPrice()*localStock.numOwned));
            }
        }

        commitMoneys();
        adapter.notifyDataSetChanged();
    }

    private void commitMoneys() {
        moneys = Math.round(moneys * 100) / 100;
        context.getSharedPreferences("PREFERENCES", Context.MODE_PRIVATE).edit().putFloat("UserMoneys", Float.parseFloat(Double.toString(moneys))).apply();
        moneysView.setText("We have " + Double.toString(moneys) + " moneys");
    }
}
