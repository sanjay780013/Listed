package com.codepath.listed.activities;

import android.app.LoaderManager;
import android.content.ContentUris;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.codepath.listed.adapters.ItemCursorAdapter;
import com.codepath.listed.R;
import com.codepath.listed.data.ListContract;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {


    /** Adapter for the ListView */
    ItemCursorAdapter mCursorAdapter;

    /** Identifier for the  data loader */
    private static final int ITEM_LOADER = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,Editor.class);
                startActivity(intent);
            }
        });
        // Find the ListView which will be populated with the data

        ListView itemListView = (ListView) findViewById(R.id.list);

        View emptyView = findViewById(R.id.empty_view);
        itemListView.setEmptyView(emptyView);

        // Setup an Adapter to create a list item for each row of  data in the Cursor.
        // There is no  data yet (until the loader finishes) so pass in null for the Cursor.
        mCursorAdapter=new ItemCursorAdapter(this,null);
        itemListView.setAdapter(mCursorAdapter);

        // Setup the item click listener

        itemListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Create new intent to go to {@link EditorActivity}

                Intent intent = new Intent(MainActivity.this,Editor.class);
                // Form the content URI that represents the specific data that was clicked on,
                // by appending the "id" (passed as input to this method) onto the

                Uri currentItemUri = ContentUris.withAppendedId(ListContract.ItemEntry.CONTENT_URI,id);

                // Set the URI on the data field of the intent
                intent.setData(currentItemUri);

                // Launch the {@link EditorActivity} to display the data for the current pet.
                startActivity(intent);
            }
        });
        // Kick off the loader
        getLoaderManager().initLoader(ITEM_LOADER, null, this);
    }

    /**
     * Helper method to delete all items in the database.
     */
    private void deleteAllItems() {
        int rowsDelete = getContentResolver().delete(ListContract.ItemEntry.CONTENT_URI,null,null);
        Log.v("MainActivity", rowsDelete + " rows deleted from Item database");
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                // Do nothing for now
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
               deleteAllItems();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                ListContract.ItemEntry._ID,
                ListContract.ItemEntry.COLUMN_LIST_NAME,
                ListContract.ItemEntry.COLUMN_LIST_DISCRIPTION,
                ListContract.ItemEntry.COLUMN_lIST_PRIOTITY
        };

        // Perform a query on the list table
        return new CursorLoader (this,
                ListContract.ItemEntry.CONTENT_URI,   // The content URI of the words table
                projection,             // The columns to return for each row
                null,                   // Selection criteria
                null,                   // Selection criteria
                ListContract.ItemEntry.COLUMN_lIST_PRIOTITY+" DESC");                  // The sort order for the returned rows

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }


}
