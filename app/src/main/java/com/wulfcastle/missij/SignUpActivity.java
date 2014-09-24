package com.wulfcastle.missij;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;


public class SignUpActivity extends Activity {

    protected EditText mUsername;
    protected EditText mPassword;
    protected EditText mEmail;
    protected Button btnSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mUsername = (EditText) findViewById(R.id.txtUsername);
        mPassword = (EditText) findViewById(R.id.txtPassword);
        mEmail = (EditText) findViewById(R.id.txtEmail);
        btnSignUp = (Button) findViewById(R.id.btnSignUp);

        btnSignUp.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                String username = mUsername.getText().toString();
                String password = mPassword.getText().toString();
                String email = mEmail.getText().toString();

                username = username.trim();  // Trimming whitespaces
                password = password.trim();
                email = email.trim();

                if (username.isEmpty() || password.isEmpty() || email.isEmpty()) {
                   alert(String.valueOf(R.string.sign_up_error_message)).show();
                } else {

                    //Creating new user using Parse.com backend

                    ParseUser newUser = new ParseUser();
                    newUser.setUsername(username);
                    newUser.setPassword(password);
                    newUser.setEmail(email);

                    newUser.signUpInBackground(new SignUpCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null ) {

                                // Then we know that the sign-up process was a success
                                Intent intent = new Intent(SignUpActivity.this, MainActivity.class); // Taking user to the inbox --->
                                // SignUpActivity.this is the context, where the app is currently at and MainActiviy.class is where the app needs to go

                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // Creating new task for Inbox (MainActivity)
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clearing Sign Up page (SignUpActivity)

                                startActivity(intent);

                            } else {
                                alert(e.getMessage()).show();

                            }
                        }
                    });



                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.sign_up, menu);
        return true;
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
        AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this); // Creating Alert Dialog
        builder.setMessage(error);
        builder.setTitle(R.string.sign_up_error_titles);
        builder.setPositiveButton(android.R.string.ok, null);

        AlertDialog dialog = builder.create();
        return dialog;
    }



}
