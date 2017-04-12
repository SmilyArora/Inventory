package com.example.smily.inventoryapp;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.smily.inventoryapp.data.ItemContract;

public class Details extends AppCompatActivity {
    private static final int FILE_SELECT_CODE = 2;
    boolean infoItemHasChanged = true;
    EditText name;
    EditText price;
    EditText count;
    EditText supplierName;
    EditText supplierEmail;
    EditText supplierPhone;
    Button asc;
    Button des;
    Uri currentItemUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edittor);


        Intent intent = getIntent();
        currentItemUri = intent.getData();
        Cursor cursor = getContentResolver().query(currentItemUri, null, null, null, null);
        cursor.moveToFirst();

        name = (EditText) findViewById(R.id.detail_name);
        price = (EditText) findViewById(R.id.detail_price);
        count = (EditText) findViewById(R.id.detail_amount);
        supplierName = (EditText) findViewById(R.id.seller_name);
        supplierEmail = (EditText) findViewById(R.id.seller_email);
        supplierPhone = (EditText) findViewById(R.id.seller_phone);
        asc = (Button) findViewById(R.id.detail_add);
        des = (Button) findViewById(R.id.detail_sub);
        findViewById(R.id.select_image).setVisibility(View.GONE);


        asc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int x = Integer.parseInt(count.getText().toString().trim());
                x++;
                count.setText(x + " ");
            }
        });
        des.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int x = Integer.parseInt(count.getText().toString().trim());
                if (x < 1) {
                    return;
                }
                x--;
                count.setText(x + " ");
            }
        });


        String cName = cursor.getString(cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_NAME));
        String cPrice = cursor.getString(cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_PRICE));
        int cAvail = cursor.getInt(cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_COUNT));
        String sName = cursor.getString(cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_SUPPLIER_NAME));
        String sEmail = cursor.getString(cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_SUPPLIER_EMAIL));
        String sPhone = cursor.getString(cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_SUPPLIER_PHONE));
        // Blob sImage = cursor.getBlob(cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_IMAGE));
        name.setText(cName);
        price.setText(cPrice);
        count.setText(cAvail + "");
        supplierName.setText(sName);
        supplierEmail.setText(sEmail);
        supplierPhone.setText(sPhone);

        // Convert byte array to bitmap and display the image
        ImageView img = (ImageView) findViewById(R.id.img_view);
        byte[] image = cursor.getBlob(cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_IMAGE));
        if (image != null && image.length > 0) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
            img.setImageBitmap(bitmap);
        }
    }

    @Override
    public void onBackPressed() {
        if (!infoItemHasChanged) {
            super.onBackPressed();
            return;
        }
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                };
        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Unsaved changes");
        builder.setPositiveButton("Discard", discardButtonClickListener);
        builder.setNegativeButton("keep editing", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_details, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.save:
                saveData();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.delete:
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setTitle("Delete entry");
                alert.setMessage("Are you sure you want to delete?");
                alert.setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                deleteItem();
                            }
                        });
                alert.setNegativeButton(
                        "No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
                alert.show();
                return true;
            case R.id.order:
                contactSupplier();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    void saveData() {

        ContentValues values = new ContentValues();
        values.put(ItemContract.ItemEntry.COLUMN_NAME, name.getText().toString());
        values.put(ItemContract.ItemEntry.COLUMN_PRICE, price.getText().toString());
        values.put(ItemContract.ItemEntry.COLUMN_COUNT, count.getText().toString());
        values.put(ItemContract.ItemEntry.COLUMN_SUPPLIER_EMAIL, supplierEmail.getText().toString());
        values.put(ItemContract.ItemEntry.COLUMN_SUPPLIER_NAME, supplierName.getText().toString());
        values.put(ItemContract.ItemEntry.COLUMN_SUPPLIER_PHONE, supplierPhone.getText().toString());


        getContentResolver().update(currentItemUri, values, null, null);
        finish();
    }

    void contactSupplier() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(Details.this);
        builder1.setMessage("Contact dealer via");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Phone",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + supplierPhone.getText().toString().trim()));
                        startActivity(intent);
                    }
                });

        builder1.setNegativeButton(
                "E-mail",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                                "mailto", supplierEmail.getText().toString().trim(), null));
                        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject");
                        emailIntent.putExtra(Intent.EXTRA_TEXT, "Body");
                        startActivity(Intent.createChooser(emailIntent, "Send email..."));
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    void deleteItem() {

        getContentResolver().delete(currentItemUri, null, null);

        finish();
    }


}

