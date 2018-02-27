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
    public static ArrayList<String> savedVideoFiles = new ArrayList<>();
    public static ArrayList<String> savedImageFiles = new ArrayList<>();
    private static String APP_FOLDER_NAME = "StatusManager";


    public static void initSavedFiles(){
        File directory = new File(Environment.getExternalStorageDirectory() + "/" +APP_FOLDER_NAME+ "/Videos");

        File[] files = directory.listFiles();
        Log.d("Files", "Size: "+ files.length);
        for (int i = 0; i < files.length; i++)
        {
            if(files[i].getName().endsWith(".mp4")){
                savedVideoFiles.add(files[i].getName());
            }
        }

        directory = new File(Environment.getExternalStorageDirectory() + "/" +APP_FOLDER_NAME+ "/Images");

        files = directory.listFiles();
        Log.d("Files", "Size: "+ files.length);
        for (int i = 0; i < files.length; i++)
        {
            if(files[i].getName().endsWith(".jpg")){
                savedImageFiles.add(files[i].getName());
            }
        }
    }

    public static ArrayList<String> getSavedVideoFiles(){
        return savedVideoFiles;
    }

    public static ArrayList<String> getSavedImageFiles() {
        return savedImageFiles;
    }

    public static boolean doesWhatsappDirExist(){
        File dir = new File(Environment.getExternalStorageDirectory() + "/Whatsapp/Media/.Statuses");
        if(dir.exists() && dir.isDirectory()) {
            return true;
        }
        else{
            return false;
        }
    }

    public static void initAppDirectrories(){
        File dir = new File(Environment.getExternalStorageDirectory() + "/" +APP_FOLDER_NAME);
        if(dir.exists() && dir.isDirectory()) {
            dir = new File(Environment.getExternalStorageDirectory() + "/" +APP_FOLDER_NAME + "/Videos");
            if(!(dir.exists() && dir.isDirectory())) {
                dir.mkdir();
            }
            dir = new File(Environment.getExternalStorageDirectory() + "/" +APP_FOLDER_NAME + "/Images");
            if(!(dir.exists() && dir.isDirectory())) {
                dir.mkdir();
            }
        }
        else{
            dir.mkdir();
            dir = new File(Environment.getExternalStorageDirectory() + "/" +APP_FOLDER_NAME + "/Videos");
            dir.mkdir();
            dir = new File(Environment.getExternalStorageDirectory() + "/" +APP_FOLDER_NAME + "/Images");
            dir.mkdir();
        }
    }

    //Method to initialise allFiles, imageFiles, videoFiles
    public static void initMediaFiles(){
        File directory = new File(Environment.getExternalStorageDirectory() + "/Whatsapp/Media/.Statuses");
        Log.d("Directory exists:",""+doesWhatsappDirExist());
        File[] files = directory.listFiles();
        Log.d("Files", "Size: "+ files.length);
        for (int i = 0; i < files.length; i++)
        {
            allFiles.add(files[i].getName());
            if(files[i].getName().endsWith(".jpg") || files[i].getName().endsWith(".gif") ){
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
