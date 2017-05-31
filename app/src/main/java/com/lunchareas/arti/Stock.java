package com.lunchareas.arti;

/* Stock.java
 * v1.1.0
 * 2017-05-30
 *
 * Copyright (C) 2017  Vanshaj Singhania, David Zhang, Emil Tu
 * Full copyright information available in MainActivity.java
 */

import android.app.ProgressDialog;
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

    double totalPrice;
    int numOwned;

    private StockAdapter adapter;
    private Context context;

    public Stock(String ticker, Context context, boolean hold) {
        this.ticker = ticker;
        this.context = context;

        if (hold) {
            ProgressDialog dialog = new ProgressDialog(context);
            dialog.setTitle("Loading Data");
            dialog.setMessage("Shouldn't take too long...");
            dialog.setCancelable(false);
            dialog.setIndeterminate(true);
            dialog.show();

            try {
                StockAsync async = new StockAsync();
                async.execute();
                async.get();
            } catch (Exception e) {
                e.printStackTrace();
            }

            dialog.cancel();
        } else {
            new StockAsync().execute();
        }
    }

    public Stock(String ticker, int numOwned, double totalPrice) {
        this.ticker = ticker;
        this.numOwned = numOwned;
        this.totalPrice = totalPrice;
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
            String url_select = "http://9b49bbed.ngrok.io/stocks/get/";
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
