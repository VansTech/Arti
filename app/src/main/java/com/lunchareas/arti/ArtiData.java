package com.lunchareas.arti;

/* ArtiData.java
 * v1.1.0
 * 2017-05-30
 *
 * Copyright (C) 2017  Vanshaj Singhania, David Zhang, Emil Tu
 * Full copyright information available in MainActivity.java
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import java.util.ArrayList;
import java.util.List;

public class ArtiData extends SQLiteOpenHelper {

    private static final String DB_NAME = "ArtiData.db";
    private static final int DB_VERSION = 1;

    public ArtiData(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DataUtility.StockUtility.SQL_CREATE);
        db.execSQL(DataUtility.LogUtility.SQL_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    private static class DataUtility {
        private static class LogUtility implements BaseColumns {
            static final String TABLE_NAME = "logs";
            static final String COLUMN_DATE = "date";
            static final String COLUMN_DESC = "desc";
            static final String COLUMN_STOCK = "stock";
            static final String COLUMN_MONETARY = "monetary_change";

            private static final String SQL_CREATE =
                    "CREATE TABLE " + TABLE_NAME + " (" +
                            _ID + " INTEGER PRIMARY KEY," +
                            COLUMN_DATE + " TEXT," +
                            COLUMN_DESC + " TEXT," +
                            COLUMN_STOCK + " TEXT," +
                            COLUMN_MONETARY + " FLOAT)";

            private static final String SQL_DELETE =
                    "DROP TABLE IF EXISTS " + TABLE_NAME;
        }

        private static class StockUtility implements BaseColumns {
            static final String TABLE_NAME = "stocks";
            static final String COLUMN_NAME = "name";
            static final String COLUMN_NUM_OWNED = "owned";
            static final String COLUMN_TOTAL_PR = "total_price_paid";

            private static final String SQL_CREATE =
                    "CREATE TABLE " + TABLE_NAME + " (" +
                            _ID + " INTEGER PRIMARY KEY," +
                            COLUMN_NAME + " TEXT," +
                            COLUMN_NUM_OWNED + " INTEGER," +
                            COLUMN_TOTAL_PR + " FLOAT)";

            private static final String SQL_DELETE =
                    "DROP TABLE IF EXISTS " + TABLE_NAME;
        }
    }

    public void addStock(String stock) {
        addStock(stock, 0, 0);
    }

    public void addStock(String stock, int num, double price) {
        if (stock.equals("") || returnStocks().contains(stock.toUpperCase())) return;

        ContentValues values = new ContentValues();
        values.put(DataUtility.StockUtility.COLUMN_NAME, stock.toUpperCase());
        values.put(DataUtility.StockUtility.COLUMN_NUM_OWNED, num);
        values.put(DataUtility.StockUtility.COLUMN_TOTAL_PR, price);

        getWritableDatabase().insert(DataUtility.StockUtility.TABLE_NAME, null, values);
    }

    public Stock getStock(String stock) {
        String[] projection = {DataUtility.StockUtility.COLUMN_NUM_OWNED, DataUtility.StockUtility.COLUMN_TOTAL_PR};
        String selection = DataUtility.StockUtility.COLUMN_NAME + " LIKE ?";

        String[] selectionArgs = {stock.toUpperCase()};

        Cursor cursor = getReadableDatabase().query(DataUtility.StockUtility.TABLE_NAME, projection, selection, selectionArgs, null, null, null);
        cursor.moveToFirst();

        int num = cursor.getInt(cursor.getColumnIndexOrThrow(DataUtility.StockUtility.COLUMN_NUM_OWNED));
        double price = cursor.getDouble(cursor.getColumnIndexOrThrow(DataUtility.StockUtility.COLUMN_TOTAL_PR));

        cursor.close();

        return new Stock(stock, num, price);
    }

    public void buyStock(String stock, double price) {
        buyStock(stock, 1, price);
    }

    public void buyStock(String stock, int num, double price) {
        Stock original = getStock(stock);

        ContentValues values = new ContentValues();
        values.put(DataUtility.StockUtility.COLUMN_NUM_OWNED, original.numOwned + num);
        values.put(DataUtility.StockUtility.COLUMN_TOTAL_PR, original.totalPrice + num*price);

        String selection = DataUtility.StockUtility.COLUMN_NAME + " LIKE ?";
        String[] selectionArgs = {stock};

        getReadableDatabase().update(DataUtility.StockUtility.TABLE_NAME, values, selection, selectionArgs);
    }

    public void sellStock(String stock) {
        ContentValues values = new ContentValues();
        values.put(DataUtility.StockUtility.COLUMN_NUM_OWNED, 0);
        values.put(DataUtility.StockUtility.COLUMN_TOTAL_PR, 0);

        String selection = DataUtility.StockUtility.COLUMN_NAME + " LIKE ?";
        String[] selectionArgs = {stock.toUpperCase()};

        getReadableDatabase().update(DataUtility.StockUtility.TABLE_NAME, values, selection, selectionArgs);
    }

    public List<String> returnStocks() {
        String[] projection = {DataUtility.StockUtility.COLUMN_NAME};

        Cursor cursor = getReadableDatabase().query(DataUtility.StockUtility.TABLE_NAME, projection, null, null, null, null, null);

        List<String> stocks = new ArrayList<>();
        while(cursor.moveToNext()) {
            String stock = cursor.getString(cursor.getColumnIndexOrThrow(DataUtility.StockUtility.COLUMN_NAME));
            stocks.add(stock);
        }
        cursor.close();

        return stocks;
    }

    public String removeStock(String stock) {
        if (stock.equals("") || !returnStocks().contains(stock.toUpperCase())) return stock;

        String selection = DataUtility.StockUtility.COLUMN_NAME + " LIKE ?";
        String[] selectionArgs = {stock};
        getWritableDatabase().delete(DataUtility.StockUtility.TABLE_NAME, selection, selectionArgs);

        return stock;
    }

    public List<ArtiLog> returnLogs() {
        String[] projection = {DataUtility.LogUtility.COLUMN_DATE, DataUtility.LogUtility.COLUMN_DESC, DataUtility.LogUtility.COLUMN_STOCK, DataUtility.LogUtility.COLUMN_MONETARY};

        Cursor cursor = getReadableDatabase().query(DataUtility.LogUtility.TABLE_NAME, projection, null, null, null, null, null);

        List<ArtiLog> logs = new ArrayList<>();
        while(cursor.moveToNext()) {
            String date = cursor.getString(cursor.getColumnIndexOrThrow(DataUtility.LogUtility.COLUMN_DATE));
            String desc = cursor.getString(cursor.getColumnIndexOrThrow(DataUtility.LogUtility.COLUMN_DESC));
            String stock = cursor.getString(cursor.getColumnIndexOrThrow(DataUtility.LogUtility.COLUMN_STOCK));
            double monetary = cursor.getDouble(cursor.getColumnIndexOrThrow(DataUtility.LogUtility.COLUMN_MONETARY));

            logs.add(new ArtiLog(date, desc, stock, monetary));
        }
        cursor.close();

        return logs;
    }

    public void addLog(ArtiLog log) {
        ContentValues values = new ContentValues();
        values.put(DataUtility.LogUtility.COLUMN_DATE, log.getDate());
        values.put(DataUtility.LogUtility.COLUMN_DESC, log.getDesc());
        values.put(DataUtility.LogUtility.COLUMN_STOCK, log.getStock());
        values.put(DataUtility.LogUtility.COLUMN_MONETARY, log.getMonetaryChange());

        getWritableDatabase().insert(DataUtility.LogUtility.TABLE_NAME, null, values);
    }
}