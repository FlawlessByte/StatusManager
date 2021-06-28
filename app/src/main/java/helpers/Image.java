package helpers;

/**
 * Created by JIMMY on 15-Feb-18.
 */

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;


public class Image implements Serializable{
    private String size;
    private String large;
    private String timestamp;
    private Date time;
    private boolean isVideo = false;

    public Image() {
    }

    public Image(String name, String large){
        this.size = name;
        this.large = large;
    }

    public Image(String name, String large, String timestamp) {
        this.size = name;
        this.large = large;
        this.timestamp = timestamp;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public Date getTime() {
        return time;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String name) {
        this.size = name;
    }

    public String getLarge() {
        return large;
    }

    public void setLarge(String large) {
        this.large = large;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isVideo() {
        return isVideo;
    }
    public void setIsVideo(Boolean isVideo){
        this.isVideo = isVideo;
    }

    public static Comparator<Image> dateComparator = new Comparator<Image>() {
        @Override
        public int compare(Image img1, Image img2) {
            return (int) (img2.getTime().compareTo(img1.getTime())) ;
        }
    };


}
