package com.wulfcastle.missij;


import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.List;

public class FriendsFragment extends ListFragment {

    public static final String TAG = FriendsFragment.class.getSimpleName();

    protected List<ParseUser> mFriends;
    protected ParseRelation<ParseUser> mFriendsRelation; // Variable to declare friendship between two users
    protected ParseUser mCurrentUser; // Variable to get current user



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_friends, container, false);



        return rootView;
    }


    @Override
    public void onResume() { // onResume basically means on the resumption of the ab, i.e. when it is clicked on again
        super.onResume();

        mCurrentUser = ParseUser.getCurrentUser(); // Setting mCurrentUser to the current user (the user selected from ListView ---> The Edit Friends Page)

        // Getting the relation status of the current user & app user - down below :
        mFriendsRelation = mCurrentUser.getRelation(ParseConstants.KEY_FRIENDS_RELATION); // Parse.com automatically creates the String key "KEY_FRIENDS_RELATION" in the back-end of Parse.com

        ParseQuery<ParseUser> query = mFriendsRelation.getQuery();
        query.addAscendingOrder(ParseConstants.KEY_USERNAME);

        getActivity().setProgressBarIndeterminateVisibility(true);

         /* getActivitity(), gets the Activity in which the Fragment is being displayed.
         We can't set the "requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS)" here in the fragment as it's an Activity method, therefore we declare it in MainActivity
         (which is where the Friends Fragment is being displayed) and just set the visibility here */

        query.findInBackground(new FindCallback<ParseUser>() { // Get relation status in the background from Parse.com via query
            @Override
            public void done(List<ParseUser> friends, ParseException e) { // This method is executed when the query is complete, the name is self-explanatory :P

                getActivity().setProgressBarIndeterminateVisibility(false);

                if(e == null) {

                    mFriends = friends; // Setting mFriends based on the list of friends returned

                    String[] usernames = new String[mFriends.size()];
                    int i = 0;
                    for (ParseUser friend : mFriends) { // For each ParseUser "friend" in list of Friends :
                        usernames[i] = friend.getUsername(); // Add username of "friend" to String Array "usernames" at index [i]
                        i++; // Increase index by 1
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getListView().getContext(), android.R.layout.simple_list_item_1, usernames);
                    setListAdapter(adapter);

                } else {
                    Log.e(TAG, e.getMessage());
                    alert(e.getMessage());
                }
            }
        });

    }




    public AlertDialog alert(String error) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getListView().getContext()); // Creating Alert Dialog
        builder.setMessage(error);
        builder.setTitle(R.string.sign_up_error_titles);
        builder.setPositiveButton(android.R.string.ok, null);

        AlertDialog dialog = builder.create();
        return dialog;
    }



}
