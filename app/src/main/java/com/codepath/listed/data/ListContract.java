package com.codepath.listed.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Sanjay Maharjan on 9/22/2016.
 */
public class ListContract {
    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.

    private ListContract(){}

    /**
     * The "Content authority" is a name for the entire content provider, similar to the
     * relationship between a domain name and its website.  A convenient string to use for the
     * content authority is the package name for the app, which is guaranteed to be unique on the
     * device.
     */
    public static final String CONTENT_AUTHORITY ="com.codepath.listed";

    /**
     * Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
     * the content provider.
     */

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);


    public static final String PATH_LIST = "list";


    public static final class ItemEntry implements BaseColumns {
        /** The content URI to access the List data in the provider */
        public static  final Uri CONTENT_URI=Uri.withAppendedPath(BASE_CONTENT_URI,PATH_LIST);
        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of items.
         */

        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_LIST;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single item.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_LIST;


        /** Name of database table for List Items */
        public final static String TABLE_NAME = "List";


        /**
         * Unique ID number for the list items (only for use in the database table).
         *
         * Type: INTEGER
         */
        public final static String _ID = BaseColumns._ID;

        /**
         * Name of the Reminder.
         *
         * Type: TEXT
         */
        public final static String COLUMN_LIST_NAME ="name";
        /**
         * Description of the items.
         *
         * Type: TEXT
         */
        public final static String COLUMN_LIST_DISCRIPTION = "description";

        public final static String COLUMN_lIST_PRIOTITY = "priority";

        /**
         * Possible values of the priority.
         */
        public static final int PRIORITY_MED = 0;
        public static final int PRIORITY_LOW = 1;
        public static final int PRIORITY_HIGH = 2;

        public static boolean isValidPriority(int priority) {
            if (priority == PRIORITY_MED||priority==PRIORITY_HIGH||priority==PRIORITY_LOW) {
            return true;
            }
            return false;
        }

    }


}
