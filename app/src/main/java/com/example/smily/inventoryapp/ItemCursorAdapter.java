package com.example.smily.inventoryapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.smily.inventoryapp.R;
import com.example.smily.inventoryapp.data.ItemContract;

/**
 * Created by shubham on 4/4/2017.
 */

public class ItemCursorAdapter extends CursorAdapter {


    public ItemCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item,parent,false);//R is error
    }


    @Override
    public void bindView(View view, final Context context, Cursor cursor) {

        TextView tv1 = (TextView) view.findViewById(R.id.name);
        TextView tv2 = (TextView) view.findViewById(R.id.price);
        TextView tv3 = (TextView) view.findViewById(R.id.count);

        String name = cursor.getString(cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_NAME));
        String price = cursor.getString(cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_PRICE));
        String avail =  cursor.getString(cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_COUNT));
        final int productId = cursor.getInt(cursor.getColumnIndex(ItemContract.ItemEntry._ID));
        final int count = cursor.getInt(cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_COUNT));


        Button btnBuy = (Button) view.findViewById(R.id.buy);
        btnBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri itemUri = ContentUris.withAppendedId(ItemContract.ItemEntry.ITEM_URI, productId);
                buyProduct(context, itemUri, count);
            }
        });

        tv1.setText(name);
        tv2.setText("Price: Rs " + price + "");
        tv3.setText("Available units:" + avail);
    }
    // Decrease product count by 1
    private void buyProduct(Context context, Uri itemUri, int currentCount) {
        int newCount = (currentCount >= 1) ? currentCount - 1 : 0;
        ContentValues values = new ContentValues();
        values.put(ItemContract.ItemEntry.COLUMN_COUNT, newCount );
        context.getContentResolver().update(itemUri, values, null, null);//update amount
    }
}