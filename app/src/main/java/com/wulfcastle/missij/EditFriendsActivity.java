package com.wulfcastle.missij;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;


public class EditFriendsActivity extends ListActivity {

    public static final String TAG = EditFriendsActivity.class.getSimpleName();
    protected List<ParseUser> mUsers;
    protected ParseRelation<ParseUser> mFriendsRelation; // Variable to declare friendship between two users
    protected ParseUser mCurrentUser; // Variable to get current user

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_edit_friends);


        getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE); // Gets default list view associated with this activity and set's the Choice Mode for the List View allow multiple choices
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.edit_friends, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        mCurrentUser = ParseUser.getCurrentUser(); // Setting mCurrentUser to the current user (the user selected from ListView ---> The Edit Friends Page)

        // Getting the relation status of the current user & app user - down below :
        mFriendsRelation = mCurrentUser.getRelation(ParseConstants.KEY_FRIENDS_RELATION); // Parse.com automatically creates the String key "KEY_FRIENDS_RELATION" in the back-end of Parse.com

        setProgressBarIndeterminateVisibility(true); // Opening Progress Bar

        // Below 4 lines of code get the query
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.orderByAscending(ParseConstants.KEY_USERNAME);
        query.setLimit(1000);
        query.findInBackground(new FindCallback<ParseUser>() {


            // Below method converts the query to a String Array and uses an ArrayAdapter to displays the query to a ListView
            @Override
            public void done(List<ParseUser> users, ParseException e) {

                setProgressBarIndeterminateVisibility(false); // Closing Progress Bar
                if (e == null) {
                    mUsers = users;
                    String[] usernames = new String[mUsers.size()];
                    int i=0;
                    for(ParseUser user : mUsers) { // Each in loop - Constructor Loop ----> "ParseUser user" is a single user from Parse.com back-end.
                    // ParseUser user is being extracted from the List<> of ParseUsers (see line 47 ^^^^) and parsed into the array
                    // For each ParseUser in list ParseUser do the following :

                        usernames[i] = user.getUsername();
                        i++;                    }

                    // Creating adapter to convert and display String[] usernames in a list
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(EditFriendsActivity.this, android.R.layout.simple_list_item_checked, usernames);
                    setListAdapter(adapter);

                } else {
                    Log.e(TAG, e.getMessage());
                     alert(getString(R.string.error_title)).show();
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        mFriendsRelation.add(mUsers.get(position)); // Adding Friend by getting position of the displayed user from the ListView.
        // #Take note that this only adds the relation locally, we also need to save the relation to the Parse.com backend


        if (getListView().isItemChecked(position)) {
            // Add Friend
            mCurrentUser.saveInBackground(new SaveCallback() { // Saving the relation to the Parse.com back-end
                @Override
                public void done(ParseException e) {
                    if (e != null) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            });
        } else {
            // Remove Friend
            
        }
    }

    public AlertDialog alert(String error) {
        AlertDialog.Builder builder = new AlertDialog.Builder(EditFriendsActivity.this); // Creating Alert Dialog
        builder.setMessage(error);
        builder.setTitle(R.string.sign_up_error_titles);
        builder.setPositiveButton(android.R.string.ok, null);

        AlertDialog dialog = builder.create();
        return dialog;
    }



}
