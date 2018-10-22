package com.harry.inventoryndab.data;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.harry.inventoryndab.R;

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
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Get the name field
        TextView tvProductName = view.findViewById(R.id.product_name_list_item);
        // Get the Price field
        TextView tvProductPrice = view.findViewById(R.id.product_price_list_item);
        // Get the quantity field
        TextView tvProductQuant = view.findViewById(R.id.product_quant_list_item);
        // Get the sell button
        Button btnProductSell = view.findViewById(R.id.product_sale_btn_list_item);

        // Get values from the cursor
        // Get name
        String pName = cursor.getString(cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_NAME));
        // Get price
        Integer pPrice = cursor.getInt(cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE));
        // Get quant
        Integer pQuant = cursor.getInt(cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY));

        // Converting the integer price to cents, laborious
        float floatPrice = pPrice;
        float pPriceInDecimal = floatPrice / 100;
        DecimalFormat df2 = new DecimalFormat(".##");
        String pPriceToDisplay = "$" + df2.format(pPriceInDecimal);

        // Customize Textview
        tvProductName.setText(pName);
        tvProductPrice.setText(pPriceToDisplay);
        tvProductQuant.setText(String.valueOf(pQuant));
    }
}