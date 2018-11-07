package com.harry.inventoryndab;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.harry.inventoryndab.R;
import com.harry.inventoryndab.data.ProductContract;

import org.w3c.dom.Text;

import java.text.DecimalFormat;

public class ProductCursorAdapter extends CursorAdapter {
    public ProductCursorAdapter(Context context, Cursor c) {
        super(context, c);
    }

    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param viewGroup  The parent to which the new view is attached to
     * @return the newly created list item view.
     */

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.product_list_item, viewGroup, false);
    }

    /**
     * This method binds the data (in the current row pointed to by cursor) to the given
     * list item layout. For example, the name for the current product can be set on the name TextView
     * in the list item layout.
     *
     * @param v    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */
    @Override
    public void bindView(final View v, final Context context, Cursor cursor) {
        // Get the name field
        TextView tvProductName = v.findViewById(R.id.product_name_list_item);
        // Get the Price field
        TextView tvProductPrice = v.findViewById(R.id.product_price_list_item);
        // Get the quantity field
        final TextView tvProductQuant = v.findViewById(R.id.product_quant_list_item);
        // Get the sell button
        Button btnProductSell = v.findViewById(R.id.product_sale_btn_list_item);

        // Get values from the cursor
        // Get ID
        final Integer pID = cursor.getInt(cursor.getColumnIndex(ProductContract.ProductEntry._ID));
        // Get name
        Log.v("id val: ", String.valueOf(pID));
        Log.v("cursor size: ", String.valueOf(cursor.getColumnCount()));


//        String pName = cursor.getString(cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_NAME));
//        // Get price
//        final Integer pPrice = cursor.getInt(cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE));
//        // Get quant
//        Integer pQuant = cursor.getInt(cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY));

        // Get supplier name & number
        int nameColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_NAME);
        int priceColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE);
        int quantColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY);
        int supplierNameColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_SUPPLIER_NAME);
        int supplierPhoneColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER);

        Log.v("name Index", String.valueOf(nameColumnIndex));
        Log.v("price Index", String.valueOf(priceColumnIndex));
        Log.v("quant Index", String.valueOf(quantColumnIndex));
        Log.v("sName Index", String.valueOf(supplierNameColumnIndex));
        Log.v("sNum Index", String.valueOf(supplierPhoneColumnIndex));
        // Extract out the value from the Cursor for the given column index
        final String pName= cursor.getString(nameColumnIndex);
        final Integer pPrice = cursor.getInt(priceColumnIndex);
        int pQuant = cursor.getInt(quantColumnIndex);
        final String supplierName = cursor.getString(supplierNameColumnIndex);
        final String supplierPhone = cursor.getString(supplierPhoneColumnIndex);

        // Update the views on the screen with the values from the database
        // Converting the integer price to cents, laborious
        float floatPrice = pPrice;
        float pPriceInDecimal = floatPrice / 100;
        DecimalFormat df2 = new DecimalFormat(".##");
        String pPriceToDisplay = "$" + df2.format(pPriceInDecimal);

        // Customize Textview
        tvProductName.setText(pName);
        tvProductPrice.setText(pPriceToDisplay);
        tvProductQuant.setText(String.valueOf(pQuant));

        // set the button up
        btnProductSell.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.v("SellOneItem", "Register button click");
                        String strQuant = tvProductQuant.getText().toString();
                        int currentQuant = Integer.parseInt(strQuant);
                        int newQuant;
                        // No negative inventory!
                        if (currentQuant < 1){
                            newQuant = 0;
                        }else {
                            newQuant = currentQuant - 1;
                        }
                        // Create a URI that the resolver requires:
                        Uri currentProductURI = ContentUris.withAppendedId(ProductContract.ProductEntry.CONTENT_URI, pID);
                        // Create a Content values to update the value with
                        ContentValues values = new ContentValues();
                        values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_NAME, pName);
                        values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE, pPrice);
                        // This will change if the
                        values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY, newQuant);
                        values.put(ProductContract.ProductEntry.COLUMN_SUPPLIER_NAME, supplierName);
                        values.put(ProductContract.ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER, supplierPhone);
                        context.getContentResolver().update(currentProductURI, values, null, null);
                    }
                }
        );
    }

    private void sellOneItem(View view, Context context){

    }

}
