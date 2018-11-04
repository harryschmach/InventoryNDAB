package com.harry.inventoryndab;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
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
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.harry.inventoryndab.data.ProductCursorAdapter;

import static com.harry.inventoryndab.data.ProductContract.*;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final String LOG_TAG = MainActivity.class.getSimpleName();

    private static final int PRODUCT_LOADER = 0;

    ProductCursorAdapter mProductCursorAdapter;

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

        // Find the ListView which will be populated with the pet data
        ListView inventoryListView = findViewById(R.id.main_list_view);

        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = findViewById(R.id.empty_view);
        inventoryListView.setEmptyView(emptyView);

        // Set cursor loader to listview
        mProductCursorAdapter = new ProductCursorAdapter(this, null);
        inventoryListView.setAdapter(mProductCursorAdapter);

        // OnClickListener to Prompt EditMode
        inventoryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Log.v(LOG_TAG, "Clicked!");
                Intent intentEditorInEditMode = new Intent(
                        MainActivity.this,
                        EditorAddProductActivity.class
                );
                // Forge a mighty URI for the intent to carry
                Uri currentProductURI = ContentUris.withAppendedId(ProductEntry.CONTENT_URI, id);

                // Add the URI to the intent
                intentEditorInEditMode.setData(currentProductURI);

                //Launch into editor
                startActivity(intentEditorInEditMode);
            }
        });

        // Load stuff with loader
        getLoaderManager().initLoader(PRODUCT_LOADER, null, this);
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
                return true;
            case R.id.action_delete_all_entries:
                deleteAllProducts();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Make projection from table
        String[] projection = {
                ProductEntry._ID,
                ProductEntry.COLUMN_PRODUCT_NAME,
                ProductEntry.COLUMN_PRODUCT_PRICE,
                ProductEntry.COLUMN_PRODUCT_QUANTITY
        };
        return new CursorLoader(
                this,
                ProductEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Update Cursor with data
        mProductCursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mProductCursorAdapter.swapCursor(null);
    }

    /**
     * Perform the deletion of the products in the database.
     */
    private void deleteAllProducts() {
        // Need URI that corresponds to all entries
        Uri allProductsURI = ProductEntry.CONTENT_URI;
        // Get content resolver to do our dirty work
        int rowsDeleted = getContentResolver().delete(allProductsURI, null, null);
        Log.v(LOG_TAG, rowsDeleted + " ROWS DELETED");
        // Show a toast message depending on whether or not the delete was successful.
        if (rowsDeleted == 0) {
            // If no rows were deleted, then there was an error with the delete.
            Toast.makeText(this, "Items Not Deleted. Weird.", Toast.LENGTH_LONG).show();
        } else {
            // Otherwise, the delete was successful and we can display a toast.
            Toast.makeText(this, "All is Deleted", Toast.LENGTH_LONG).show();
        }
    }
}
