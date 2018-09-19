package com.harry.inventoryndab;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.harry.inventoryndab.data.InventoryDbHelper;

import java.net.URI;
import java.security.Provider;

import static com.harry.inventoryndab.data.ProductContract.*;

public class MainActivity extends AppCompatActivity {
    /** Database helper that will provide us access to the database */
    private InventoryDbHelper mDbHelper;
    public static final String LOG_TAG = MainActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EditorAddProductActivity.class);
                startActivity(intent);
            }
        });
        // To access our database, we instantiate our subclass of SQLiteOpenHelper
        // and pass the context, which is the current activity.
        mDbHelper = new InventoryDbHelper(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        displayDatabaseInfo();
    }

    /**
     * Helper method to display information in the onscreen TextView about the state of
     * the database.
     */
    private void displayDatabaseInfo() {
        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                ProductEntry._ID,
                ProductEntry.COLUMN_PRODUCT_NAME,
                ProductEntry.COLUMN_PRODUCT_PRICE,
                ProductEntry.COLUMN_PRODUCT_QUANTITY,
                ProductEntry.COLUMN_SUPPLIER_NAME,
                ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER
        };

        // Perform a query on the prodcut table using the Content URI
        Cursor cursor = getContentResolver().query(ProductEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);

        TextView displayView = findViewById(R.id.sql_table_tv);

        try {
            // Create a header in the Text View that looks like this:
            //
            // The products table contains <number of rows in Cursor> products.
            // _id - name - price - quantity - supplierName - supplierPhone
            //
            // In the while loop below, iterate through the rows of the cursor and display
            // the information from each column in this order.
            displayView.setText("The product table contains " + cursor.getCount() + " products.\n\n");
            displayView.append(ProductEntry._ID + " - " +
                    ProductEntry.COLUMN_PRODUCT_NAME + " - " +
                    ProductEntry.COLUMN_PRODUCT_PRICE + " - " +
                    ProductEntry.COLUMN_PRODUCT_QUANTITY + " - " +
                    ProductEntry.COLUMN_SUPPLIER_NAME + " - " +
                    ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER + "\n");

            // Figure out the index of each column
            int idColumnIndex = cursor.getColumnIndex(ProductEntry._ID);
            int nameColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME);
            int priceColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY);
            int supplierNameColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_SUPPLIER_NAME);
            int supplierPhoneColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER);

            // Iterate through all the returned rows in the cursor
            while (cursor.moveToNext()) {
                // Use that index to extract the String or Int value of the word
                // at the current row the cursor is on.
                int currentID = cursor.getInt(idColumnIndex);
                String currentName = cursor.getString(nameColumnIndex);
                int currentPrice = cursor.getInt(priceColumnIndex);
                int currentQuantity = cursor.getInt(quantityColumnIndex);
                String currentSupplierName = cursor.getString(supplierNameColumnIndex);
                String currentSupplierPhone = cursor.getString(supplierPhoneColumnIndex);

                // Display the values from each column of the current row in the cursor in the TextView
                displayView.append(("\n" + currentID + " - " +
                        currentName + " - " +
                        currentPrice + " - " +
                        currentQuantity + " - " +
                        currentSupplierName + " - " +
                        currentSupplierPhone
                ));
            }
        } finally {
            // Always close the cursor when you're done reading from it. This releases all its
            // resources and makes it invalid.

            if (cursor != null) {
                cursor.close();
            }
        }
    }

    /**
     * Helper method to insert hardcoded product data into the database. For testing quick insertion
     * only.
     */
    private void insertDummyProduct() {
        // Create dummy book to insert for testing
        ContentValues values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "The Great Gatzuby");
        values.put(ProductEntry.COLUMN_PRODUCT_PRICE, 599);
        values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, 2);
        values.put(ProductEntry.COLUMN_SUPPLIER_NAME, "TatteredPages");
        values.put(ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER, "8583218970");

        // Insert a new row for The Great Gatzuby in the database, returning the ID of that new row.
        // The first argument for db.insert() is the table name.
        // The second argument provides the name of a column in which the framework
        // can insert NULL in the event that the ContentValues is empty (if
        // this is set to "null", then the framework will not insert a row when
        // there are no values).

        // The third argument is the ContentValues object containing the info for the data inserted.

        // Calling the provider to do the insertion
        Uri returnedUri = getContentResolver().insert(ProductEntry.CONTENT_URI, values);

        Log.v(LOG_TAG, values.toString());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                insertDummyProduct();
                displayDatabaseInfo();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
