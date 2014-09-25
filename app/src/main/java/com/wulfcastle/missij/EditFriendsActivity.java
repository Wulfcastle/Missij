package com.wulfcastle.missij;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.ArrayAdapter;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;


public class EditFriendsActivity extends ListActivity {

    public static final String TAG = EditFriendsActivity.class.getSimpleName();
    protected List<ParseUser> mUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_edit_friends);
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

        setProgressBarIndeterminateVisibility(true); // Opening Progress Bar

        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.orderByAscending(ParseConstants.KEY_USERNAME);
        query.setLimit(1000);
        query.findInBackground(new FindCallback<ParseUser>() {

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
                        i++;
                    }

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


    public AlertDialog alert(String error) {
        AlertDialog.Builder builder = new AlertDialog.Builder(EditFriendsActivity.this); // Creating Alert Dialog
        builder.setMessage(error);
        builder.setTitle(R.string.sign_up_error_titles);
        builder.setPositiveButton(android.R.string.ok, null);

        AlertDialog dialog = builder.create();
        return dialog;
    }



}
