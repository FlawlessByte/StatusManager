package co.realinventor.statusmanager;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import helpers.Favourites;
import helpers.MediaFiles;
import com.google.android.gms.ads.MobileAds;


public class IntroActivity extends AppCompatActivity {

    private TextView textView;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        SharedPreferences shared = getSharedPreferences("APP_DEFAULTS", Context.MODE_PRIVATE);
//        MediaFiles.WHATSAPP_STATUS_FOLDER_PATH = shared.getString("WHATSAPP_STATUS_FOLD_PATH",Environment.getExternalStorageDirectory()+"/Whatsapp/Media/.Statuses/");
        MediaFiles.initWhatsAppDirPath();

        //Admob initialization
        MobileAds.initialize(this, "ca-app-pub-4525583199746587~9357637931");

        textView = (TextView)findViewById(R.id.textView);
        button = (Button)findViewById(R.id.buttonGone);

        checkIfPermissionGranted();
    }



    public void checkIfPermissionGranted(){

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // only for Marshmallow and newer versions
            Log.d("Version check","Marshmallow or above detected");

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted
                Log.d("Permission check", "WRITE_EXTERNAL_STORAGE is not granted");

                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                    // Show an explanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.

                    //Show Information about why you need the permission
                    AlertDialog.Builder builder = new AlertDialog.Builder(IntroActivity.this, R.style.AlertDialogCustom);
                    builder.setTitle(getResources().getString(R.string.permission_title));
                    builder.setMessage(getResources().getString(R.string.permission_summary));
                    builder.setPositiveButton(getResources().getString(R.string.permission_grant), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            ActivityCompat.requestPermissions(IntroActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1000);
                        }
                    });
                    builder.setNegativeButton(getResources().getString(R.string.permission_cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();

                            textView.setText(getResources().getString(R.string.permission_try_again));
                            button.setVisibility(View.VISIBLE);
                            button.setText(getResources().getString(R.string.try_again));
                            button.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    button.setVisibility(View.GONE);
                                    checkIfPermissionGranted();
                                }
                            });

                        }
                    });
                    builder.show();

                }
                else {

                    // No explanation needed; request the permission
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            1000);

                    if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED){
                        textView.setText(getResources().getString(R.string.permission_try_again));
                        button.setVisibility(View.VISIBLE);
                        button.setText(getResources().getString(R.string.try_again));
                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                button.setVisibility(View.GONE);
                                checkIfPermissionGranted();
                            }
                        });

                    }
                    else{
                        proceedToViewActivity();
                    }

                    // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                    // app-defined int constant. The callback method gets the
                    // result of the request.
                }
            }
            else {
                // Permission already granted
                Log.d("Permission check", "WRITE_EXTERNAL_STORAGE permission granted");
                proceedToViewActivity();
            }
        }
        else{
            Log.d("Version check", "Lower than marshmallow");

            proceedToViewActivity();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1000: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    proceedToViewActivity();
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {

                    textView.setText(getResources().getString(R.string.permission_try_again));
                    button.setVisibility(View.VISIBLE);
                    button.setText(getResources().getString(R.string.try_again));
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            button.setVisibility(View.GONE);
                            checkIfPermissionGranted();
                        }
                    });
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }



    public void proceedToViewActivity(){
        if(!MediaFiles.doesWhatsappDirExist()){
            //Looks like the user does not have whatsapp
            Log.e("WhatsApp folder stat", "Doesn't exist");
            textView.setText(getResources().getString(R.string.whatsapp_fold_not_exist));


            //Check if GBWhatsapp
            AlertDialog.Builder builder = new AlertDialog.Builder(IntroActivity.this, R.style.AlertDialogCustom);
            builder.setTitle(getResources().getString(R.string.fold_not_found));
            builder.setMessage(getResources().getString(R.string.whatsapp_fold_not_exist));
            builder.setPositiveButton(getResources().getString(R.string.retry), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();

//                    SharedPreferences sharedPref = getSharedPreferences("APP_DEFAULTS", Context.MODE_PRIVATE);
//                    //Change the value to false
//                    SharedPreferences.Editor editor = sharedPref.edit();
//                    editor.putString("WHATSAPP_STATUS_FOLD_PATH", Environment.getExternalStorageDirectory()+"/GBWhatsapp/Media/.Statuses/");
//                    editor.apply();

                    Intent intent = new Intent(getApplication(), IntroActivity.class);
                    startActivity(intent);

                }
            });
            builder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                }
            });
//            builder.setNeutralButton(getResources().getString(R.string.no_whatsapp), new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialogInterface, int i) {
//                    dialogInterface.dismiss();
//                    Toast.makeText(getApplication(), getResources().getString(R.string.app_cannot_work), Toast.LENGTH_SHORT).show();
//                }
//            });
            builder.show();

        }
        else{

            textView.setText(getResources().getString(R.string.app_name));

            //Initialise Fav file
            try {
                FileOutputStream fos = openFileOutput(Favourites.FAV_FILENAME, Context.MODE_APPEND);
                fos.write("".getBytes());
                fos.close();
            }
            catch (IOException ios){
                ios.printStackTrace();
            }


            try {
                SharedPreferences sharedPref = getSharedPreferences("APP_DEFAULTS", Context.MODE_PRIVATE);
                if(sharedPref.getBoolean("isAppFirstTime", true)){

                    //Change the value to false
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putBoolean("isAppFirstTime", false);
                    editor.apply();

                    //Make MediaScanner scan the existing files in StatusManager/Saved
                    makeMediaScan();

                    //First time continue button
                    button.setText(getResources().getString(R.string.continue_to));
                    button.setVisibility(View.VISIBLE);
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(IntroActivity.this, ViewActivity.class);
                            finish();
                            startActivity(intent);
                        }
                    });

                }
                else{
                    Intent intent = new Intent(IntroActivity.this, ViewActivity.class);
                    finish();
                    startActivity(intent);
                }
                Log.d("Shared pref try", "");
            }
            catch (Exception e){
                Log.d("SHared pref ", "Not found 1");
            }
        }
    }

    private void makeMediaScan(){

        MediaFiles.initSavedFiles();
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList = MediaFiles.getSavedFiles();
//        String[] array = MediaFiles.getSavedFiles().toArray(new String[0]);
        String array[] = new String[arrayList.size()];
        int i = 0;
        for(String str : arrayList){
            array[i] = MediaFiles.DOWNLOADED_IMAGE_PATH + str;
            i++;
        }

        MediaScannerConnection.scanFile(
                getApplicationContext(),
                array,
                null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    @Override
                    public void onScanCompleted(String path, Uri uri) {
                        Log.v("MediaScanner ",
                                "file " + path + " was scanned successfully: " + uri);
                    }
                });
    }
}
