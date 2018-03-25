package com.example.mohamed.inventoryapp;

import android.annotation.SuppressLint;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


import com.example.mohamed.inventoryapp.data.ProductsContract;
import com.example.mohamed.inventoryapp.data.ProductsDbHelper;


public class ProductLoader extends AsyncTaskLoader<Cursor> {
    @SuppressLint("StaticFieldLeak")
    private Context mContext;
    ProductLoader(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public Cursor loadInBackground() {
        ProductsDbHelper dbHelper = new ProductsDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        return db.query(
                ProductsContract.ProductEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );
    }
}
