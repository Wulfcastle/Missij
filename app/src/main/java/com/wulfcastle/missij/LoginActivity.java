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
import android.widget.TextView;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;


public class LoginActivity extends Activity {


    protected EditText mUsername;
    protected EditText mPassword;
    protected Button btnLogin;
    protected TextView mSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_login);

        mUsername = (EditText) findViewById(R.id.usernameField);
        mPassword = (EditText) findViewById(R.id.passwordField);
        btnLogin  = (Button) findViewById(R.id.btnLogin);


        btnLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                String username = mUsername.getText().toString();
                String password = mPassword.getText().toString();
                username = username.trim();  // Trimming whitespaces
                password = password.trim();

                if (username.isEmpty() || password.isEmpty()) {
                    alert(getString(R.string.login_error_message)).show();

                } else {

                    // Login

                    setProgressBarIndeterminateVisibility(true); // Showing Progress Bar

                    ParseUser.logInInBackground(username, password, new LogInCallback() {
                        public void done(ParseUser user, ParseException e) {

                            setProgressBarIndeterminateVisibility(false); // Closing Progress Bar

                            if (e == null) { // You could also use ---> if (user != null)
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class); // Switch to User's Inbox
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // Creating new task for Inbox (MainActivity)
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clearing Login page (LoginActivity)
                                startActivity(intent);
                            } else {
                                alert(e.getMessage()).show();
                            }
                        }
                    });

                }
            }
        });






        mSignUp = (TextView) findViewById(R.id.txtSignUp); // Change to SignUpActivity on click of sign up text
        mSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);

            }
        });


    }



    public AlertDialog alert(String error) {
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this); // Creating Alert Dialog
        builder.setMessage(error);
        builder.setTitle(R.string.login_error_titles);
        builder.setPositiveButton(android.R.string.ok, null);

        AlertDialog dialog = builder.create();
        return dialog;
    }


}
