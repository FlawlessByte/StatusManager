package helpers;

import java.util.Comparator;

/**
 * Created by JIMMY on 24-Feb-18.
 */

public class Videos extends Image {

    public static Comparator<Videos> dateComparator = new Comparator<Videos>() {
        @Override
        public int compare(Videos vid1, Videos vid2) {
            return (int) (vid2.getTime().compareTo(vid1.getTime())) ;
        }
    };
}
