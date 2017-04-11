package com.example.smily.inventoryapp.data;

import android.net.Uri;
import android.provider.BaseColumns;

import java.sql.Blob;

import static android.text.style.TtsSpan.GENDER_FEMALE;
import static android.text.style.TtsSpan.GENDER_MALE;


public class ItemContract {

    public static final String CONTENT_AUTHORITY = "com.example.smily.inventoryapp";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_ORDERS = "orders";


    public static final class ItemEntry implements BaseColumns {

        public static final Uri ITEM_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_ORDERS);
        public static final String TABLE_NAME = "Items";

        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_NAME = "Name";
        public static final String COLUMN_PRICE = "Price";
        public static final String COLUMN_COUNT = "Count";
        public static final String COLUMN_SUPPLIER_NAME = "supplier_name";
        public static final String COLUMN_SUPPLIER_PHONE = "supplier_phone";
        public static final String COLUMN_SUPPLIER_EMAIL = "supplier_email";
        public static final String COLUMN_IMAGE = "Image";
        public static boolean isValidEmail(String email){
            return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
        }

        public static boolean isValidMobile(String phone) {
            return android.util.Patterns.PHONE.matcher(phone).matches();
        }
    }


}
