package com.lunchareas.arti;

public class ArtiLog {

    private String date, desc, stock;
    private double monetaryChange;

    public ArtiLog(String date, String desc, String stock, double monetaryChange) {
        this.date = date;
        this.desc = desc;
        this.stock = stock;
        this.monetaryChange = monetaryChange;
    }

    public String getDate() {
        return date;
    }

    public String getDesc() {
        return desc;
    }

    public String getStock() {
        return stock;
    }

    public double getMonetaryChange() {
        return monetaryChange;
    }
}
