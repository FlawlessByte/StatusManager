package co.realinventor.statusmanager;

import android.content.Context;
import android.os.FileObserver;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.io.IOException;

import helpers.Favourites;

/**
 * Created by JIMMY on 14-Mar-18.
 */

public class MyFileObserver extends FileObserver {
    public String absolutePath;
    private Context context;
    public MyFileObserver(String path, Context context) {
        super(path, FileObserver.ALL_EVENTS);
        absolutePath = path;
        this.context = context;
    }
    @Override
    public void onEvent(int event, String path) {
        if (path == null) {
            return;
        }
        //a new file or subdirectory was created under the monitored directory
        if ((FileObserver.CREATE & event)!=0) {
//            FileAccessLogStatic.accessLogMsg += absolutePath + "/" + path + " is createdn";


            Log.d("File Observer ", "Found 1 file"+absolutePath +"/"+path);
            Toast.makeText(context, "Found 1 file"+absolutePath +"/"+path, Toast.LENGTH_SHORT).show();
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                            .setSmallIcon(R.drawable.app_logo)
                            .setContentTitle("Status Manager")
                            .setContentText("You have new status notifications\n"+absolutePath +"/"+path)
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT);
            mBuilder.build();

        }


//        //a file or directory was opened
//        if ((FileObserver.OPEN & event)!=0) {
//            FileAccessLogStatic.accessLogMsg += path + " is openedn";
//        }
//        //data was read from a file
//        if ((FileObserver.ACCESS & event)!=0) {
//            FileAccessLogStatic.accessLogMsg += absolutePath + "/" + path + " is accessed/readn";
//        }
//        //data was written to a file
//        if ((FileObserver.MODIFY & event)!=0) {
//            FileAccessLogStatic.accessLogMsg += absolutePath + "/" + path + " is modifiedn";
//        }
//        //someone has a file or directory open read-only, and closed it
//        if ((FileObserver.CLOSE_NOWRITE & event)!=0) {
//            FileAccessLogStatic.accessLogMsg += path + " is closedn";
//        }
//        //someone has a file or directory open for writing, and closed it
//        if ((FileObserver.CLOSE_WRITE & event)!=0) {
//            FileAccessLogStatic.accessLogMsg += absolutePath + "/" + path + " is written and closedn";
//        }
//        //[todo: consider combine this one with one below]
//        //a file was deleted from the monitored directory
//        if ((FileObserver.DELETE & event)!=0) {
//            //for testing copy file
//// FileUtils.copyFile(absolutePath + "/" + path);
//            FileAccessLogStatic.accessLogMsg += absolutePath + "/" + path + " is deletedn";
//        }
//        //the monitored file or directory was deleted, monitoring effectively stops
//        if ((FileObserver.DELETE_SELF & event)!=0) {
//            FileAccessLogStatic.accessLogMsg += absolutePath + "/" + " is deletedn";
//        }
//        //a file or subdirectory was moved from the monitored directory
//        if ((FileObserver.MOVED_FROM & event)!=0) {
//            FileAccessLogStatic.accessLogMsg += absolutePath + "/" + path + " is moved to somewhere " + "n";
//        }
//        //a file or subdirectory was moved to the monitored directory
//        if ((FileObserver.MOVED_TO & event)!=0) {
//            FileAccessLogStatic.accessLogMsg += "File is moved to " + absolutePath + "/" + path + "n";
//        }
//        //the monitored file or directory was moved; monitoring continues
//        if ((FileObserver.MOVE_SELF & event)!=0) {
//            FileAccessLogStatic.accessLogMsg += path + " is movedn";
//        }
//        //Metadata (permissions, owner, timestamp) was changed explicitly
//        if ((FileObserver.ATTRIB & event)!=0) {
//            FileAccessLogStatic.accessLogMsg += absolutePath + "/" + path + " is changed (permissions, owner, timestamp)n";
//        }
    }
}