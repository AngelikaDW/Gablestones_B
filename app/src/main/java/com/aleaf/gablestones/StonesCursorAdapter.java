package com.aleaf.gablestones;
import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;



/**
 * {@link StonesCursorAdapter} is an adapter for a list or grid view
 * that uses a {@link Cursor} of pet data as its data source. This adapter knows
 * how to create list items for each row of pet data in the {@link Cursor}.
 */

public class StonesCursorAdapter extends CursorAdapter{

    private LayoutInflater mInflater;
    /**
     * Constructs a new {@link StonesCursorAdapter}.
     *  @param context The context
     * @param cursor       The cursor from which to get the data.*/
    public StonesCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Inflate a list item view using the layout specified in list_item.xml
        return mInflater.inflate(R.layout.list_item, parent, false);
    }
    /**
     * This method binds the stones data (in the current row pointed to by cursor) to the given
     * list item layout. For example, the name for the current stone can be set on the name TextView
     * in the list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Find individual views that we want to modify in the list item layout
        TextView streetTextView = (TextView) view.findViewById(R.id.street);
        TextView categoryTextView = (TextView) view.findViewById(R.id.category);
        //Extract properties from cursor
        // Read the pet attributes from the Cursor for the current pet
        String stoneStreet = cursor.getString(cursor.getColumnIndexOrThrow("street"));
        String stoneCategory = cursor.getString(cursor.getColumnIndexOrThrow("category"));


        // Populate fields with extracted properties
        streetTextView.setText(stoneStreet);
        categoryTextView.setText(stoneCategory);
    }
}
