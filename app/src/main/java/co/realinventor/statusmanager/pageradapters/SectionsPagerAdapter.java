package co.realinventor.statusmanager.pageradapters;

/**
 * Created by JIMMY on 22-Feb-18.
 */

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import fragments.DownViewFragment;
import fragments.PlaceholderFragment;

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        switch (position){
            case 0:
                Bundle args1 = new Bundle();
                args1.putString("title","images");
                args1.putInt("TYPE_MEDIA",PlaceholderFragment.FILE_IMAGE);
                PlaceholderFragment fragment1 = new PlaceholderFragment();
                fragment1.setArguments(args1);
                return fragment1;
            case 1:
                Bundle args2 = new Bundle();
                args2.putString("title","videos");
                args2.putInt("TYPE_MEDIA",PlaceholderFragment.FILE_VIDEO);
                PlaceholderFragment fragment2 = new PlaceholderFragment();
                fragment2.setArguments(args2);
                return fragment2;
            case 2:
                Bundle args3 = new Bundle();
                args3.putString("title","downloads");
                DownViewFragment fragment3 = new DownViewFragment();
                fragment3.setArguments(args3);
                return fragment3;
            case 3:
                Bundle args4 = new Bundle();
                args4.putString("title","favs");
                DownViewFragment fragment4 = new DownViewFragment();
                fragment4.setArguments(args4);
                return fragment4;
            default:
                return null;
        }

    }

    @Override
    public int getCount() {
        // Show 4 total pages.
        return 4;
    }

    @Override
    public CharSequence getPageTitle(int position){
        switch (position){
            case 0:
                return "Images";
            case 1:
                return "Videos";
            case 2:
                return "Saved";
            case 3:
                return "Favourites";
            default:
                return null;
        }
    }

}