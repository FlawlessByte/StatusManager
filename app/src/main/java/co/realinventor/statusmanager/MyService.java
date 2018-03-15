package co.realinventor.statusmanager;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.FileObserver;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import helpers.MediaFiles;

public class MyService extends Service {

    public static MyFileObserver myFileObserver;

    public MyService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //TODO do something useful

        Notification notification ;

        Intent notificationIntent = new Intent(this, ViewActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        Notification.Builder builder = new Notification.Builder(getApplicationContext())
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.app_logo)
                .setContentTitle("StatusSaver");
        notification = builder.build();

        startForeground(2345, notification);


        myFileObserver = new MyFileObserver(MediaFiles.WHATSAPP_STATUS_FOLDER_PATH, getApplicationContext());
        myFileObserver.startWatching();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if(prefs.getBoolean("key_auto_download",false)){
            Log.d("MyService","Auto download enabled");
        }
        else {
            Log.d("MyService","Auto download disabled");
        }

        if(prefs.getBoolean("notifications_new_message",false)){
            Log.d("MyService","notifications enabled");
        }
        else {
            Log.d("MyService","Notifications disabled");
        }

        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}