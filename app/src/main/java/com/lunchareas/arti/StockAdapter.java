package com.lunchareas.arti;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class StockAdapter extends BaseAdapter {

    List<Stock> stocks;

    public StockAdapter(List<Stock> stocks) {
        this.stocks = stocks;
        for (Stock s : stocks) {
            System.out.println(s.getTicker());
            s.setAdapter(this);
        }
    }

    @Override
    public int getCount() {
        return stocks.size();
    }

    @Override
    public Object getItem(int position) {
        return stocks.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View stockItemView = inflater.inflate(R.layout.stock_item, parent, false);

        TextView tickerView = (TextView) stockItemView.findViewById(R.id.stock_ticker);
        tickerView.setText(stocks.get(position).getTicker());

        TextView priceView = (TextView) stockItemView.findViewById(R.id.stock_price);
        priceView.setText(String.format(Double.toString(stocks.get(position).getPrice())));

        TextView diffView = (TextView) stockItemView.findViewById(R.id.stock_diff);
        double diff = stocks.get(position).getDiff();
        System.out.println(diff);

        double absDiff = Math.abs(diff);
        System.out.println(absDiff*100);
        absDiff = Math.round(absDiff * 100) / 100d;

        if (diff < 0) {
            diffView.setTextColor(stockItemView.getResources().getColor(R.color.red));
        } else if (diff > 0) {
            diffView.setTextColor(stockItemView.getResources().getColor(R.color.green));
        }

        diffView.setText(String.format(Double.toString(absDiff)));

        return stockItemView;
    }
}
