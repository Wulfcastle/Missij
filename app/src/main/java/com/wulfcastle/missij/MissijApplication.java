package com.wulfcastle.missij;

import android.app.Application;
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseObject;


public class MissijApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Parse.initialize(this, "AEOTJUxvyNkguDvFx8uNdHYXDzrGY8aIu2lHaOmS", "yf2heK3JjlR8uL8EOfyeYfBKHBp6ALY8hDSy7CDW");

        ParseObject testObject = new ParseObject("TestObject");
        testObject.put("foo", "bar");
        testObject.saveInBackground();

    }

}
