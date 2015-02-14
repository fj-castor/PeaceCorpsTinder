package com.blueoxfords.peacecorpstinder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.model.GraphUser;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;

import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by soheil on 15-02-14.
 */
public class LoginActivity extends Activity {

    public static void start(Context context) {
        context.startActivity(new Intent(context, LoginActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (ParseUser.getCurrentUser() != null) {
            MainActivity.start(this);
        } else {
            Button signIn = (Button) findViewById(R.id.signInButton);
            signIn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    login();
                }
            });
        }
    }

    public void login() {
        ParseFacebookUtils.logIn(Arrays.asList(ParseFacebookUtils.Permissions.Friends.ABOUT_ME), this, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException err) {
                if (user == null) {
                    Log.d("com.amplifyme", "The user cancelled the Facebook login.");
                } else {
                    if (user.isNew()) {
                        updateProfile(user);
                    }
                    MainActivity.start(LoginActivity.this);
                }
            }
        });
    }

    public void updateProfile(final ParseUser parseUser) {
        Request.newMeRequest(ParseFacebookUtils.getSession(), new Request.GraphUserCallback() {
            @Override
            public void onCompleted(GraphUser user, Response response) {
                if (user != null) {
                    parseUser.put(Constants.ParseColumns.FB_ID, user.getId());
                    parseUser.put(Constants.ParseColumns.NICKNAME, user.getName());
                    parseUser.put(Constants.ParseColumns.FIRST_NAME, user.getFirstName());
                    parseUser.put(Constants.ParseColumns.LAST_NAME, user.getLastName());
                    parseUser.put(Constants.ParseColumns.GENDER, user.getProperty(Constants.Facebook.GENDER));
                    parseUser.put(Constants.ParseColumns.PICTURE_URL, String.format(Constants.Facebook.PROFILE_PICTURE_URL, user.getId()));
                    parseUser.saveInBackground();
                }
            }
        }).executeAsync();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.finishAuthentication(requestCode, resultCode, data);
    }
}
