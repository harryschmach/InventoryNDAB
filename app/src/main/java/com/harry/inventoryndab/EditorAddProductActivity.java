package com.harry.inventoryndab;

import android.annotation.SuppressLint;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import javax.xml.datatype.Duration;

import static com.harry.inventoryndab.data.ProductContract.*;

public class EditorAddProductActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor>{

    /**
     * Instantiate variables of edit text fields
     */
    EditText mProductName;
    EditText mProductPrice;
    EditText mProductQuantity;
    EditText mSupplierName;
    EditText mSupplierPhone;

    // Get the loader
    private static final int EXISTING_LOADER = 0;
    /** Content URI for the existing product (null if it's a new product) */
    private Uri mCurrentProductUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor_add_product);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Get the intent
        Intent launcherIntent = getIntent();
        mCurrentProductUri = launcherIntent.getData();

        // Determine if we have found ourselves here with a new product or editing an old one
        if (mCurrentProductUri == null){
            // new product!
            setTitle(getString(R.string.editor_activity_new_product));
        }else {
            // Edit Product!
            setTitle(getString(R.string.editor_activitiy_edit_product));
            // Get the loader
            getLoaderManager().initLoader(EXISTING_LOADER, null, this);
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        /**
         * Cast variables of edit text fields
         */
        mProductName = findViewById(R.id.et_product_name);
        mProductPrice = findViewById(R.id.et_product_price);
        mProductQuantity = findViewById(R.id.et_product_quant);
        mSupplierName = findViewById(R.id.et_supplier_name);
        mSupplierPhone = findViewById(R.id.et_supplier_phone);
    }

    private void saveProduct(){
        // Read from input fields
        // Use trim to eliminate leading or trailing white space

        String productNameString = mProductName.getText().toString().trim();
        String productPriceString = mProductPrice.getText().toString().trim();
        String productQuantString = mProductQuantity.getText().toString().trim();
        String supplierNameString = mSupplierName.getText().toString().trim();
        String supplierPhoneString = mSupplierPhone.getText().toString().trim();

        // check for empties
        if (TextUtils.isEmpty(productNameString)){
            Toast.makeText(this,"Product needs a name", Toast.LENGTH_SHORT).show();
            return;
        }else if (TextUtils.isEmpty(productPriceString)) {
            Toast.makeText(this, "Product needs a price", Toast.LENGTH_SHORT).show();
            return;
        }else if (TextUtils.isEmpty(productQuantString)) {
            Toast.makeText(this, "Product needs a quantity", Toast.LENGTH_SHORT).show();
            return;
        }else if (TextUtils.isEmpty(supplierNameString)) {
            Toast.makeText(this, "Product needs a supplier", Toast.LENGTH_SHORT).show();
            return;
        }


        // recast numbered entries
        double productPrice = Double.parseDouble(productPriceString);
        int productQuantity = Integer.parseInt(productQuantString);
        // Create a ContentValues object where column names are the keys,
        // and product attributes from the editor are the values.
        ContentValues values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, productNameString);
        values.put(ProductEntry.COLUMN_PRODUCT_PRICE, productPrice);
        values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, productQuantity);
        values.put(ProductEntry.COLUMN_SUPPLIER_NAME, supplierNameString);
        values.put(ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER, supplierPhoneString);



        if (mCurrentProductUri == null){
            // new product
            // Insert a new row for Product in the database, returning the Uri of that new row.
            Uri newUri = getContentResolver().insert(ProductEntry.CONTENT_URI, values);
            // Show a toast message depending on whether or not the insertion was successful
            if (newUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, getString(R.string.editor_save_product_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can toast its success!
                Toast.makeText(this, getString(R.string.editor_save_product_successful),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            // updating existing product
            int rowsAffected = getContentResolver().update(mCurrentProductUri, values, null, null);
            // Show a toast message depending on whether or not the update was successful.
            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, getString(R.string.editor_save_product_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_save_product_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_insert_editor_data:
                // Save Product to database
                saveProduct();
                // Exit activity
                finish();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // Navigate back to parent activity (MainActivity)
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Need the projection of the Product
        String[] projection = {
                ProductEntry._ID,
                ProductEntry.COLUMN_PRODUCT_NAME,
                ProductEntry.COLUMN_PRODUCT_QUANTITY,
                ProductEntry.COLUMN_PRODUCT_PRICE,
                ProductEntry.COLUMN_SUPPLIER_NAME,
                ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER };

        return new CursorLoader(this,
                mCurrentProductUri,
                projection,
                null,
                null,
                null);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }
        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (cursor.moveToFirst()) {
            int nameColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME);
            int priceColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PRICE);
            int quantColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY);
            int supplierNameColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_SUPPLIER_NAME);
            int supplierPhoneColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER);
            // Extract out the value from the Cursor for the given column index
            String name = cursor.getString(nameColumnIndex);
            int price = cursor.getInt(priceColumnIndex);
            int quant = cursor.getInt(quantColumnIndex);
            String supplierName = cursor.getString(supplierNameColumnIndex);
            int supplierPhone = cursor.getInt(supplierPhoneColumnIndex);
            // Update the views on the screen with the values from the database
            mProductName.setText(name);
            mProductPrice.setText(Integer.toString(price));
            mProductQuantity.setText(Integer.toString(quant));
            mSupplierName.setText(supplierName);
            mSupplierPhone.setText(Integer.toString(supplierPhone));

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        mProductName.setText("");
        mProductPrice.setText("");
        mProductQuantity.setText("");
        mSupplierName.setText("");
        mSupplierPhone.setText("");
    }
}
