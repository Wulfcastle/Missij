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
import android.widget.TextView;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;


public class LoginActivity extends Activity {


    protected EditText mUsername;
    protected EditText mPassword;
    protected Button btnLogin;
    protected TextView mSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

                    ParseUser.logInInBackground(username, password, new LogInCallback() {
                        public void done(ParseUser user, ParseException e) {
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.login, menu);
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
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this); // Creating Alert Dialog
        builder.setMessage(error);
        builder.setTitle(R.string.login_error_titles);
        builder.setPositiveButton(android.R.string.ok, null);

        AlertDialog dialog = builder.create();
        return dialog;
    }


}
