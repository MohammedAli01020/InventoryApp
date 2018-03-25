package com.example.mohamed.inventoryapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.mohamed.inventoryapp.data.ProductsContract.ProductEntry;

public class ProductsDbHelper extends SQLiteOpenHelper {

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + ProductEntry.TABLE_NAME + "(" +
                    ProductEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    ProductEntry.COLUMN_IMAGE + " TEXT NOT NULL," +
                    ProductEntry.COLUMN_NAME + " TEXT NOT NULL," +
                    ProductEntry.COLUMN_QUANTITY + " INTEGER NOT NULL," +
                    ProductEntry.COLUMN_PRICE + " INTEGER NOT NULL," +
                    ProductEntry.COLUMN_SUPPLIER + " TEXT NOT NULL)";

    private static final String SQL_DROP_ENTRIES =
            "DROP TABLE IF EXISTS" + ProductEntry.TABLE_NAME;

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "products.db";

    public ProductsDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL(SQL_DROP_ENTRIES);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
