package ch.mse.mybudget;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import ch.mse.mybudget.data.BudgetrContract;

/**
 * Allows user to create a new pet or edit an existing one.
 */
public class EditorExpendituresActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * Identifier for the expenditure data loader
     */
    private static final int EXISTING_EXPENDITURE_LOADER = 0;

    /**
     * Content URI for the existing expenditure (null if it's a new expenditure)
     */
    private Uri mCurrentExpenditureUri;

    /**
     * EditText field to enter the expenditure's amount
     */
    private EditText mAmountEditText;

    /**
     * EditText field to enter the expenditure's date
     */
    private EditText mDateEditText;

    /**
     * EditText field to enter the expenditure's place
     */
    private EditText mPlaceEditText;

    /**
     * EditText field to enter the expenditure's description
     */
    private EditText mDescriptionEditText;


    /**
     * Boolean flag that keeps track of whether the expenditure has been edited (true) or not (false)
     */
    private boolean mExpenditureHasChanged = false;

    /**
     * OnTouchListener that listens for any user touches on a View, implying that they are modifying
     * the view, and we change the mExpenditureHasChanged boolean to true.
     */
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mExpenditureHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor_expenditures);

        // Examine the intent that was used to launch this activity,
        // in order to figure out if we're creating a new expenditure or editing an existing one.
        Intent intent = getIntent();
        mCurrentExpenditureUri = intent.getData();

        // If the intent DOES NOT contain a expenditure content URI, then we know that we are
        // creating a new expenditure.
        if (mCurrentExpenditureUri == null) {
            // This is a new expenditure, so change the app bar to say "Add a Expenditure"
            setTitle(getString(R.string.editor_expenditures_activity_title_new_expenditure));

            // Invalidate the options menu, so the "Delete" menu option can be hidden.
            // (It doesn't make sense to delete a pet that hasn't been created yet.)
            invalidateOptionsMenu();
        } else {
            // Otherwise this is an existing expenditure, so change app bar to say "Edit Expenditure"
            setTitle(getString(R.string.editor_expenditures_activity_title_edit_expenditure));

            // Initialize a loader to read the expenditure data from the database
            // and display the current values in the editor
            getLoaderManager().initLoader(EXISTING_EXPENDITURE_LOADER, null, this);
        }

        // Find all relevant views that we will need to read user input from
        mAmountEditText = (EditText) findViewById(R.id.et_editor_expenditures_amount);
        mDateEditText = (EditText) findViewById(R.id.et_editor_expenditures_date);
        mPlaceEditText = (EditText) findViewById(R.id.et_editor_expenditures_place);
        mDescriptionEditText = (EditText) findViewById(R.id.et_editor_expenditures_description);

        // Setup OnTouchListeners on all the input fields, so we can determine if the user
        // has touched or modified them. This will let us know if there are unsaved changes
        // or not, if the user tries to leave the editor without saving.
        mAmountEditText.setOnTouchListener(mTouchListener);
        mDateEditText.setOnTouchListener(mTouchListener);
        mPlaceEditText.setOnTouchListener(mTouchListener);
        mDescriptionEditText.setOnTouchListener(mTouchListener);

    }

    /**
     * Get user input from editor and save expenditure into database.
     */
    private void saveExpenditure() {
        // Read from input fields
        // Use trim to eliminate leading or trailing white space
        String amountString = mAmountEditText.getText().toString().trim();
        String dateString = mDateEditText.getText().toString().trim();
        String placeString = mPlaceEditText.getText().toString().trim();
        String descriptionString = mDescriptionEditText.getText().toString().trim();

        // Check if this is supposed to be a new expenditure
        // and check if all the fields in the editor are blank
        if (mCurrentExpenditureUri == null &&
                TextUtils.isEmpty(amountString) && TextUtils.isEmpty(dateString) &&
                TextUtils.isEmpty(placeString) && TextUtils.isEmpty(descriptionString)) {
            // Since no fields were modified, we can return early without creating a new expenditure.
            // No need to create ContentValues and no need to do any ContentProvider operations.
            return;
        }

        // Create a ContentValues object where column names are the keys,
        // and expenditure attributes from the editor are the values.
        ContentValues values = new ContentValues();
        values.put(BudgetrContract.ExpenditureEntry.COLUMN_NAME_DATE, dateString);
        values.put(BudgetrContract.ExpenditureEntry.COLUMN_NAME_PLACE, placeString);
        values.put(BudgetrContract.ExpenditureEntry.COLUMN_NAME_DESCRIPTION, descriptionString);
        // If the amount is not provided by the user, don't try to parse the string into an
        // float value. Use 0 by default.
        float amount = 0;
        if (!TextUtils.isEmpty(amountString)) {
            amount = Float.parseFloat(amountString);
        }
        values.put(BudgetrContract.ExpenditureEntry.COLUMN_NAME_AMOUNT, amount);

        // Determine if this is a new or existing expenditure by checking if mCurrentExpenditureUri is null or not
        if (mCurrentExpenditureUri == null) {
            // This is a NEW expenditure, so insert a new expenditure into the provider,
            // returning the content URI for the new expenditure.
            Uri newUri = getContentResolver().insert(BudgetrContract.ExpenditureEntry.CONTENT_URI, values);

            // Show a toast message depending on whether or not the insertion was successful.
            if (newUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, getString(R.string.editor_expenditures_insert_expenditure_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_expenditures_insert_expenditure_successful),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            // Otherwise this is an EXISTING expenditure, so update the expenditure with content URI: mCurrentExpenditureUri
            // and pass in the new ContentValues. Pass in null for the selection and selection args
            // because mCurrentExpenditureUri will already identify the correct row in the database that
            // we want to modify.
            int rowsAffected = getContentResolver().update(mCurrentExpenditureUri, values, null, null);

            // Show a toast message depending on whether or not the update was successful.
            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, getString(R.string.editor_expenditures_update_expenditure_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_expenditures_update_expenditure_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor_activity.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor_activity, menu);
        return true;
    }

    /**
     * This method is called after invalidateOptionsMenu(), so that the
     * menu can be updated (some menu items can be hidden or made visible).
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new expenditure, hide the "Delete" menu item.
        if (mCurrentExpenditureUri == null) {
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
                saveExpenditure();
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
                // If the pet hasn't changed, continue with navigating up to parent activity
                // which is the {@link CatalogActivity}.
                if (!mExpenditureHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorExpendituresActivity.this);
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
                                NavUtils.navigateUpFromSameTask(EditorExpendituresActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * This method is called when the back button is pressed.
     */
    @Override
    public void onBackPressed() {
        // If the expenditure hasn't changed, continue with handling back button press
        if (!mExpenditureHasChanged) {
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
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Since the editor shows all expenditure attributes, define a projection that contains
        // all columns from the expenditure table
        String[] projection = {
                BudgetrContract.ExpenditureEntry._ID,
                BudgetrContract.ExpenditureEntry.COLUMN_NAME_AMOUNT,
                BudgetrContract.ExpenditureEntry.COLUMN_NAME_DATE,
                BudgetrContract.ExpenditureEntry.COLUMN_NAME_PLACE,
                BudgetrContract.ExpenditureEntry.COLUMN_NAME_DESCRIPTION};

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                mCurrentExpenditureUri,         // Query the content URI for the current expenditure
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (cursor.moveToFirst()) {
            // Find the columns of pet attributes that we're interested in
            int amountColumnIndex = cursor.getColumnIndex(BudgetrContract.ExpenditureEntry.COLUMN_NAME_AMOUNT);
            int dateColumnIndex = cursor.getColumnIndex(BudgetrContract.ExpenditureEntry.COLUMN_NAME_DATE);
            int placeColumnIndex = cursor.getColumnIndex(BudgetrContract.ExpenditureEntry.COLUMN_NAME_PLACE);
            int descriptionColumnIndex = cursor.getColumnIndex(BudgetrContract.ExpenditureEntry.COLUMN_NAME_DESCRIPTION);

            // Extract out the value from the Cursor for the given column index
            float amount = cursor.getFloat(amountColumnIndex);
            String date = cursor.getString(dateColumnIndex);
            String place = cursor.getString(placeColumnIndex);
            String description = cursor.getString(descriptionColumnIndex);

            // Update the views on the screen with the values from the database
            mAmountEditText.setText(Float.toString(amount));
            mDateEditText.setText(date);
            mPlaceEditText.setText(place);
            mDescriptionEditText.setText(description);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        mAmountEditText.setText("");
        mDateEditText.setText("");
        mPlaceEditText.setText("");
        mDescriptionEditText.setText("");
    }

    /**
     * Show a dialog that warns the user there are unsaved changes that will be lost
     * if they continue leaving the editor.
     *
     * @param discardButtonClickListener is the click listener for what to do when
     *                                   the user confirms they want to discard their changes
     */
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

    /**
     * Prompt the user to confirm that they want to delete this expenditure.
     */
    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the expenditure.
                deleteExpenditure();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the expenditure.
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
     * Perform the deletion of the expenditure in the database.
     */
    private void deleteExpenditure() {
        // Only perform the delete if this is an existing expenditure.
        if (mCurrentExpenditureUri != null) {
            // Call the ContentResolver to delete the pet at the given content URI.
            // Pass in null for the selection and selection args because the mCurrentPetUri
            // content URI already identifies the pet that we want.
            int rowsDeleted = getContentResolver().delete(mCurrentExpenditureUri, null, null);

            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.editor_expenditures_delete_expenditure_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_expenditures_delete_expenditure_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }

        // Close the activity
        finish();
    }
}
