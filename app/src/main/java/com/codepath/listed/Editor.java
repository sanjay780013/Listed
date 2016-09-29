package com.codepath.listed;



import android.support.v7.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
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
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.codepath.listed.data.ListContract;

public class Editor extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{


    /** Identifier for the item data loader */
    private static final int EXISTING_ITEM_LOADER = 0;

    /** Content URI for the existing item (null if it's a new pet) */
    private Uri mCurrentItemUri;



    private EditText mNameEditText;

    private EditText mDescriptionEditText;

    private Spinner mPrioritySpinner;

    private int mPriority = ListContract.ItemEntry.PRIORITY_MED;

    /** Boolean flag that keeps track of whether the data has been edited (true) or not (false) */
    private boolean mItemHasChanged = false;

    /**
     * OnTouchListener that listens for any user touches on a View, implying that they are modifying
     * the view, and we change the mPetHasChanged boolean to true.
     */
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mItemHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Examine the intent that was used to launch this activity,
        // in order to figure out if we're creating a new List item or editing an existing one.

        Intent intent = getIntent();
        mCurrentItemUri=intent.getData();

        // If the intent DOES NOT contain a  content URI, then we know that we are
        // creating a new List item.

        if(mCurrentItemUri==null){
            // This is a new pet, so change the app bar to say "Add a ListItem"
            setTitle(getString(R.string.editor_activity_title_new_item));
            invalidateOptionsMenu();
        }
        else{
            setTitle(getString(R.string.editor_activity_title_edit_item));

            // Initialize a loader to read the data from the database
            // and display the current values in the editor
            getLoaderManager().initLoader(EXISTING_ITEM_LOADER,null,this);
        }

        mNameEditText =(EditText)findViewById(R.id.edit_todo_name);
        mDescriptionEditText = (EditText)findViewById(R.id.edit_description);
        mPrioritySpinner = (Spinner)findViewById(R.id.spinner);


        // Setup OnTouchListeners on all the input fields, so we can determine if the user
        // has touched or modified them. This will let us know if there are unsaved changes
        // or not, if the user tries to leave the editor without saving.

        mNameEditText.setOnTouchListener(mTouchListener);

