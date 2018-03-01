package co.realinventor.statusmanager;

import android.Manifest;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import helpers.MediaFiles;

public class IntroActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        if(!MediaFiles.doesWhatsappDirExist()){
            //Looks like the user does not have whatsapp
            Log.e("WhatsApp folder stat", "Doesn't exist");
        }



        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1000);

        MediaFiles.initMediaFiles();
        MediaFiles.initAppDirectrories();

        Intent intent = new Intent(IntroActivity.this, ViewActivity.class);
        startActivity(intent);
    }
}
