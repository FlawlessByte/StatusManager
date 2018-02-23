package helpers;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by JIMMY on 14-Feb-18.
 */

public class MediaFiles {
    public static ArrayList<String> allFiles = new ArrayList<String>();
    public static ArrayList<String> imageFiles = new ArrayList<String>();
    public static ArrayList<String> videoFiles = new ArrayList<String>();

    //Default constructor to initialise allFiles, imageFiles, videoFiles
    public static void initMediaFiles(){
        File directory = new File(Environment.getExternalStorageDirectory() + "/Whatsapp/Media/.Statuses");
        Log.d("Directory exists:",""+directory.exists());
        File[] files = directory.listFiles();
        Log.d("Files", "Size: "+ files.length);
        for (int i = 0; i < files.length; i++)
        {
            allFiles.add(files[i].getName());
            if(files[i].getName().endsWith(".jpg")){
                imageFiles.add(files[i].getName());
            }
            else if(files[i].getName().endsWith(".mp4")){
                videoFiles.add(files[i].getName());
            }
        }

        for (String s:imageFiles){
            Log.d("Image file: ",s);
        }

        for (String t:videoFiles){
            Log.d("Video file: ",t);
        }
    }

    public static ArrayList<String> getAllFiles(){
        return allFiles;
    }

    public static ArrayList<String> getImageFiles() {
        return imageFiles;
    }

    public static ArrayList<String> getVideoFiles() {
        return videoFiles;
    }
}
