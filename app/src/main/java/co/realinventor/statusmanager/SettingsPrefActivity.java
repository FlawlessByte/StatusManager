package co.realinventor.statusmanager;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import helpers.MediaFiles;

public class SettingsPrefActivity extends AppCompatPreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setTitle(getResources().getString(R.string.settings));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // load settings fragment
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MainPreferenceFragment()).commit();

    }



    public static class MainPreferenceFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener{

        final String AUTODOWNLOAD_KEY = "key_auto_download";
        final String NOTIFICATION_KEY = "notifications_new_message";
        final String FEEDBACK_KEY = "key_feedback";
        final String RATE_APP_KEY = "key_rate_app";
        final String REWARDED_VIDEO_KEY = "key_video_ad";
        final String FREE_WHATSAPP_FOLD_KEY = "key_free_whatsapp_fold";
        final String PRIVACY_POLICY_KEY = "key_privacy";
        final String SHARE_KEY = "key_share";
        final String SECRET_KEY = "key_secret";
        private int secretTapCount = 0;

        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_main);

//            // Use an activity context to get the rewarded video instance.
//            mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(getActivity());
//            mRewardedVideoAd.setRewardedVideoAdListener(this);
//
//            mRewardedVideoAd.loadAd("ca-app-pub-4525583199746587/6934978941",
//                    new AdRequest.Builder()
////                            .addTestDevice("750C63CE8C1A0106CF1A8A4C5784DC17")
//                            .build());

            Preference feedPref = findPreference(FEEDBACK_KEY);
            Preference ratePref = findPreference(RATE_APP_KEY);
//            Preference rewardPref = findPreference(REWARDED_VIDEO_KEY);
            Preference freePref = findPreference(FREE_WHATSAPP_FOLD_KEY);
            Preference sharePref = findPreference(SHARE_KEY);
            Preference privacyPolicyKey = findPreference(PRIVACY_POLICY_KEY);
            Preference versionPref = findPreference("key_version");

            versionPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Log.i("VersionPref", "clicked");
                    if(++secretTapCount == 10){
                        // Unlock NoAds, and Toast
                        String MSG = "You have unlocked the Ad free experience! Thanks for being the my most favourite customer!";
                        SharedPreferences sharedPref = getActivity().getSharedPreferences("APP_DEFAULTS", Context.MODE_PRIVATE);
                        //Change the value to true
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putBoolean("NoAdsUnlocked", true);
                        editor.apply();
                        Toast.makeText(getActivity(), MSG, Toast.LENGTH_LONG).show();
                    }
                    else if(secretTapCount > 6 && secretTapCount < 10){
                        String MSG = "You are " + (10 - secretTapCount) + " steps away from unlocking Ad free XP!";
                        Toast.makeText(getActivity(), MSG, Toast.LENGTH_SHORT).show();
                    }
                    Log.i("VersionPref", ""+secretTapCount);
                    return false;
                }
            });

            privacyPolicyKey.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Log.d("Pref", "privacy pref");
                    getActivity().startActivity(new Intent("android.intent.action.VIEW", Uri.parse("https://realinventor.github.io/StatusSaver/privacy.html")));
                    return false;
                }
            });
            sharePref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Log.d("Pref", "share pref");
                    shareApplication(getActivity());
                    return false;
                }
            });

            feedPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Log.d("Pref", "Feedback");
                    sendFeedback(getActivity());
                    return false;
                }
            });

            ratePref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Log.d("Pref", "Rate");
                    rateApp(getActivity());
                    return false;
                }
            });
//            rewardPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
//                @Override
//                public boolean onPreferenceClick(Preference preference) {
//                    Log.d("Pref", "Rewarded video");
//                    if (mRewardedVideoAd.isLoaded()) {
//                        mRewardedVideoAd.show();
//                    }
//                    else {
//                        Log.d("Pref", "Rewarded video not loaded");
//                        Toast.makeText(getActivity(),getResources().getString(R.string.video_not_loaded),Toast.LENGTH_SHORT).show();
//                    }
//                    return false;
//                }
//            });

            freePref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Log.d("Pref", "Free whatsapp fold clicked");
                    float freed_size = 0;
                    try {
                        freed_size = MediaFiles.removeExpired();
                    }
                    catch (Exception e){
                        Toast.makeText(getActivity(),getResources().getString(R.string.unsuccessful), Toast.LENGTH_SHORT).show();
                    }
                    if(freed_size != 0){
                        Toast.makeText(getActivity(), getResources().getString(R.string.u_freed_data,String.format("%.02f",freed_size)), Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(getActivity(), getResources().getString(R.string.no_expired_files),Toast.LENGTH_SHORT).show();
                    }

                    return false;
                }
            });
        }


        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
