package com.example.smily.inventoryapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.smily.inventoryapp.data.ItemContract;

import com.example.smily.inventoryapp.data.ItemContract.ItemEntry;

public class Catalog extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    ItemCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //listview and adapter
        ListView lv = (ListView) findViewById(R.id.list_view);

        View emptyView = findViewById(R.id.emptyview);
        lv.setEmptyView(emptyView);

        mCursorAdapter = new ItemCursorAdapter(this,null);


        lv.setAdapter(mCursorAdapter);



        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(Catalog.this, Details.class);
                Uri currentItemUri = ContentUris.withAppendedId(ItemContract.ItemEntry.ITEM_URI,id);
                intent.setData(currentItemUri);
                startActivity(intent);
            }
        });

        getSupportLoaderManager().initLoader(10, null, this);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_add));
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Catalog.this, Edittor.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.dummy_data:
                insertDummy();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.delete_all:

                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setTitle("Delete entry");
                alert.setMessage("Are you sure you want to delete?");
                alert.setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                getContentResolver().delete(ItemContract.ItemEntry.ITEM_URI,null, null);
                            }
                        });
                alert.setNegativeButton(
                        "No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                alert.show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, ItemContract.ItemEntry.ITEM_URI,
                null,null,null,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }


    void insertDummy(){
        ContentValues values = new ContentValues();
        values.put(ItemEntry.COLUMN_NAME, "Biscuits");
        values.put(ItemEntry.COLUMN_PRICE, 30);
        values.put(ItemEntry.COLUMN_COUNT, "223");
        values.put(ItemEntry.COLUMN_SUPPLIER_EMAIL, "smily.arora96@gmail.com");
        values.put(ItemEntry.COLUMN_SUPPLIER_NAME, "smily");
        values.put(ItemEntry.COLUMN_SUPPLIER_PHONE, "9041908421");
        //values.put(ItemEntry.COLUMN_IMAGE, null);

        getContentResolver().insert(ItemEntry.ITEM_URI, values);
    }
}
