package com.blueoxfords.peacecorpstinder;

import android.app.Application;

import com.parse.Parse;

/**
 * Created by soheil on 15-02-14.
 */
public class PeaceCorpsApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);

        Parse.initialize(this, "uZWysVCW7KeVJLU1MHQQsiQlFWQGTCjTmjkc9jTH", "iRpctT8yg8ZJfUEAfVVinYDqdnaWjQ9cNn18sMoG");
    }
}
