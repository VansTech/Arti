package com.lunchareas.arti;

/* ArtiData.java
 * v1.0.0
 * 2017-05-28
 *
 * Copyright (C) 2017  Vanshaj Singhania, David Zhang
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
        db.execSQL(DataUtility.SQL_CREATE_INDEX);
        db.execSQL(DataUtility.SQL_CREATE_STOCK);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    private static class DataUtility {

        // Index Table Things
        private static class IndexUtility implements BaseColumns {
            static final String TABLE_NAME = "indices";
            static final String COLUMN_NAME_NAME = "name";
        }

        private static final String SQL_CREATE_INDEX =
                "CREATE TABLE " + IndexUtility.TABLE_NAME + " (" +
                        IndexUtility._ID + " INTEGER PRIMARY KEY," +
                        IndexUtility.COLUMN_NAME_NAME + " TEXT)";

        private static final String SQL_DELETE_INDEX =
                "DROP TABLE IF EXISTS " + IndexUtility.TABLE_NAME;

        // Stock Table Things
        private static class StockUtility implements BaseColumns {
            static final String TABLE_NAME = "stocks";
            static final String COLUMN_NAME = "name";
            static final String COLUMN_NUM_OWNED = "owned";
            static final String COLUMN_TOTAL_PR = "total_price_paid";
        }

        private static final String SQL_CREATE_STOCK =
                "CREATE TABLE " + StockUtility.TABLE_NAME + " (" +
                        StockUtility._ID + " INTEGER PRIMARY KEY," +
                        StockUtility.COLUMN_NAME + " TEXT, " +
                        StockUtility.COLUMN_NUM_OWNED + " INTEGER, " +
                        StockUtility.COLUMN_TOTAL_PR + " FLOAT)";

        private static final String SQL_DELETE_STOCK =
                "DROP TABLE IF EXISTS " + StockUtility.TABLE_NAME;
    }

    public void addIndex(String index) {
        ContentValues values = new ContentValues();
        values.put(DataUtility.IndexUtility.COLUMN_NAME_NAME, index);

        getWritableDatabase().insert(DataUtility.IndexUtility.TABLE_NAME, null, values);
    }

    public List<String> returnIndices() {
        String[] projection = {DataUtility.IndexUtility.COLUMN_NAME_NAME};

        Cursor cursor = getReadableDatabase().query(DataUtility.IndexUtility.TABLE_NAME, projection, null, null, null, null, null);

        List<String> indices = new ArrayList<>();
        while(cursor.moveToNext()) {
            String index = cursor.getString(cursor.getColumnIndexOrThrow(DataUtility.IndexUtility.COLUMN_NAME_NAME));
            indices.add(index);
        }
        cursor.close();

        return indices;
    }

    public String removeIndex(String index) {
        String selection = DataUtility.IndexUtility.COLUMN_NAME_NAME + " LIKE ?";
        String[] selectionArgs = {index};
        getWritableDatabase().delete(DataUtility.IndexUtility.TABLE_NAME, selection, selectionArgs);

        return index;
    }

    public void addStock(String stock) {
        addStock(stock, 0, 0);
    }

    public void addStock(String stock, int num, double price) {
        if (stock.equals("") || returnStocks().contains(stock.toUpperCase())) return;

        ContentValues values = new ContentValues();
        values.put(DataUtility.StockUtility.COLUMN_NAME, stock);
        values.put(DataUtility.StockUtility.COLUMN_NUM_OWNED, num);
        values.put(DataUtility.StockUtility.COLUMN_TOTAL_PR, price);

        getWritableDatabase().insert(DataUtility.StockUtility.TABLE_NAME, null, values);
    }

    public Stock getStock(String stock) {
        String[] projection = {DataUtility.StockUtility.COLUMN_NAME, DataUtility.StockUtility.COLUMN_NUM_OWNED, DataUtility.StockUtility.COLUMN_TOTAL_PR};
        String selection = DataUtility.StockUtility.COLUMN_NAME + " LIKE ?";
        String[] selectionArgs = {stock};

        Cursor cursor = getReadableDatabase().query(DataUtility.StockUtility.TABLE_NAME, projection, selection, selectionArgs, null, null, null);

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
        String[] selectionArgs = {stock};

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
}
