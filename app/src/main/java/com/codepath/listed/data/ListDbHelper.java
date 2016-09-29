package com.codepath.listed.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Sanjay Maharjan on 9/22/2016.
 */
public class ListDbHelper extends SQLiteOpenHelper{
    public static final String LOG_TAG = ListDbHelper.class.getSimpleName();
    /** Name of the database file */
    private static final String DATABASE_NAME = "makealist.db";

    /**
     * Database version. If you change the database schema, you must increment the database version.
     */
    private static final int DATABASE_VERSION = 1;

    /**
     * Constructs a new instance of {@link ListDbHelper}.
     *
     * @param context of the app
     */

    public ListDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create a String that contains the SQL statement to create the pets table

        String SQL_CREATE_LIST_TABLE ="CREATE TABLE " + ListContract.ItemEntry.TABLE_NAME + " ("
                + ListContract.ItemEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + ListContract.ItemEntry.COLUMN_LIST_NAME +" TEXT NOT NULL,"
                + ListContract.ItemEntry.COLUMN_LIST_DISCRIPTION + " TEXT NOT NULL,"
                + ListContract.ItemEntry.COLUMN_lIST_PRIOTITY + " INTEGER NOT NULL);";

        // Execute the SQL statement
            db.execSQL(SQL_CREATE_LIST_TABLE);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
