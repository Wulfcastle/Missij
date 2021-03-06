package com.wulfcastle.missij;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
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
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
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

                   alert(getString(R.string.sign_up_error_message)).show();


                } else {

                    //Creating new user using Parse.com backend
                    setProgressBarIndeterminateVisibility(true); // Showing Progress Bar

                    ParseUser newUser = new ParseUser();
                    newUser.setUsername(username);
                    newUser.setPassword(password);
                    newUser.setEmail(email);

                    newUser.signUpInBackground(new SignUpCallback() {

                        @Override
                        public void done(ParseException e) {
                            setProgressBarIndeterminateVisibility(false); // Close Progress Bar

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







    public AlertDialog alert(String error) {
        AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this); // Creating Alert Dialog
        builder.setMessage(error);
        builder.setTitle(R.string.sign_up_error_titles);
        builder.setPositiveButton(android.R.string.ok, null);

        AlertDialog dialog = builder.create();
        return dialog;
    }



}
