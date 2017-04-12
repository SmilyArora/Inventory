package com.example.smily.inventoryapp;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import com.example.smily.inventoryapp.data.ItemContract;

import static android.R.attr.data;
import static android.R.attr.imeSubtypeExtraValue;

public class Edittor extends AppCompatActivity {
    private static final int FILE_SELECT_CODE = 2;
    boolean infoItemHasChanged = true;
    private int REQUEST_IMAGE_CAPTURE = 1;
    byte[] image;
    EditText name;
    EditText price;
    EditText count;
    EditText supplierName;
    EditText supplierEmail;
    EditText supplierPhone;
    Button selectImage;
    Button asc;
    Button des;
    ImageView img;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK && data!=null) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");

            img.setImageBitmap(imageBitmap);

            // Convert Bitmap to byte array
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
            image = stream.toByteArray();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edittor);


        name = (EditText) findViewById(R.id.detail_name);
        price = (EditText) findViewById(R.id.detail_price);
        count = (EditText) findViewById(R.id.detail_amount);
        supplierName = (EditText) findViewById(R.id.seller_name);
        supplierEmail = (EditText) findViewById(R.id.seller_email);
        supplierPhone = (EditText) findViewById(R.id.seller_phone);
        asc = (Button) findViewById(R.id.detail_add);
        des = (Button) findViewById(R.id.detail_sub);
        img = (ImageView) findViewById(R.id.img_view);
        selectImage = (Button) findViewById(R.id.select_image);

        selectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            }
        });



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
                if(x<1){return;}
                x--;
                count.setText(x + " ");
            }
        });

    }

    @Override
    public void onBackPressed() {

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog();
    }

    private void showUnsavedChangesDialog() {


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Unsaved changes");
        builder.setPositiveButton("Discard", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
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
        getMenuInflater().inflate(R.menu.menu_editor, menu);
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
                deleteItem();
                return true;
            case R.id.order:
                contactSupplier();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    void saveData(){

        if (TextUtils.isEmpty(name.getText().toString()) || TextUtils.isEmpty(price.getText().toString()) ||
                TextUtils.isEmpty(supplierName.getText().toString()) || TextUtils.isEmpty(supplierEmail.getText().toString()) ||
                TextUtils.isEmpty(supplierPhone.getText().toString()) || !ItemContract.ItemEntry.isValidMobile(supplierPhone.getText().toString())
                || !ItemContract.ItemEntry.isValidEmail(supplierEmail.getText().toString()) || image.length < 1) {
            Toast.makeText(this, "Enter all the correct and valid details in editor", Toast.LENGTH_SHORT).show();
            return;}
        ContentValues values = new ContentValues();
        values.put(ItemContract.ItemEntry.COLUMN_NAME, name.getText().toString());
        values.put(ItemContract.ItemEntry.COLUMN_PRICE, price.getText().toString());
        int c = 0;
        String count = this.count.getText().toString().trim();
        if (!TextUtils.isEmpty(count)) {
            c = Integer.parseInt(count);
        }
        values.put(ItemContract.ItemEntry.COLUMN_COUNT, c);
        values.put(ItemContract.ItemEntry.COLUMN_SUPPLIER_EMAIL, supplierEmail.getText().toString());
        values.put(ItemContract.ItemEntry.COLUMN_SUPPLIER_NAME, supplierName.getText().toString());
        values.put(ItemContract.ItemEntry.COLUMN_SUPPLIER_PHONE, supplierPhone.getText().toString());
        values.put(ItemContract.ItemEntry.COLUMN_IMAGE, image);



        getContentResolver().insert(ItemContract.ItemEntry.ITEM_URI, values);
        finish();
    }

    void contactSupplier(){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(Edittor.this);
        builder1.setMessage("Contact dealer via");
        builder1.setCancelable(true);//back button work

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
                                "mailto",supplierEmail.getText().toString().trim(), null));
                        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject");
                        emailIntent.putExtra(Intent.EXTRA_TEXT, "Body");
                        startActivity(Intent.createChooser(emailIntent, "Send email..."));
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    void deleteItem(){
        finish();
    }


}
