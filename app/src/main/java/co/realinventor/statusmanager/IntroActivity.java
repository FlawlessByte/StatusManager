package co.realinventor.statusmanager;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.MediaController;
import android.widget.VideoView;

import helpers.MediaFiles;
import helpers.Videos;

public class IntroActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);


//        MediaController mc= new MediaController(this);
//
//        VideoView videoView = (VideoView)findViewById(R.id.video_previews);
//        mc.setAnchorView(videoView);
//
//        videoView.setVisibility(View.VISIBLE);
//        String large = "/sdcard/Download/The_Chainsmokers_Coldplay_Someth.mp4";
//        videoView.setVideoURI(Uri.parse(large));
//        videoView.setMediaController(mc);
//        videoView.requestFocus();
//        videoView.start();





        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1000);

        MediaFiles.initMediaFiles();

        Intent intent = new Intent(IntroActivity.this, ViewActivity.class);
        startActivity(intent);
    }
}