//            Intent intent = new Intent(getActivity(), MyService.class);
//            intent.putExtra("KEY1", "Value to be used by the service");
//
//            if(key.equals(AUTODOWNLOAD_KEY)){
//                Preference preference = findPreference(key);
//                if(sharedPreferences.getBoolean(AUTODOWNLOAD_KEY, false)){
//                    Log.d("AUto download","Enabled");
//                    getActivity().startService(intent);
//
//                }
//                else{
//                    Log.d("AUto download","Disabled");
//                    if(sharedPreferences.getBoolean(NOTIFICATION_KEY, false)) {
//                        getActivity().stopService(intent);
//                        getActivity().startService(intent);
//                    }
//                    else{
//                        getActivity().stopService(intent);
//                    }
//                }
//            }
//            if(key.equals(NOTIFICATION_KEY)){
//                Preference preference = findPreference(key);
//                if(sharedPreferences.getBoolean(NOTIFICATION_KEY, false)){
//                    Log.d("Notifications","Enabled");
//                    getActivity().startService(intent);
//                }
//                else{
//                    Log.d("Notifications","Disabled");
//                    if(sharedPreferences.getBoolean(AUTODOWNLOAD_KEY, false)) {
//                        getActivity().stopService(intent);
//                        getActivity().startService(intent);
//                    }
//                    else{
//                        getActivity().stopService(intent);
//                    }
//                }
//            }
        }

        @Override
        public void onResume() {
            super.onResume();
            getPreferenceScreen().getSharedPreferences()
                    .registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onPause() {
            super.onPause();
            getPreferenceScreen().getSharedPreferences()
                    .unregisterOnSharedPreferenceChangeListener(this);
        }


//        @Override
//        public void onRewarded(RewardItem reward) {
//            Toast.makeText(getActivity(),getResources().getString(R.string.thank_you),Toast.LENGTH_SHORT).show();
//            // Reward the user.
//        }
//
//        @Override
//        public void onRewardedVideoAdLeftApplication() {}
//
//        @Override
//        public void onRewardedVideoAdClosed() {}

//        @Override
//        public void onRewardedVideoAdFailedToLoad(int errorCode) {
//            Toast.makeText(getActivity(),getResources().getString(R.string.sorry_some_error),Toast.LENGTH_SHORT).show();
//        }
//
//        @Override
//        public void onRewardedVideoAdLoaded() {}
//
//        @Override
//        public void onRewardedVideoAdOpened() {}
//
//        @Override
//        public void onRewardedVideoStarted() {}
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }


    public static void sendFeedback(Context context) {
        String body = null;
        try {
            body = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
            body = "\n\n-----------------------------\nPlease don't remove this information\n Device OS: Android \n Device OS version: " +
                    Build.VERSION.RELEASE + "\n App Version: 1.1" + body + "\n Device Brand: " + Build.BRAND +
                    "\n Device Model: " + Build.MODEL + "\n Device Manufacturer: " + Build.MANUFACTURER;
        } catch (PackageManager.NameNotFoundException e) {
        }
        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", "jimmyjose.mec@gmail.com", null));
//        intent.setType("message/rfc822");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"jimmyjose.mec@gmail.com"});
        intent.putExtra(Intent.EXTRA_SUBJECT, "Feedback from android app");
        intent.putExtra(Intent.EXTRA_TEXT, body);
        context.startActivity(Intent.createChooser(intent, context.getResources().getString(R.string.choose_email_client)));
    }


    public static void rateApp(Context context){
        Uri uri = Uri.parse("market://details?id=" + context.getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        // To count with Play market backstack, After pressing back button,
        // to taken back to our application, we need to add following flags to intent.
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            context.startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            context.startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + context.getPackageName())));
        }
    }

    public static void shareApplication(Context context) {
        Intent sharingIntent = new Intent("android.intent.action.SEND");
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra("android.intent.extra.SUBJECT", "Status Saver for WhatsApp");
        sharingIntent.putExtra("android.intent.extra.TEXT", "Status Saver for WhatsApp- \nPreview, save, & share instantly! \n\nClick on the link to download..\n\nhttp://play.google.com/store/apps/details?id=co.realinventor.statusmanager");
        context.startActivity(Intent.createChooser(sharingIntent, context.getResources().getString(R.string.share_using)));
    }

}
