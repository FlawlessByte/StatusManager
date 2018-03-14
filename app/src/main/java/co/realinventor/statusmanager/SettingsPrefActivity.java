package co.realinventor.statusmanager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;

public class SettingsPrefActivity extends AppCompatPreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_settings_pref);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // load settings fragment
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MainPreferenceFragment()).commit();

    }



    public static class MainPreferenceFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

        final String AUTODOWNLOAD_KEY = "key_auto_download";
        final String NOTIFICATION_KEY = "notifications_new_message";

        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_main);
        }


        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            Intent intent = new Intent(getActivity(), MyService.class);
            intent.putExtra("KEY1", "Value to be used by the service");

            if(key.equals(AUTODOWNLOAD_KEY)){
                Preference preference = findPreference(key);
                if(sharedPreferences.getBoolean(AUTODOWNLOAD_KEY, false)){
                    Log.d("AUto download","Enabled");
                    getActivity().startService(intent);

                }
                else{
                    Log.d("AUto download","Disabled");
                    if(sharedPreferences.getBoolean(NOTIFICATION_KEY, false)) {
                        getActivity().stopService(intent);
                        getActivity().startService(intent);
                    }
                    else{
                        getActivity().stopService(intent);
                    }
                }
            }
            if(key.equals(NOTIFICATION_KEY)){
                Preference preference = findPreference(key);
                if(sharedPreferences.getBoolean(NOTIFICATION_KEY, false)){
                    Log.d("Notifications","Enabled");
                    getActivity().startService(intent);
                }
                else{
                    Log.d("Notifications","Disabled");
                    //ThreadHandler.getNotificationThread().cancel();
                    if(sharedPreferences.getBoolean(AUTODOWNLOAD_KEY, false)) {
                        getActivity().stopService(intent);
                        getActivity().startService(intent);
                    }
                    else{
                        getActivity().stopService(intent);
                    }
                }
            }
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

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

}
