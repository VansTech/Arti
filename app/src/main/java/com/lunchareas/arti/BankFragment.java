package com.lunchareas.arti;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class BankFragment extends Fragment{

    double moneys;
    ArtiData data;
    Context context;

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
        ((ListView) view.findViewById(R.id.bank_log)).setAdapter(new LogAdapter(logs));
    }

    private void run() {
        List<String> stockStr = data.returnStocks();
        List<Stock> stocks = new ArrayList<>();

        data.addLog(new ArtiLog("5/30", "Sale x1", "AAPL", 10));

        for (String s : stockStr) {
            stocks.add(data.getStock(s));
        }

        for (Stock s : stocks) {
            if (s.getDiff() < 0) {
                data.buyStock(s.getTicker(), s.getPrice());
                moneys -= s.getPrice();

                data.addLog(new ArtiLog("5/30", "Purchase x1", s.getTicker(), s.getPrice()));
            }

            if (s.getDiff() > 0) {
                data.sellStock(s.getTicker());
                moneys += (s.getPrice() * s.numOwned);

                data.addLog(new ArtiLog("5/30", "Sale x" + s.numOwned, s.getTicker(), s.getPrice()*s.numOwned));
            }
        }

        commitMoneys();
    }

    private void commitMoneys() {
        context.getSharedPreferences("PREFERENCES", Context.MODE_PRIVATE).edit().putFloat("UserMoneys", Float.parseFloat(Double.toString(moneys))).apply();
    }
}
