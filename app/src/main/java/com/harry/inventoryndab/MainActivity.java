package com.harry.inventoryndab;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.harry.inventoryndab.data.InventoryDbHelper;
import com.harry.inventoryndab.data.ProductCursorAdapter;

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

        // Find the ListView which will be populated with the pet data
        ListView inventoryListView = findViewById(R.id.main_list_view);

        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = findViewById(R.id.empty_view);
        inventoryListView.setEmptyView(emptyView);
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

        // Perform a query on the product table using the Content URI
        Cursor cursor = getContentResolver().query(ProductEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
        // get List to display in
        ListView mainLV = findViewById(R.id.main_list_view);
        // setup adapter
        ProductCursorAdapter adapter = new ProductCursorAdapter(this, cursor);
        // attach adapter
        mainLV.setAdapter(adapter);

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
