package co.realinventor.statusmanager;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;

import java.io.FileOutputStream;
import java.io.IOException;

import helpers.Favourites;
import helpers.MediaFiles;
import com.google.android.gms.ads.MobileAds;


public class IntroActivity extends AppCompatActivity {

    boolean isPermGranted = false;
    private TextView textView;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        //Admob initialization
        //MobileAds.initialize(this, "ca-app-pub-3940256099942544~3347511713");


        textView = (TextView)findViewById(R.id.textView);
        button = (Button)findViewById(R.id.buttonGone);


        checkIfPermissionGranted();


//        LottieAnimationView animationView = (LottieAnimationView) findViewById(R.id.lottieAnimView);
//        animationView.playAnimation();



//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                Intent intent = new Intent(IntroActivity.this, ViewActivity.class);
//                finish();
//                startActivity(intent);
//            }
//        },1500);
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
                    AlertDialog.Builder builder = new AlertDialog.Builder(IntroActivity.this);
                    builder.setTitle("Need Storage Permission");
                    builder.setMessage("This app needs read & write permission to access files in your system.");
                    builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            ActivityCompat.requestPermissions(IntroActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1000);
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.show();

                }
                else {

                    // No explanation needed; request the permission
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            1000);

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

                    textView.setText("Please provide the required privilleges and try again!");
                    button.setVisibility(View.VISIBLE);
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
            textView.setText("It looks like you don't have a Whatsapp Status folder in your system!");
        }
        else{

            textView.setText("Welcome!");
            try {
                FileOutputStream fos = openFileOutput(Favourites.FAV_FILENAME, Context.MODE_APPEND);
                fos.write("".getBytes());
                fos.close();
            }
            catch (IOException ios){
                ios.printStackTrace();
            }

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
    }
}
