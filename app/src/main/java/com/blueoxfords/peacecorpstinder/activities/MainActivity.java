package com.blueoxfords.peacecorpstinder.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.blueoxfords.ImageRestClient;
import com.blueoxfords.ImageService;
import com.blueoxfords.peacecorpstinder.R;
import com.blueoxfords.peacecorpstinder.fragments.MatchFragment;
import com.blueoxfords.peacecorpstinder.fragments.ProfileFragment;
import com.blueoxfords.peacecorpstinder.fragments.TinderFragment;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class MainActivity extends FragmentActivity implements ActionBar.TabListener {

    AppSectionsPagerAdapter mAppSectionsPagerAdapter;

    ViewPager mViewPager;
    public static Activity activity;

    public static void start(Context c) {
        c.startActivity(new Intent(c, MainActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activity = this;

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

    public void getLegalInfo(View v) {
        String photoId = v.getTag() + "";
        ImageRestClient.get().getInfoFromImageId(photoId, new Callback<ImageService.ImageInfoWrapper>() {
            @Override
            public void success(ImageService.ImageInfoWrapper imageInfoWrapper, Response response) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.activity);

                ScrollView wrapper = new ScrollView(MainActivity.activity);
                LinearLayout infoLayout = new LinearLayout(MainActivity.activity);
                infoLayout.setOrientation(LinearLayout.VERTICAL);
                infoLayout.setPadding(35, 35, 35, 35);

                TextView imageOwner = new TextView(MainActivity.activity);
                imageOwner.setText(Html.fromHtml("<b>Image By: </b>" + imageInfoWrapper.photo.owner.username));
                if (imageInfoWrapper.photo.owner.realname.length() > 0) {
                    imageOwner.setText(imageOwner.getText() + " (" + imageInfoWrapper.photo.owner.realname + ")");
                }
                infoLayout.addView(imageOwner);

                if (getLicenseUrl(Integer.parseInt(imageInfoWrapper.photo.license)).length() > 0) {
                    TextView licenseLink = new TextView(MainActivity.activity);
                    licenseLink.setText(Html.fromHtml("<a href=\"" + getLicenseUrl(Integer.parseInt(imageInfoWrapper.photo.license)) + "\"><b>Licensing</b></a>"));
                    licenseLink.setMovementMethod(LinkMovementMethod.getInstance());
                    infoLayout.addView(licenseLink);
                }

                if (imageInfoWrapper.photo.urls.url.size() > 0) {
                    TextView imageLink = new TextView(MainActivity.activity);
                    imageLink.setText(Html.fromHtml("<a href=\"" + imageInfoWrapper.photo.urls.url.get(0)._content + "\"><b>Image Link</b></a>"));
                    imageLink.setMovementMethod(LinkMovementMethod.getInstance());
                    infoLayout.addView(imageLink);
                }

                if (imageInfoWrapper.photo.title._content.length() > 0) {
                    TextView photoTitle = new TextView(MainActivity.activity);
                    photoTitle.setText(Html.fromHtml("<b>Image Title: </b>" + imageInfoWrapper.photo.title._content));
                    infoLayout.addView(photoTitle);
                }

                if (imageInfoWrapper.photo.description._content.length() > 0) {
                    TextView description = new TextView(MainActivity.activity);
                    description.setText(Html.fromHtml("<b>Image Description: </b>" + imageInfoWrapper.photo.description._content));
                    infoLayout.addView(description);
                }

                TextView contact = new TextView(MainActivity.activity);
                contact.setText(Html.fromHtml("<br><i>To remove this photo, please email peacecorpstinder@gmail.com</i>"));
                infoLayout.addView(contact);

                wrapper.addView(infoLayout);

                builder.setTitle("Photo Information");
                builder.setPositiveButton("Close", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
                builder.setView(wrapper);
                builder.create().show();
            }

            @Override
            public void failure(RetrofitError error) {
                Log.i("testing", "could not retrieve legal/attribution info");
            }
        });
    }

    public static String getLicenseUrl(int id) {
        switch (id) {
            case 1:
                return "http://creativecommons.org/licenses/by-nc-sa/2.0/legalcode";
            case 2:
                return "http://creativecommons.org/licenses/by-nc-nd/2.0/legalcode";
            case 3:
                return "http://creativecommons.org/licenses/by-nc-nd/2.0/legalcode";
            case 4:
                return "http://creativecommons.org/licenses/by/2.0/legalcode";
            case 5:
                return "http://creativecommons.org/licenses/by-sa/2.0/legalcode";
            case 6:
                return "http://creativecommons.org/licenses/by-nd/2.0/legalcode";
            default:
                return "";
        }
    }
}
