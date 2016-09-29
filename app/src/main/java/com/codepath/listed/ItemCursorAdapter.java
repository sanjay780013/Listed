package com.codepath.listed;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.codepath.listed.data.ListContract;

/**
 * Created by Sanjay Maharjan on 9/28/2016.
 */
public class ItemCursorAdapter extends CursorAdapter {

    /**
     * Constructs a new {@link ItemCursorAdapter}.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     */

    public ItemCursorAdapter(Context context,Cursor c){
        super(context, c,0 /* flags */);
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
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Find individual views that we want to modify in the list item layout
        TextView nameTextView = (TextView) view.findViewById(R.id.name);
        TextView summaryTextView = (TextView) view.findViewById(R.id.summary);

        // Find the columns of items attributes that we're interested in
        int nameColumnIndex = cursor.getColumnIndex(ListContract.ItemEntry.COLUMN_LIST_NAME);
        int summarayColumnIndex = cursor.getColumnIndex(ListContract.ItemEntry.COLUMN_LIST_DISCRIPTION);

        String itemName=cursor.getString(nameColumnIndex);
        String itemDescription= cursor.getString(summarayColumnIndex);

        if(TextUtils.isEmpty(itemDescription)){
            itemDescription=context.getString(R.string.no_summaray);
        }
        nameTextView.setText(itemName);
        summaryTextView.setText(itemDescription);

    }
}
