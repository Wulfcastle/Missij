package com.wulfcastle.missij;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class RecipientsActivity extends ListActivity {

    public static final String TAG = RecipientsActivity.class.getSimpleName();
    protected List<ParseUser> mFriends;
    protected ParseRelation<ParseUser> mFriendsRelation; // Variable to declare friendship between two users
    protected ParseUser mCurrentUser; // Variable to get current user

    protected MenuItem mSend; // We use this to set the "Send" menu item as visible by referencing it


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_recipients);
        getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE); // Gets default list view associated with this activity and set's the Choice Mode for the List View allow multiple choices
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.recipients, menu);
        mSend = menu.getItem(0); // Setting mSend variable to the correct menu item
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

       switch (id) {
           case android.R.id.home:
               NavUtils.navigateUpFromSameTask(this);
               return true;

           case R.id.action_send:
               ParseObject message = createMessage();
               //sendMessage(message);
               return true;
       }

        return super.onOptionsItemSelected(item);

    }


    protected ParseObject createMessage() { // Message constructor for storing messages in Parse.com
        ParseObject message = new ParseObject(ParseConstants.CLASS_MESSAGES); // Creating a new message in "Messages" class/database in Parse.com
        message.put(ParseConstants.KEY_SENDER_ID, ParseUser.getCurrentUser().getObjectId()); // Assigning a Sender ID to the Message
        message.put(ParseConstants.KEY_SENDER_NAME, ParseUser.getCurrentUser().getUsername()); // Assigning the Username of the sender to the Messgage
        message.put(ParseConstants.KEY_RECEPIENTS_IDS, getRecipientIDs()); // Adding the Recipient ID's to the Message

        return message;
    }



    private void sendMessage(ParseObject message) {

    }

    @Override
    public void onResume() { // onResume basically means on the resumption of the ab, i.e. when it is clicked on again
        super.onResume();

        mCurrentUser = ParseUser.getCurrentUser(); // Setting mCurrentUser to the current user (the user selected from ListView ---> The Edit Friends Page)

        // Getting the relation status of the current user & app user - down below :
        mFriendsRelation = mCurrentUser.getRelation(ParseConstants.KEY_FRIENDS_RELATION); // Parse.com automatically creates the String key "KEY_FRIENDS_RELATION" in the back-end of Parse.com

        ParseQuery<ParseUser> query = mFriendsRelation.getQuery();
        query.addAscendingOrder(ParseConstants.KEY_USERNAME);

        setProgressBarIndeterminateVisibility(true);

         /* getActivitity(), gets the Activity in which the Fragment is being displayed.
         We can't set the "requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS)" here in the fragment as it's an Activity method, therefore we declare it in MainActivity
         (which is where the Friends Fragment is being displayed) and just set the visibility here */

        query.findInBackground(new FindCallback<ParseUser>() { // Get relation status in the background from Parse.com via query
            @Override
            public void done(List<ParseUser> friends, ParseException e) { // This method is executed when the query is complete, the name is self-explanatory :P

                setProgressBarIndeterminateVisibility(false);

                if(e == null) {

                    mFriends = friends; // Setting mFriends based on the list of friends returned

                    String[] usernames = new String[mFriends.size()];
                    int i = 0;
                    for (ParseUser friend : mFriends) { // For each ParseUser "friend" in list of Friends :
                        usernames[i] = friend.getUsername(); // Add username of "friend" to String Array "usernames" at index [i]
                        i++; // Increase index by 1
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getListView().getContext(), android.R.layout.simple_list_item_multiple_choice, usernames);
                    setListAdapter(adapter);

                } else {
                    Log.e(TAG, e.getMessage());
                    alert(e.getMessage());
                }
            }
        });

    }

    protected ArrayList<String> getRecipientIDs() {
        ArrayList<String> recipientIDs = new ArrayList<String>();

        for (int i=0; i < getListView().getCount(); i++) {  // Looping through whole list
            if (getListView().isItemChecked(i)) { // Checking to see if item [i] has been checked
                // Add friend to recipient list
                recipientIDs.add(mFriends.get(i).getObjectId()); // Adding ObjectID of current friend in the loop to recipientIDs
            }
        }

        return recipientIDs;
    }


    public AlertDialog alert(String error) {
        AlertDialog.Builder builder = new AlertDialog.Builder(RecipientsActivity.this); // Creating Alert Dialog
        builder.setMessage(error);
        builder.setTitle(R.string.sign_up_error_titles);
        builder.setPositiveButton(android.R.string.ok, null);

        AlertDialog dialog = builder.create();
        return dialog;
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        if (l.getCheckedItemCount() > 0) { // Get's the number of items checked in the list (l), see above ^^
            mSend.setVisible(true);


        } else {
            mSend.setVisible(false); // Making the "Send" button visible when a user clicks on at least one recipient
        }
    }
}
