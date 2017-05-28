package com.lunchareas.arti;

import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class Stock {

    private String ticker;
    private double price, diff;

    private StockAdapter adapter;
    private Context context;

    public Stock(String ticker, Context context) {
        this.ticker = ticker;
        this.context = context;

        new StockAsync().execute();
    }

    public void setAdapter(StockAdapter adapter) {
        this.adapter = adapter;
    }

    public double getDiff() {
        return diff;
    }

    public double getPrice() {
        return price;
    }

    public String getTicker() {
        return ticker;
    }

    private class StockAsync extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            String url_select = "http://192.168.1.128:5000/stocks/get/";
            BufferedReader reader;

            try {
                URLConnection connection = new URL(url_select + ticker).openConnection();

                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                StringBuilder builder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }

                JSONObject object;

                try {
                    object = new JSONObject(builder.toString());
                    object = object.getJSONObject(ticker);

                    price = object.getDouble("today_price");
                    diff = price - object.getDouble("yest_price");
                } catch (JSONException e) {
                    new ArtiData(context).removeStock(ticker);
                    adapter.stocks.remove(Stock.this);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
        }
    }

}
