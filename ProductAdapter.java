package com.example.mohamed.inventoryapp;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mohamed.inventoryapp.data.ProductsContract.ProductEntry;
import com.squareup.picasso.Picasso;


import java.io.InputStream;
import java.text.NumberFormat;

class ProductAdapter extends CursorAdapter {

    private final MainActivity mContext;

    ProductAdapter(MainActivity context, Cursor c) {
        super(context, c, 0);
        this.mContext = context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        View v = LayoutInflater.from(context).inflate(R.layout.list_item, viewGroup, false);

        ViewHolder holder = new ViewHolder(v);
        v.setTag(holder);
        return v;
    }

    static class ViewHolder {
        TextView nameTextView;
        TextView quantityTextView;
        TextView priceTextView;
        TextView supplierTextView;
        ImageView imageView;
        Button sale;

        ViewHolder (View itemView) {
            nameTextView = itemView.findViewById(R.id.name);
            quantityTextView = itemView.findViewById(R.id.quantity);
            priceTextView = itemView.findViewById(R.id.price);
            supplierTextView = itemView.findViewById(R.id.supplier);
            imageView = itemView.findViewById(R.id.image);
            sale = itemView.findViewById(R.id.sale);
        }
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {

        ViewHolder holder = (ViewHolder) view.getTag();

        String name = cursor.getString(cursor.getColumnIndexOrThrow(ProductEntry.COLUMN_NAME));
        final int quantity = cursor.getInt(cursor.getColumnIndexOrThrow(ProductEntry.COLUMN_QUANTITY));
        int price = cursor.getInt(cursor.getColumnIndexOrThrow(ProductEntry.COLUMN_PRICE));
        String supplier = cursor.getString(cursor.getColumnIndexOrThrow(ProductEntry.COLUMN_SUPPLIER));
        String imageUri = cursor.getString(cursor.getColumnIndexOrThrow(ProductEntry.COLUMN_IMAGE));
        final int id = cursor.getInt(cursor.getColumnIndexOrThrow(ProductEntry._ID));

        holder.nameTextView.setText(name);
        holder.quantityTextView.setText("quantity: " + quantity);
        holder.priceTextView.setText("price: " + NumberFormat.getCurrencyInstance().format(price));
        holder.supplierTextView.setText("supplier: " + supplier);
        Picasso.with(context).load(Uri.parse(imageUri)).into(holder.imageView);

        holder.sale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mContext.updateQuantity(String.valueOf(id), quantity);
            }
        });
    }
}
