package co.realinventor.statusmanager;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import co.realinventor.statusmanager.pageradapters.*;
import helpers.MediaFiles;
import helpers.ViewPagerFixed;

public class ViewActivity extends AppCompatActivity {
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPagerFixed mViewPager;
    private AdView mAdView;
    private InterstitialAd mInterstitialAd;
    private boolean noAdsUnlocked;
    private final String TAG = "ViewActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);

        SharedPreferences sharedPref = getSharedPreferences("APP_DEFAULTS", Context.MODE_PRIVATE);
        noAdsUnlocked = sharedPref.getBoolean("NoAdsUnlocked", false);
        Log.i(TAG, "NoAdsUnlocked: "+noAdsUnlocked);

        if(!noAdsUnlocked) {
            //Banner ad
            mAdView = findViewById(R.id.adView);
            AdRequest adRequest = new AdRequest.Builder()
//                .addTestDevice("750C63CE8C1A0106CF1A8A4C5784DC17")
                    .build();
            mAdView.loadAd(adRequest);
        }

        //Interstitial ad
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-4525583199746587/1749666103");
        mInterstitialAd.loadAd(new AdRequest.Builder()
//                .addTestDevice("750C63CE8C1A0106CF1A8A4C5784DC17")
                .build());

        //Init media files
        MediaFiles.initMediaFiles();
        MediaFiles.initAppDirectories();
        MediaFiles.initSavedFiles();

        //Title code to handle intent from download tab
        String intent_title = getIntent().getStringExtra("title");

        //Tool bar thing
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPagerFixed) findViewById(R.id.container);
        mViewPager.setOffscreenPageLimit(1);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);


        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        if (intent_title == null) {
            Log.e("intent tiltle", "null");
        } else if (intent_title.equals("images")) {
            mViewPager.setCurrentItem(0);
        } else if (intent_title.equals("videos")) {
            mViewPager.setCurrentItem(1);
        } else if (intent_title.equals("downloads")) {
            mViewPager.setCurrentItem(2);
        } else if (intent_title.equals("favs")) {
            mViewPager.setCurrentItem(3);
        }
    }

    //Menu thing
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(ViewActivity.this, SettingsPrefActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        Log.d("ViewActivity", "Back button pressed");

        new AlertDialog.Builder(this, R.style.AlertDialogCustom)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(getResources().getString(R.string.exiting_app))
                .setMessage(getResources().getString(R.string.are_you_sure_exit))
                .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
//                        finish();
                        if (mInterstitialAd.isLoaded() && !noAdsUnlocked) {
                            mInterstitialAd.show();
                            mInterstitialAd.setAdListener(new AdListener() {
                                @Override
                                public void onAdLoaded() {
                                    // Code to be executed when an ad finishes loading.
                                    Log.i("Interstitial ad", "Loaded");
                                }

                                @Override
                                public void onAdFailedToLoad(int errorCode) {
                                    // Code to be executed when an ad request fails.
                                    Log.e(TAG, "Failed to load Ad: " +errorCode);
                                    finish();
                                }

                                @Override
                                public void onAdOpened() {
                                    // Code to be executed when the ad is displayed.
                                    Log.i("Interstitial ad", "Ad opened");
                                }

                                @Override
                                public void onAdLeftApplication() {
                                    // Code to be executed when the user has left the app.
                                    Log.i("Interstitial ad", "User left app");
                                    finish();
                                }

                                @Override
                                public void onAdClosed() {
                                    // Code to be executed when when the interstitial ad is closed.
                                    Log.i("Interstitial ad", "Ad closed");
                                    finish();
                                }
                            });


                        } else {
                            Log.d("TAG", "The interstitial wasn't loaded yet.");
                            finish();
                        }

                    }

                })
                .setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                })
                .show();
    }
}
