package com.wulfcastle.missij;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


import android.app.ActionBar;


import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;


import com.parse.ParseAnalytics;
import com.parse.ParseUser;


public class MainActivity extends FragmentActivity implements ActionBar.TabListener {


    SectionsPagerAdapter mSectionsPagerAdapter;
    /*
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    public static final String TAG = MainActivity.class.getSimpleName();

    public static final int TAKE_PHOTO_REQUEST = 0;
    public static final int TAKE_VIDEO_REQUEST = 1;
    public static final int CHOOSE_PHOTO_REQUEST = 2;
    public static final int CHOOSE_VIDEO_REQUEST = 3;

    public static final int MEDIA_TYPE_IMAGE = 4;
    public static final int MEDIA_TYPE_VIDEO = 5;

    private Uri fileUri;

    protected DialogInterface.OnClickListener mDialogListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int which) { // int "which" is the number of the String Array in the dialog which is clicked on

            switch(which) {
                case 0: // Take Picture Option
                    Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); // Opening new app to capture image
                    fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE); // Create a file to save the image
                    saveImage(takePhotoIntent);
                    break;

                case 1: // Take Video Option

                    Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                    fileUri = getOutputMediaFileUri(MEDIA_TYPE_VIDEO);
                    saveVideo(takeVideoIntent);
                    break;

                case 2: // Choose Picture Option
                    break;

                case 3: // Chose Video Option
                    break;
            }

        }
    };

    private Uri getOutputMediaFileUri(int mediaType) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        if (isExternalStorageAvailable() == true) {
            // Get the URI

            // 1. Get the External Storage Directory

            String appName = MainActivity.this.getString(R.string.app_name);
            File mediaStorageDirectory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), appName); // Creating a sub-directory from our app name

            // 2. Create our apps sub-directory if there isn't already one

            if (! mediaStorageDirectory.exists()) {
                if (! mediaStorageDirectory.mkdirs()); // Make the directory ---> mkdirs() returns a boolean value based on whether it was executed successfully or not.
                Log.e(TAG, getString(R.string.directory_creation_error));
                return null;
            }
            // 3. Create a file name


            // 4. Create the file

            File mediaFile;
            Date now = new Date(); // Getting date and time, to create timestamp when file is saved in case the file is saved over an existing file
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(now); // Formatting date into a String timestamp

            String path = mediaStorageDirectory.getPath() + File.separator;

            if (mediaType == MEDIA_TYPE_IMAGE) {
                mediaFile = new File(path + "IMG_" + timestamp + ".jpg"); // Format for saving Image file

            } else if (mediaType == MEDIA_TYPE_VIDEO) {
                mediaFile = new File(path + "VID_" + timestamp + ".mp4"); // Format for saving Image file
            } else {
                return null;
            }

            Log.d(TAG, "File: " + Uri.fromFile(mediaFile));


            // 5. Return the file's URI

            return Uri.fromFile(mediaFile);

        } else {
            return null;
        }
    }

    protected void saveImage(Intent intent) {
        if (fileUri == null) {
            // Display Error
            Toast.makeText(MainActivity.this, R.string.storage_error, Toast.LENGTH_LONG);
        } else {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // Set the image file name
            startActivityForResult(intent, TAKE_PHOTO_REQUEST); // startActivityForResult means that the activity should start an external app, use it ,and return a result
        }
    }

    protected void saveVideo(Intent intent) {
        if (fileUri == null) {
            // Display Error
            Toast.makeText(MainActivity.this, R.string.storage_error, Toast.LENGTH_LONG);
        } else {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // Set the video file name
            intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 10); // Set the max video length
            intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0); // Set the video quality to low
            startActivityForResult(intent, TAKE_VIDEO_REQUEST); // startActivityForResult means that the activity should start an external app, use it ,and return a result
        }
    }

    private boolean isExternalStorageAvailable() {
        String state = Environment.getExternalStorageState();  // Tells us which state external storage is in

        if (state.equals(Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_main);
        ParseAnalytics.trackAppOpened(getIntent());

        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {

            Log.i(TAG, currentUser.getUsername());

        } else {
            navigateToLogin();
        }



        // Set up the action bar.
        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);


        // Create the adapter that will return a fragment for each of the three
        // primary sections of the app.
        mSectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            // add to the gallery
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            mediaScanIntent.setData(fileUri);
            sendBroadcast(mediaScanIntent);

        } else if (resultCode != RESULT_CANCELED) {
            Toast.makeText(this, R.string.general_error, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int itemId = item.getItemId(); // Get the id of Menu Item that was clicked on

        switch (itemId) {

            case R.id.action_logout:

                ParseUser.logOut(); // Logout current user
                ParseUser currentUser = ParseUser.getCurrentUser(); // this will now be null
                navigateToLogin();

            case R.id.action_edit_friends:

                Intent intent = new Intent(this, EditFriendsActivity.class);
                startActivity(intent);

            case R.id.action_camera:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setItems(R.array.camera_choices, mDialogListener);
                AlertDialog dialog = builder.create();
                dialog.show();

            }


            return super.onOptionsItemSelected(item);
        }


    private void navigateToLogin() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // Creating new task for Login page (LoginActivity)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clearing Inbox (MainActivity)
        startActivity(intent);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }




}
