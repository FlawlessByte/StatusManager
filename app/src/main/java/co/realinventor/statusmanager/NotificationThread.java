package co.realinventor.statusmanager;

import android.content.Context;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import helpers.MediaFiles;

/**
 * Created by JIMMY on 13-Mar-18.
 */

public class NotificationThread extends Thread {
    Context context;
    Date currentTime;
    ArrayList<String> allFiles = new ArrayList<>();

    NotificationThread(Context context){
        this.context = context;
    }

    public void run(){
        while (!Thread.currentThread().isInterrupted()) {
            boolean newFilesPresent = false;
            currentTime = Calendar.getInstance().getTime();
            try {
                Thread.sleep(10000);
            }
            catch (InterruptedException e){
                e.printStackTrace();
                return;
            }

            MediaFiles.initMediaFiles();
            allFiles.clear();
            allFiles = MediaFiles.getAllFiles();

            for (String i: allFiles){
                File file = new File(MediaFiles.WHATSAPP_STATUS_FOLDER_PATH+i);
                Date date = new Date(file.lastModified());
                if (date.after(currentTime)){

                    newFilesPresent = true;
                    break;
                }
            }

            if(newFilesPresent){
                Log.d("Notification Thread", "Found 1 file");
                Toast.makeText(context, "Found 1 file", Toast.LENGTH_SHORT).show();
                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context,"channels")
                        .setSmallIcon(R.drawable.app_logo)
                        .setContentTitle("Status Manager")
                        .setContentText("You have new status notifications")
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);
                mBuilder.build();
            }
            else{
                Log.d("Notification Thread", "No new file found");
            }

            if(Thread.interrupted()){
                return;
            }

        }
    }

    public void cancel(){
        interrupt();
    }
}
