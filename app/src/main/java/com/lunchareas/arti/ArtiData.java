package com.lunchareas.arti;

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
            static final String COLUMN_NAME_NAME = "name";
        }

        private static final String SQL_CREATE_STOCK =
                "CREATE TABLE " + StockUtility.TABLE_NAME + " (" +
                        StockUtility._ID + " INTEGER PRIMARY KEY," +
                        StockUtility.COLUMN_NAME_NAME + " TEXT)";

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
            String index = cursor.getString(cursor.getColumnIndexOrThrow(DataUtility.IndexUtility._ID));
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
}
