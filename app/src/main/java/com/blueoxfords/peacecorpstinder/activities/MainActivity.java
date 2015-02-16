package com.blueoxfords.peacecorpstinder.activities;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.blueoxfords.peacecorpstinder.R;
import com.blueoxfords.peacecorpstinder.fragments.MatchFragment;
import com.blueoxfords.peacecorpstinder.fragments.ProfileFragment;
import com.blueoxfords.peacecorpstinder.fragments.TinderFragment;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;


public class MainActivity extends FragmentActivity implements ActionBar.TabListener {

    AppSectionsPagerAdapter mAppSectionsPagerAdapter;

    ViewPager mViewPager;

    public static void start(Context c) {
        c.startActivity(new Intent(c, MainActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAppSectionsPagerAdapter = new AppSectionsPagerAdapter(this, getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mAppSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(2);


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

    public static class AppSectionsPagerAdapter extends FragmentPagerAdapter {

        Context context;

        public AppSectionsPagerAdapter(Context c, FragmentManager fm) {
            super(fm);
            context = c;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public Fragment getItem(int i) {
            switch (i) {
                case 0:
                    return TinderFragment.instantiate(context, TinderFragment.class.getName());

                case 1:
                    return MatchFragment.instantiate(context, MatchFragment.class.getName());

                case 2:
                    return ProfileFragment.instantiate(context, ProfileFragment.class.getName());

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
