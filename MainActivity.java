package com.example.mohamed.inventoryapp;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mohamed.inventoryapp.data.ProductsContract.ProductEntry;
import com.example.mohamed.inventoryapp.data.ProductsDbHelper;


public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int PRODUCT_LOADER = 0;
    private ProductsDbHelper mDbHelper;
    private ProductAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDbHelper = new ProductsDbHelper(this);
        TextView mMessage = findViewById(R.id.msg);

        ListView list = findViewById(R.id.products_list);
        list.setEmptyView(mMessage);

        mAdapter = new ProductAdapter(this, null);
        list.setAdapter(mAdapter);

        getLoaderManager().initLoader(PRODUCT_LOADER, null, this);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, DetailsActivity.class);
                startActivity(i);
            }
        });

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(i);
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(ProductEntry._ID));
                Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
                intent.putExtra("id", id);
                startActivity(intent);
            }
        });
    }

    public void updateQuantity(String id, int quantity) {
        ProductsDbHelper dbHelper = new ProductsDbHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        if (quantity == 0) {
            Toast.makeText(this, "not available!", Toast.LENGTH_SHORT).show();
            return;
        }

        quantity--;

        ContentValues values = new ContentValues();
        values.put(ProductEntry.COLUMN_QUANTITY, quantity);

        String selection = ProductEntry._ID + " = ?";
        String[] selectionArgs = {String.valueOf(id)};

        db.update(
                ProductEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs);

        getLoaderManager().restartLoader(PRODUCT_LOADER, null, this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.remove_all) {
            removeAllProducts();
            getLoaderManager().restartLoader(PRODUCT_LOADER, null, this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void removeAllProducts() {

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.delete(ProductEntry.TABLE_NAME, null, null);
    }

    @Override
    protected void onDestroy() {
        if (mDbHelper != null) {
            mDbHelper.close();
        }
        super.onDestroy();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new ProductLoader(this);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }
}
