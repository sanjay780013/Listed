package com.codepath.listed.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

/**
 * Created by Sanjay Maharjan on 9/26/2016.
 */
public class ItemProvider extends ContentProvider {

    /**
     * Tag for the log messages
     */
    public static final String LOG_TAG = ItemProvider.class.getSimpleName();

    /**
     * URI matcher code for the content URI for the whole Item table
     */

    private static final int ITEMS = 100;

    /**
     * URI matcher code for the content URI for a single list Item in the table
     */

    private static final int ITEMS_ID = 101;


    /**
     * UriMatcher object to match a content URI to a corresponding code.
     * The input passed into the constructor represents the code to return for the root URI.
     * It's common to use NO_MATCH as the input for this case.
     */

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        // The calls to addURI() go here, for all of the content URI patterns that the provider
        // should recognize. All paths added to the UriMatcher have a corresponding code to return
        // when a match is found.
        sUriMatcher.addURI(ListContract.CONTENT_AUTHORITY, ListContract.PATH_LIST, ITEMS);
        // The content URI of the form "content://com.codepath.listed/List/#" will map to the
        // integer code {@link #ITEMS_ID}. This URI is used to provide access to ONE single row
        // of the pets table.
        sUriMatcher.addURI(ListContract.CONTENT_AUTHORITY, ListContract.PATH_LIST + "/#", ITEMS_ID);
    }

    private ListDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new ListDbHelper(getContext());
        return true;
    }


    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor;
        // Figure out if the URI matcher can match the URI to a specific code

      final int match=sUriMatcher.match(uri);
        switch (match) {
            case ITEMS:

                // For the ITEMS code, query the lists table directly with the given
                // projection, selection, selection arguments, and sort order. The cursor
                // could contain multiple rows of the list table.

                cursor = db.query(ListContract.ItemEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;

            case ITEMS_ID:
                selection = ListContract.ItemEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                cursor = db.query(ListContract.ItemEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        // Set notification URI on the Cursor,
        // so we know what content URI the Cursor was created for.
        // If the data at this URI changes, then we know we need to update the Cursor.
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }



    @Override
    public String getType(Uri uri) {

        final int match = sUriMatcher.match(uri);
        switch (match){
            case ITEMS:
                return ListContract.ItemEntry.CONTENT_LIST_TYPE;
            case ITEMS_ID:
                return ListContract.ItemEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ITEMS:
                return insertItems(uri, values);

            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertItems(Uri uri, ContentValues values) {
        //Check that the name is not null
        String name = values.getAsString(ListContract.ItemEntry.COLUMN_LIST_NAME);
        if (name == null) {
            throw new IllegalArgumentException("List Name  requires a name");
        }

        //Check that if priority is valid
        Integer priority = values.getAsInteger(ListContract.ItemEntry.COLUMN_lIST_PRIOTITY);
        if (priority == null || !ListContract.ItemEntry.isValidPriority(priority)) {
            throw new IllegalArgumentException("Items requires valid priority");
        }
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        //Insert the new item with the given values

        long id = db.insert(ListContract.ItemEntry.TABLE_NAME, null, values);
        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }
        // Notify all listeners that the data has changed for the pet content URI
        getContext().getContentResolver().notifyChange(uri, null);

        // Return the new URI with the ID (of the newly inserted row) appended at the end
        return ContentUris.withAppendedId(uri, id);

    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {


        SQLiteDatabase db =mDbHelper.getWritableDatabase();

        int rowsDeleted;

        final int match=sUriMatcher.match(uri);
        switch (match){
            case ITEMS:
                rowsDeleted= db.delete(ListContract.ItemEntry.TABLE_NAME,selection,selectionArgs);
                break;
            case ITEMS_ID:
                selection = ListContract.ItemEntry._ID +"=?";
                selectionArgs= new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted= db.delete(ListContract.ItemEntry.TABLE_NAME,selection,selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        // If 1 or more rows were deleted, then notify all listeners that the data at the
        // given URI has changed
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri,null);
        }

        // Return the number of rows deleted
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ITEMS:
                return updateItems(uri, values, selection, selectionArgs);
            case ITEMS_ID:
                selection = ListContract.ItemEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateItems(uri, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }


    }

    private int updateItems(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        //Check that the name is not null

        if (values.containsKey(ListContract.ItemEntry.COLUMN_LIST_NAME)) {
            String name = values.getAsString(ListContract.ItemEntry.COLUMN_LIST_NAME);
            if (name == null) {
                throw new IllegalArgumentException("List Name  requires a name");
            }
        }

        //Check that if priority is valid

        if(values.containsKey(ListContract.ItemEntry.COLUMN_lIST_PRIOTITY)){
            Integer priority = values.getAsInteger(ListContract.ItemEntry.COLUMN_lIST_PRIOTITY);

            if (priority == null || !ListContract.ItemEntry.isValidPriority(priority)) {
                throw new IllegalArgumentException("Items requires valid priority");
                // If there are no values to update, then don't try to update the database
            }
        }


        if (values.size() == 0) {
            return 0;
        }

        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        int rowsUpdate = db.update(ListContract.ItemEntry.TABLE_NAME, values, selection, selectionArgs);

        if(rowsUpdate!=0){
            getContext().getContentResolver().notifyChange(uri,null);
        }
        // Returns the number of database rows affected by the update statement
        return rowsUpdate;
    }

}

