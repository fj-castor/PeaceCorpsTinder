package com.blueoxfords.peacecorpstinder;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class MatchActivity extends FragmentActivity implements ActionBar.TabListener {

    AppSectionsPagerAdapter mAppSectionsPagerAdapter;

    ViewPager mViewPager;

    String matchId;

    ParseObject match;

    boolean personMatched;

    public static void start(Context c, String matchId) {
        c.startActivity(new Intent(c, MatchActivity.class).putExtra("matchId", matchId));
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putString("matchId", matchId);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) {
            matchId = savedInstanceState.getString("matchId");
        } else {
            matchId = getIntent().getStringExtra("matchId");
        }

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Match");
        try {
            match = query.get(matchId);
            personMatched = match.get("user1") != null && match.get("user2") != null;
        } catch (ParseException e) {
            e.printStackTrace();
            Toast.makeText(MatchActivity.this, "Something went wrong fetching the match", Toast.LENGTH_LONG).show();
            finish();
        }

        mAppSectionsPagerAdapter = new AppSectionsPagerAdapter(this, getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mAppSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(1);

        final ActionBar actionBar = getActionBar();

        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(false);
            actionBar.setTitle(getString(R.string.app_name));
            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

            mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
                @Override
                public void onPageSelected(int position) {
                    actionBar.setSelectedNavigationItem(position);
                }
            });

            for (int i = 0; i < mAppSectionsPagerAdapter.getCount(); i++) {
                actionBar.addTab(
                        actionBar.newTab()
                                .setIcon(mAppSectionsPagerAdapter.getPageIcon(i))
                                .setTabListener(this));
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (ParseUser.getCurrentUser() == null) {
            LoginActivity.start(this);
        }
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        mViewPager.setCurrentItem(tab.getPosition());
        if (getActionBar() != null) {
            getActionBar().setTitle(mAppSectionsPagerAdapter.getPageTitle(tab.getPosition()));
        }
        tab.getIcon().setColorFilter(getResources().getColor(R.color.red), PorterDuff.Mode.SRC_ATOP);
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
        tab.getIcon().setColorFilter(getResources().getColor(R.color.tab_icon_unselected_grey), PorterDuff.Mode.SRC_ATOP);
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
    }

    public class AppSectionsPagerAdapter extends FragmentPagerAdapter {

        Context context;

        public AppSectionsPagerAdapter(Context c, FragmentManager fm) {
            super(fm);
            context = c;
        }

        @Override
        public int getCount() {
            return personMatched ? 2 : 1;
        }

        @Override
        public Fragment getItem(int i) {
            switch (i) {
                case 0:
                    return OpeningDetailsFragment.newInstance(match.getString("opening"), matchId);

                case 1:
                    return new Fragment();

                default:
                    return null;
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return context.getResources().obtainTypedArray(R.array.tab_titles).getString(position);
        }

        public Drawable getPageIcon(int position) {
            Drawable icon = context.getResources().obtainTypedArray(R.array.tab_icons).getDrawable(position);
            icon.setColorFilter(context.getResources().getColor(R.color.tab_icon_unselected_grey), PorterDuff.Mode.SRC_ATOP);
            return icon;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.finishAuthentication(requestCode, resultCode, data);
    }
}
