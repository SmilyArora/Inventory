package com.example.smily.inventoryapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.smily.inventoryapp.data.ItemContract.ItemEntry;

public class ItemDbHelper extends SQLiteOpenHelper {

    public final static String DB_NAME = "inventory.db";
    public final static int DB_VERSION = 1;
    public final static String LOG_TAG = ItemDbHelper.class.getSimpleName();

    public ItemDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String table = "CREATE TABLE " +
                ItemEntry.TABLE_NAME + "(" +
                ItemEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                ItemEntry.COLUMN_NAME + " TEXT ," +
                ItemEntry.COLUMN_PRICE + " TEXT ," +
                ItemEntry.COLUMN_COUNT + " INTEGER DEFAULT 0," +
                ItemEntry.COLUMN_SUPPLIER_NAME + " TEXT," +
                ItemEntry.COLUMN_SUPPLIER_PHONE + " TEXT," +
                ItemEntry.COLUMN_SUPPLIER_EMAIL + " TEXT," +
                ItemEntry.COLUMN_IMAGE + " BLOB" +
                ");";

        db.execSQL(table);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
