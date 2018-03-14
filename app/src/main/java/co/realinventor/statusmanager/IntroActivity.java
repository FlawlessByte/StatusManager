package co.realinventor.statusmanager;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.airbnb.lottie.LottieAnimationView;

import java.io.FileOutputStream;
import java.io.IOException;

import helpers.Favourites;
import helpers.MediaFiles;
import com.google.android.gms.ads.MobileAds;


public class IntroActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        //Admob initialization
        MobileAds.initialize(this, "ca-app-pub-3940256099942544~3347511713");

        if(!MediaFiles.doesWhatsappDirExist()){
            //Looks like the user does not have whatsapp
            Log.e("WhatsApp folder stat", "Doesn't exist");
        }
        else{

        }

        try {
            FileOutputStream fos = openFileOutput(Favourites.FAV_FILENAME, Context.MODE_APPEND);
            fos.write("".getBytes());
            fos.close();
        }
        catch (IOException ios){
            ios.printStackTrace();
        }

        LottieAnimationView animationView = (LottieAnimationView) findViewById(R.id.lottieAnimView);
        animationView.playAnimation();

        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1000);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(IntroActivity.this, ViewActivity.class);
                startActivity(intent);
            }
        },1500);
    }
}
