package com.harry.inventoryndab.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

public class ProductProvider extends ContentProvider {

    /** Tag for the log messages */
    public static final String LOG_TAG = ProductProvider.class.getSimpleName();

    // Initialize global dBHelper
    private InventoryDbHelper mDBHelper;

    /** URI matcher code for the content URI for the products table */
    private static final int PRODUCTS = 100;

    /** URI matcher code for the content URI for a single product in the table */
    private static final int PRODUCTS_ID = 101;

    /**
     * UriMatcher object to match a content URI to a corresponding code.
     * The input passed into the constructor represents the code to return for the root URI.
     * It's common to use NO_MATCH as the input for this case.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer. This is run the first time anything is called from this class.
    static {
        // URI for whole table
        sUriMatcher.addURI(ProductContract.CONTENT_AUTHORITY, ProductContract.PATH_PRODUCTS, PRODUCTS);
        // URI for single product entry
        sUriMatcher.addURI(ProductContract.CONTENT_AUTHORITY, ProductContract.PATH_PRODUCT_SINGULAR, PRODUCTS_ID);
    }

    /**
     * Initialize the provider and the database helper object.
     */
    @Override
    public boolean onCreate() {
        // Create database helper
        mDBHelper = new InventoryDbHelper(getContext());
        // Make sure the variable is a global variable, so it can be referenced from other
        // ContentProvider methods.
        return true;
    }

    /**
     * Perform the query for the given URI. Use the given projection, selection, selection arguments, and sort order.
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // need the database
        SQLiteDatabase database = mDBHelper.getReadableDatabase();
        // need cursor to read database
        Cursor cursor;

        int match = sUriMatcher.match(uri);
        switch (match){
            case PRODUCTS:
                // query whole table
                cursor = database.query(ProductContract.ProductEntry.TABLE_NAME, projection, selection,
                        selectionArgs, null, null, sortOrder);
                break;
            case PRODUCTS_ID:
                // query just 1
                selection = ProductContract.ProductEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(ProductContract.ProductEntry.TABLE_NAME, projection, selection,
                        selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        return cursor;
    }

    /**
     * Insert new data into the provider with the given ContentValues.
     */
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return insertProduct(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    /**
     * Insert a product into the database with the given content values. Return the new content URI
     * for that specific row in the database.
     */
    private Uri insertProduct(Uri uri, ContentValues values) {
        // Check for null product name
        String name = values.getAsString(ProductContract.ProductEntry.COLUMN_PRODUCT_NAME);
        if (name == null) {
            throw new IllegalArgumentException("Product requires a name");
        }
        // Check for null supplier
        String supplierName = values.getAsString(ProductContract.ProductEntry.COLUMN_SUPPLIER_NAME);
        if (supplierName == null) {
            throw new IllegalArgumentException("Supplier needs a name");
        }
        // check for positive quantity
        Long quant = values.getAsLong(ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY);
        if (!(quant > 0)) {
            throw new IllegalArgumentException("Quant needs to be positive");
        }
        // check for positive price
        Double price = values.getAsDouble(ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE);
        if (!(price > 0)) {
            throw new IllegalArgumentException("Price needs to be positive");
        }

        // Get db
        SQLiteDatabase db = mDBHelper.getWritableDatabase();

        // get the id of the row of new data inserted
        long id = db.insert(ProductContract.ProductEntry.TABLE_NAME, null, values);

        // Check for bad insertion
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }
        // Once we know the ID of the new row in the table,
        // return the new URI with the ID appended to the end of it
        return ContentUris.withAppendedId(uri, id);
    }

    /**
     * Updates the data at the given selection and selection arguments, with the new ContentValues.
     */
    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        return 0;
    }

    /**
     * Delete the data at the given selection and selection arguments.
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    /**
     * Returns the MIME type of data for the content URI.
     */
    @Override
    public String getType(Uri uri) {
        return null;
    }
}
