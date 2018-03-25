package com.example.mohamed.inventoryapp;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mohamed.inventoryapp.data.ProductsContract.ProductEntry;
import com.example.mohamed.inventoryapp.data.ProductsDbHelper;

public class DetailsActivity extends AppCompatActivity {

    private final int PICK_IMAGE_REQUEST = 1;
    private int mQuantity;
    private ProductsDbHelper mDbHelper;
    private EditText mName;
    private TextView mQuantityTextView;
    private EditText mPrice;
    private EditText mSupplier;
    private Uri uri;
    private String mImageUri;
    private int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inserting);

        mName = findViewById(R.id.et_name);
        mQuantityTextView = findViewById(R.id.tv_quantity);
        mPrice = findViewById(R.id.et_price);
        mSupplier = findViewById(R.id.et_supplier);

        mDbHelper = new ProductsDbHelper(this);

        Intent i = getIntent();
        id = i.getIntExtra("id", -1);

        if (id != -1) {
            updateUi(String.valueOf(id));
        }

    }

    private void updateUi(String id) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String selection = ProductEntry._ID + " = ?";
        String[] selectionArgs = {id};

        Cursor cursor = db.query(
                ProductEntry.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        while (cursor.moveToNext()) {

            mName.setText(cursor.getString(cursor.getColumnIndexOrThrow(ProductEntry.COLUMN_NAME)));
            mQuantityTextView.setText(String.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow(ProductEntry.COLUMN_QUANTITY))));
            mPrice.setText(String.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow(ProductEntry.COLUMN_PRICE))));
            mSupplier.setText(cursor.getString(cursor.getColumnIndexOrThrow(ProductEntry.COLUMN_SUPPLIER)));
            mImageUri = cursor.getString(cursor.getColumnIndexOrThrow(ProductEntry.COLUMN_IMAGE));
        }

        cursor.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.insert_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.insert_product) {
            insertProduct();
            return true;
        }

        if (item.getItemId() == R.id.delete_product) {
            if (id == -1) {
                Toast.makeText(this, "you must add product", Toast.LENGTH_SHORT).show();
                return true;
            }

            deleteCurrentProduct();
            return true;
        }

        if (item.getItemId() == R.id.order_product) {
            if (id != -1) {
                Toast.makeText(this, "you must add product", Toast.LENGTH_SHORT).show();
                orderCurrentProduct();
            }

            id = -1;
            return true;

        }

        if (item.getItemId() == R.id.update_product) {
            if (id == -1) {
                Toast.makeText(this, "you must add product", Toast.LENGTH_SHORT).show();
                return true;
            }

            updateCurrentProduct();
            id = -1;
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void insertProduct() {

        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        if (mName.getText().length() == 0
                || mQuantityTextView.getText().length() == 0
                || mPrice.getText().length() == 0
                || mSupplier.getText().length() == 0
                || uri == null) {
            Toast.makeText(this, "Not allowed empty entry!", Toast.LENGTH_SHORT).show();
            return;
        }

        ContentValues values = new ContentValues();

        values.put(ProductEntry.COLUMN_NAME, mName.getText().toString());
        values.put(ProductEntry.COLUMN_QUANTITY, mQuantityTextView.getText().toString());
        values.put(ProductEntry.COLUMN_PRICE, mPrice.getText().toString());
        values.put(ProductEntry.COLUMN_SUPPLIER, mSupplier.getText().toString());
        values.put(ProductEntry.COLUMN_IMAGE, uri.toString());

        db.insert(ProductEntry.TABLE_NAME, null, values);
        clearText();
        Toast.makeText(getApplicationContext(), "product added successfully", Toast.LENGTH_SHORT).show();
    }

    private void updateCurrentProduct() {

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        String selection = ProductEntry._ID + " = ?";
        String[] selectionArgs = {String.valueOf(id)};

        if (mName.getText().length() == 0
                || mQuantityTextView.getText().length() == 0
                || mPrice.getText().length() == 0
                || mSupplier.getText().length() == 0
                || mImageUri == null) {
            Toast.makeText(this, "Not allowed empty entry!", Toast.LENGTH_SHORT).show();
            return;
        }

        ContentValues values = new ContentValues();

        values.put(ProductEntry.COLUMN_NAME, mName.getText().toString());
        values.put(ProductEntry.COLUMN_QUANTITY, mQuantityTextView.getText().toString());
        values.put(ProductEntry.COLUMN_PRICE, mPrice.getText().toString());
        values.put(ProductEntry.COLUMN_SUPPLIER, mSupplier.getText().toString());
        values.put(ProductEntry.COLUMN_IMAGE, mImageUri);

        db.update(
                ProductEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs);

        clearText();
        Toast.makeText(getApplicationContext(), "product updated successfully", Toast.LENGTH_SHORT).show();
    }

    private void orderCurrentProduct() {

        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String selection = ProductEntry._ID + " = ?";
        String[] selectionArgs = {String.valueOf(id)};

        Cursor cursor = db.query(
                ProductEntry.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        String productName = "";

        while (cursor.moveToNext()) {
            productName = (cursor.getString(cursor.getColumnIndexOrThrow(ProductEntry.COLUMN_NAME)));
        }

        cursor.close();
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_SUBJECT, "order product from supplier");
        intent.putExtra(Intent.EXTRA_TEXT, "We need the product: " + productName);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }

        clearText();
    }

    private void deleteCurrentProduct() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("Are you sure ?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SQLiteDatabase db = mDbHelper.getWritableDatabase();

                        String selection = ProductEntry._ID + " = ?";
                        String[] selectionArgs = {String.valueOf(id)};
                        db.delete(ProductEntry.TABLE_NAME, selection, selectionArgs);
                        clearText();
                        id = -1;
                        Toast.makeText(getApplicationContext(), "product deleted successfully", Toast.LENGTH_SHORT).show();
                        returnBackToMain();
                    }
                })

                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // no thing
                    }
                }).create().show();

    }

    private void clearText() {
        mName.getText().clear();
        mQuantityTextView.setText("0");
        mPrice.getText().clear();
        mSupplier.getText().clear();
        mQuantity = 0;
        uri = null;
        mImageUri = null;
    }

    private void display(int number) {
        mQuantityTextView.setText(String.valueOf(number));
    }

    public void increment(View view) {
        mQuantity = mQuantity + 1;
        display(mQuantity);
    }

    public void decrement(View view) {
        if (mQuantity == 0) {
            return;
        }
        mQuantity = mQuantity - 1;
        display(mQuantity);
    }

    public void getImage(View view) {
        Intent intent;

        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
        } else {
            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
        }

        checkWriteToExternalPerms();

        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uri = data.getData();
        }
    }

    private void checkWriteToExternalPerms() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }
        }
    }

    private void returnBackToMain() {
        Intent intent = new Intent(DetailsActivity.this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        if (mDbHelper != null) {
            mDbHelper.close();
        }
        super.onDestroy();
    }
}
