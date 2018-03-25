package com.example.mohamed.inventoryapp.data;

import android.provider.BaseColumns;

public final class ProductsContract {

    private ProductsContract() {
    }

    public static class ProductEntry implements BaseColumns {

        public static final String _ID = BaseColumns._ID;

        public static final String TABLE_NAME = "product";
        public static final String COLUMN_IMAGE = "image";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_QUANTITY = "quantity";
        public static final String COLUMN_PRICE = "price";
        public static final String COLUMN_SUPPLIER = "supplier";

    }

}
