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
import com.parse.Parse;
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

                    addFriendCheckmarks();

                } else {
                    Log.e(TAG, e.getMessage());
                     alert(getString(R.string.error_title)).show();
                }
            }
        });
    }



    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);


        if (getListView().isItemChecked(position)) {

            // Add Friend
            mFriendsRelation.add(mUsers.get(position));

                /* Adding Friend by getting position of the displayed user ---->
                 Remember that we have to use mUsers (as mUsers is the whole ist of users), not mCurrentUser for this) from the ListView. */
                // #Take note that this only adds the relation locally, we also need to save the relation to the Parse.com backend, down below.
         } else {

            // Remove Friend
            mFriendsRelation.remove(mUsers.get(position));
        }

        mCurrentUser.saveInBackground(new SaveCallback() { // Saving the relation to the Parse.com back-end
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, e.getMessage());
                }
            }
        });

    }

    private void addFriendCheckmarks() {
        mFriendsRelation.getQuery().findInBackground(new FindCallback<ParseUser>() { // Get relation status from Parse.com
            @Override
            public void done(List<ParseUser> friends, ParseException e) {

                if (e == null) {
                    // Query succeeded ---> List of Users Returned -----> Look for a match
                    for (int i=0; i < mUsers.size(); i++) { // mUsers is the list of all users
                       ParseUser user = mUsers.get(i); // Looping through all users and storing current user in ParseUser variable -----> mUsers is a lift of all users in the database
                        for (ParseUser friend : friends) { // friends is a list of all the app user's friends
                            // For *each* ParseUser (known as friend in this loop) in List<ParseUser> (List of ParseUsers)  of  "friends" do the following :

                            if (friend.getObjectId().equals(user.getObjectId())) {

                            /*

                             Here the the current user's Object ID (from "ParseUser user" variable above (which is acquired from mUsers, which is a list of all users)
                             is being compared Object ID's from the list of friends (ParseUser friend is a single user from the list of all friends. The list of all friends are defined above in line 138)

                             If the Object ID of the ParseUser "user" variable matches the ParseUser "friend" variable it means that the current user in the loop is a friend of the app user.
                             Therefore we can check the, CheckMark to show that the user is a friend

                             ## Take note that ParseUser "user" and ParseUser "friend" will always be at the same index (i.e. the same place in the list) as both are listed in a nested for-loop
                                that means that we will be setting the CheckMark for the right user

                             */

                               getListView().setItemChecked(i, true);
                               /* Setting the checkmark. "i" is being used as the int for the position
                                (as we are still in the for loop) and the CheckMark is set to true */

                            }
                        }
                    }

                } else {
                    Log.e(TAG, e.getMessage());
                }

            }
        });
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
