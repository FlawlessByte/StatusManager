package helpers;

/**
 * Created by JIMMY on 15-Feb-18.
 */

import java.io.Serializable;


public class Image implements Serializable {
    private String size;
    private String large;
    private String timestamp;

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

    public String getName() {
        return size;
    }

    public void setName(String name) {
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
}