        setupSpinner();
    }
    /**
     * Setup the dropdown spinner that allows the user to select the priority
     */


    public void setupSpinner(){
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter prioritySpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.spinner, android.R.layout.simple_spinner_item);

        prioritySpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        mPrioritySpinner.setAdapter(prioritySpinnerAdapter);

        mPrioritySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String)parent.getItemAtPosition(position);
                if(!TextUtils.isEmpty(selection)){
                    if(selection.equals(getString(R.string.priority_low))){
                        mPriority= ListContract.ItemEntry.PRIORITY_LOW;
                    }else if(selection.equals(getString(R.string.priority_medium))){
                        mPriority= ListContract.ItemEntry.PRIORITY_MED;
                    }else{
                        mPriority= ListContract.ItemEntry.PRIORITY_HIGH;
                    }

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mPriority = ListContract.ItemEntry.PRIORITY_MED;
            }
        });

    }
    public void saveItem(){

        // Read from input fields
        // Use trim to eliminate leading or trailing white space

        String nameString =mNameEditText.getText().toString().trim();
        String descriptionString = mDescriptionEditText.getText().toString().trim();
        // Check if this is supposed to be a new item
        // and check if all the fields in the editor are blank

        if(mCurrentItemUri==null &&
                TextUtils.isEmpty(nameString) &&
                TextUtils.isEmpty(descriptionString)&&
                mPriority== ListContract.ItemEntry.PRIORITY_MED){
            return;

        }


        // Create a ContentValues object where column names are the keys,
        // and item attributes from the editor are the values.

        ContentValues values = new ContentValues();
        values.put(ListContract.ItemEntry.COLUMN_LIST_NAME,nameString);
        values.put(ListContract.ItemEntry.COLUMN_LIST_DISCRIPTION,descriptionString);
        values.put(ListContract.ItemEntry.COLUMN_lIST_PRIOTITY,mPriority);

        if(mCurrentItemUri==null){
            Uri newUri =getContentResolver().insert(ListContract.ItemEntry.CONTENT_URI,values);

            // Show a toast message depending on whether or not the insertion was successful
            if (newUri == null) {
                // If the row ID is -1, then there was an error with insertion.
                Toast.makeText(this, "Error with saving List  Item", Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast with the row ID.
                Toast.makeText(this, "List Item saved", Toast.LENGTH_SHORT).show();
            }
        }else{
            // Otherwise this is an EXISTING data, so update the item with content URI: mCurrentItemUri
            // and pass in the new ContentValues. Pass in null for the selection and selection args
            // because mCurrentItemUri will already identify the correct row in the database that
            // we want to modify.

            int rowsAffected = getContentResolver().update(mCurrentItemUri,values,null,null);

            // Show a toast message depending on whether or not the update was successful.
            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, "Item update Failed",
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, "Item update Successful",
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
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new data, hide the "Delete" menu item.
        if (mCurrentItemUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // Save pet to database
                saveItem();
                // Exit activity
                finish();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Pop up confirmation dialog for deletion
                showDeleteConfirmationDialog();
                return true;

            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                if (!mItemHasChanged) {
                NavUtils.navigateUpFromSameTask(Editor.this);
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
                                NavUtils.navigateUpFromSameTask(Editor.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        // If the Item hasn't changed, continue with handling back button press
        if (!mItemHasChanged) {
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


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                ListContract.ItemEntry._ID,
                ListContract.ItemEntry.COLUMN_LIST_NAME,
                ListContract.ItemEntry.COLUMN_LIST_DISCRIPTION,
                ListContract.ItemEntry.COLUMN_lIST_PRIOTITY
        };

        // Perform a query on the pets table
        return new CursorLoader(this,
                mCurrentItemUri,   // The content URI of the words table
                projection,             // The columns to return for each row
                null,                   // Selection criteria
                null,                   // Selection criteria
                null);                  // The sort order for the returned rows
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
// Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }
        while(cursor.moveToNext()){
            int nameColumnIndex = cursor.getColumnIndex(ListContract.ItemEntry.COLUMN_LIST_NAME);
            int descriptionColumnIndex=cursor.getColumnIndex(ListContract.ItemEntry.COLUMN_LIST_DISCRIPTION);
            int priorityColumnIndex = cursor.getColumnIndex(ListContract.ItemEntry.COLUMN_lIST_PRIOTITY);

            String currentName = cursor.getString(nameColumnIndex);
            String currentDescription = cursor.getString(descriptionColumnIndex);
            int currentPriority = cursor.getInt(priorityColumnIndex);

            mNameEditText.setText(currentName);
            mDescriptionEditText.setText(currentDescription);


            switch(currentPriority){
                case ListContract.ItemEntry.PRIORITY_HIGH:
                    mPrioritySpinner.setSelection(2);
                    break;
                case ListContract.ItemEntry.PRIORITY_LOW:
                    mPrioritySpinner.setSelection(1);
                    break;
                default:
                    mPrioritySpinner.setSelection(0);
                    break;
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.

        mNameEditText.setText("");
        mDescriptionEditText.setText("");
        mPrioritySpinner.setSelection(0);
    }
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
                // and continue editing the item.
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
     * Prompt the user to confirm that they want to delete this item.
     */
    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pet.
                delete();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
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
    /**
     * Perform the deletion of the pet in the database.
     */
    private void delete() {
        // Only perform the delete if this is an existing item.
        if (mCurrentItemUri != null) {
            // Call the ContentResolver to delete the item at the given content URI.
            // Pass in null for the selection and selection args because the mCurrentPetUri
            // content URI already identifies the pet that we want.
            int rowsDeleted = getContentResolver().delete(mCurrentItemUri, null, null);

            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, "Error Deleting Item ",
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, "Item Deleted Successfully!! ",
                        Toast.LENGTH_SHORT).show();
            }
        }

        // Close the activity
        finish();
    }

}


