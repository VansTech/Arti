package com.lunchareas.arti;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class StockFragment extends Fragment {

    private ArtiData data;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.stock_fragment, container, false);
        data = new ArtiData(this.getActivity());

        try {
            setUp(view);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return view;
    }

    private void setUp(View view) throws Exception {
        List<String> stockStrings = data.returnStocks();
        List<Stock> stocks = new ArrayList<>();

        System.out.println("PRINTING STOCKS");
        for (String s : stockStrings) {
            System.out.println(s);
            stocks.add(new Stock(s, view.getContext()));
        }

        ((ListView) view.findViewById(R.id.stock_list)).setAdapter(new StockAdapter(stocks));
    }}
