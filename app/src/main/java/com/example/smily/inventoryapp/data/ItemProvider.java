package com.example.smily.inventoryapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.example.smily.inventoryapp.data.ItemContract;
import com.example.smily.inventoryapp.data.ItemContract.ItemEntry;

public class ItemProvider extends ContentProvider {

    private static final String LOG_NAME = ItemProvider.class.getSimpleName();
    private static final int ORDERS = 100;
    private static final int ORDERS_ID = 101;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer. This is run the first time anything is called from this class.
    static {
        sUriMatcher.addURI(ItemContract.CONTENT_AUTHORITY,ItemContract.PATH_ORDERS,ORDERS);
        sUriMatcher.addURI(ItemContract.CONTENT_AUTHORITY,ItemContract.PATH_ORDERS + "/#", ORDERS_ID);
    }

    ItemDbHelper mDbHelper ;

    @Override
    public boolean onCreate() {
        mDbHelper = new ItemDbHelper(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortItem) {
        // Get readable database
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        Cursor cursor ;

        // Figure out if the URI matcher can match the URI to a specific code
        switch ( sUriMatcher.match(uri) ) {
            case ORDERS://100
                cursor= db.query(ItemEntry.TABLE_NAME,projection,selection,selectionArgs,
                        null,null,sortItem);
                break;
            case ORDERS_ID://101

                selection = ItemEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                cursor = db.query(ItemEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortItem);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        Log.e("Provider query", "count is:"+ cursor.getCount());
        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {//idfc
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ORDERS:
                return insertItem(uri, contentValues);
            default:
                throw new IllegalArgumentException("Invalid URI " + uri);
        }
    }

    private Uri insertItem(Uri uri, ContentValues values) {
        mDbHelper = new ItemDbHelper(getContext());
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        long id = db.insert(ItemEntry.TABLE_NAME, null, values);
        if(id == -1){
            Toast.makeText(getContext(), "Error in insertion", Toast.LENGTH_SHORT);
            return null;
        }
        //Callback on reaching
        getContext().getContentResolver().notifyChange(uri,null);

        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ORDERS:
                // Delete all rows that match the selection and selection args
                getContext().getContentResolver().notifyChange(uri,null);
                return database.delete(ItemEntry.TABLE_NAME, selection, selectionArgs);
            case ORDERS_ID:
                // Delete a single row given by the ID in the URI
                selection = ItemEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                getContext().getContentResolver().notifyChange(uri,null);
                return database.delete(ItemEntry.TABLE_NAME, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
    }
    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ORDERS:
                return updateItem(uri, contentValues, selection, selectionArgs);
            case ORDERS_ID:
                selection = ItemEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateItem(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateItem(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int x = db.update(ItemEntry.TABLE_NAME,values,selection,selectionArgs);
        getContext().getContentResolver().notifyChange(uri,null);
        return x;
    }


}
