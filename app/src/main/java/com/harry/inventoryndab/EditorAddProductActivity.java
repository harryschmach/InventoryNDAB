package com.harry.inventoryndab;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.net.Uri;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

    /**
     * Instantiate the buttons
     */
    Button incQuantbtn;
    Button decQuantbtn;
    Button callVendorbtn;


    // Get the loader
    private static final int EXISTING_LOADER = 0;
    /** Content URI for the existing product (null if it's a new product) */
    private Uri mCurrentProductUri;

    // Boolean for change
    private boolean mContentChanged = false;

    @SuppressLint("ClickableViewAccessibility")
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

            // Dont need the "clear" option in menu... which is the whole menu...
            invalidateOptionsMenu();

        }else {
            // Edit Product!
            setTitle(getString(R.string.editor_activitiy_edit_product));
            // Get the loader
            getLoaderManager().initLoader(EXISTING_LOADER, null, this);
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        /**
         * Cast variables of widgets
         */
        mProductName = findViewById(R.id.et_product_name);
        mProductPrice = findViewById(R.id.et_product_price);
        mProductQuantity = findViewById(R.id.et_product_quant);
        mSupplierName = findViewById(R.id.et_supplier_name);
        mSupplierPhone = findViewById(R.id.et_supplier_phone);
        decQuantbtn = findViewById(R.id.decrease_quant_btn);
        incQuantbtn = findViewById(R.id.increase_quant_btn);
        callVendorbtn = findViewById(R.id.call_supplier_btn);

        /**
         * Give the buttons their functions
         */
        decQuantbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                decreaseQuant();
            }
        });
        incQuantbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                incrementQuant();
            }
        });
        callVendorbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String supplierPhoneString = mSupplierPhone.getText().toString().trim();
                dialPhoneNumber(supplierPhoneString);
            }
        });

        mProductName.setOnTouchListener(mTouchListener);
        mProductPrice.setOnTouchListener(mTouchListener);
        mProductQuantity.setOnTouchListener(mTouchListener);
        mSupplierName.setOnTouchListener(mTouchListener);
        mSupplierPhone.setOnTouchListener(mTouchListener);
        decQuantbtn.setOnTouchListener(mTouchListener);
        incQuantbtn.setOnTouchListener(mTouchListener);
    }

    private boolean saveProduct(){
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
            return false;
        }else if (TextUtils.isEmpty(productPriceString)) {
            Toast.makeText(this, "Product needs a price", Toast.LENGTH_SHORT).show();
            return false;
        }else if (TextUtils.isEmpty(productQuantString)) {
            Toast.makeText(this, "Product needs a quantity", Toast.LENGTH_SHORT).show();
            return false;
        }else if (TextUtils.isEmpty(supplierNameString)) {
            Toast.makeText(this, "Product needs a supplier", Toast.LENGTH_SHORT).show();
            return false;
        }else if (TextUtils.isEmpty(supplierPhoneString)) {
            Toast.makeText(this, "Product needs a supplier phone number", Toast.LENGTH_SHORT).show();
            return false;
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
        return true;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new pet, hide the "Delete" menu item.
        if (mCurrentProductUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete_in_editor);
            menuItem.setVisible(false);
        }
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_insert_editor_data:
                // Save Product to database
                boolean successSave = saveProduct();
                // Exit activity
                if (successSave){
                    finish();
                }
                return true;
            // Delete option
            case R.id.action_delete_in_editor:
                showDeleteConfirmationDialog();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // Navigate back to parent activity (MainActivity) if no changes
                if (!mContentChanged) {
                    NavUtils.navigateUpFromSameTask(EditorAddProductActivity.this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(EditorAddProductActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
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
            //debug
            Log.v("load name Index", String.valueOf(nameColumnIndex));
            Log.v("load price Index", String.valueOf(priceColumnIndex));
            Log.v("load quant Index", String.valueOf(quantColumnIndex));
            Log.v("load sName Index", String.valueOf(supplierNameColumnIndex));
            Log.v("load sNum Index", String.valueOf(supplierPhoneColumnIndex));
            // Extract out the value from the Cursor for the given column index
            String name = cursor.getString(nameColumnIndex);
            int price = cursor.getInt(priceColumnIndex);
            int quant = cursor.getInt(quantColumnIndex);
            String supplierName = cursor.getString(supplierNameColumnIndex);
            String supplierPhone = cursor.getString(supplierPhoneColumnIndex);
            // Update the views on the screen with the values from the database
            mProductName.setText(name);
            mProductPrice.setText(Integer.toString(price));
            mProductQuantity.setText(Integer.toString(quant));
            mSupplierName.setText(supplierName);
            mSupplierPhone.setText(supplierPhone);

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

    private void incrementQuant(){
        String strQuant = mProductQuantity.getText().toString();
        int currentQuant = Integer.parseInt(strQuant);
        int newQuant = currentQuant + 1;
        mProductQuantity.setText(Integer.toString(newQuant));
    }
    private void decreaseQuant(){
        String strQuant = mProductQuantity.getText().toString();
        int currentQuant = Integer.parseInt(strQuant);
        int newQuant;
        // No negative inventory!
        if (currentQuant < 1){
            newQuant = 0;
        }else {
            newQuant = currentQuant - 1;
        }
        mProductQuantity.setText(Integer.toString(newQuant));
    }

    // OnTouchListener that listens for any user touches on a View, implying that they are modifying
    // the view, and we change the mContentChanged boolean to true.

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mContentChanged = true;
            Log.v("EditorActivity", "Something changed!! AHHH!!");
            Log.v("EditorActivity", String.valueOf(mContentChanged));
            return false;
        }
    };

    // Make a warning for unsaved changes
    private void showUnsavedChangesDialog(
        DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        // If the product hasn't changed, continue with handling back button press
        Log.v("EditorActivityBackBtn", String.valueOf(mContentChanged));
        if (!mContentChanged) {
            Log.v("EditorActivityBackBtn", "Returning with Super");
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }
    // Deletion
    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the product.
                deleteProduct();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the product.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Perform the deletion of the product in the database.
     */
    private void deleteProduct() {
        if (mCurrentProductUri != null){
            // Get content resolver to do our dirty work
            int rowsDeleted = getContentResolver().delete(mCurrentProductUri, null, null);
            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, "Item Not Deleted. Weird.", Toast.LENGTH_LONG).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, "Item Deleted", Toast.LENGTH_LONG).show();
            }
        }
        finish();
    }

    public void dialPhoneNumber(String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }
}
