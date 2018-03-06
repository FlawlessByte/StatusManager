package fragments;

/**
 * Created by JIMMY on 14-Feb-18.
 */

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.formats.NativeAd;
import com.google.android.gms.ads.formats.NativeAppInstallAd;
import com.google.android.gms.ads.formats.NativeContentAd;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import co.realinventor.statusmanager.R;
import helpers.GalleryAdapter;
import helpers.Image;
import helpers.MediaFiles;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlaceholderFragment extends Fragment {

    public static final int FILE_VIDEO = 100;
    public static final int FILE_IMAGE = 101;
    private int MEDIA_TYPE ;
    private int GRID_COUNT = 2;


    /**Ad variables**/
    // The number of native ads to load and display.
    public static final int NUMBER_OF_ADS = 2;

    // List of native ads that have been successfully loaded.
    private List<NativeAd> mNativeAds = new ArrayList<>();

    private ArrayList<Image> images;
    private ArrayList<Object> allObjects;

    private GalleryAdapter mAdapter;
    private RecyclerView recyclerView;

    public PlaceholderFragment() {}



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_view, container, false);

        MEDIA_TYPE = getArguments().getInt("TYPE_MEDIA");
        Log.d("TYPE_MEDIA ", ""+MEDIA_TYPE);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);

        images = new ArrayList<>();
        allObjects = new ArrayList<>();
        mAdapter = new GalleryAdapter(getActivity(),allObjects);


//        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 3);

        RecyclerView.LayoutManager mLayoutManager = new StaggeredGridLayoutManager(GRID_COUNT, LinearLayoutManager.VERTICAL);

        if(MEDIA_TYPE == FILE_VIDEO){
//            mLayoutManager = new GridLayoutManager(getActivity(), 1);
            mLayoutManager = new StaggeredGridLayoutManager(GRID_COUNT, LinearLayoutManager.VERTICAL);
        }

        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);


        recyclerView.addOnItemTouchListener(new GalleryAdapter.RecyclerTouchListener(getContext(), recyclerView, new GalleryAdapter.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("images", images);
                bundle.putInt("position", position);
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                SlideshowDialogFragment newFragment = SlideshowDialogFragment.newInstance();
                newFragment.setArguments(bundle);
                newFragment.show(ft, "slideshow");
            }

            @Override
            public void onLongClick(View view, int position) {
                Toast.makeText(getActivity(), "Long clicked!",Toast.LENGTH_SHORT).show();
            }
        }));


        processImages();

        Collections.sort(images, Image.dateComparator);

        for(Image i: images){
            allObjects.add(i);
        }

        loadNativeAd();

        mAdapter.notifyDataSetChanged();

        return rootView;
    }



    private void processImages(){

        ArrayList<String> fils;

        if(MEDIA_TYPE == FILE_IMAGE){
            fils = MediaFiles.getImageFiles();
        }
        else{
            fils = MediaFiles.getVideoFiles();
        }


        String PATH = Environment.getExternalStorageDirectory() + "/Whatsapp/Media/.Statuses/";

        //DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(getContext());

        android.text.format.DateFormat df = new android.text.format.DateFormat();


        for (String i:fils){
            Image image = new Image();
            File file = new File(PATH+i);
            Date date = new Date(file.lastModified());


            String DATA_TAG = "KB";
            String FILE_SIZE = String.format("%.02f", (file.length() / 1024.0));
            if(MEDIA_TYPE == FILE_VIDEO){
                FILE_SIZE = String.format("%.02f", ((file.length() / 1024.0)/1024.0));
                DATA_TAG = "MB";
                image.setIsVideo(true);
            }

            image.setTimestamp(df.format("hh:mm:ss a | dd MMMM, yyyy", date).toString());
            //change method to setSIze()
            image.setSize(FILE_SIZE + " "+DATA_TAG);
            image.setLarge(PATH + i);
            image.setTime(new Date(file.lastModified()));
            images.add(image);


        }
    }


    private void insertAdsInItems() {
        if (mNativeAds.size() <= 0) {
            return;
        }

        int offset = (allObjects.size() / mNativeAds.size()) + 1;
        int index = 0;
        for (NativeAd ad: mNativeAds) {
            allObjects.add(index, ad);
            index = index + offset;
        }
//        loadMenu();
    }


    private void loadNativeAd(final int adLoadCount) {

        if (adLoadCount >= NUMBER_OF_ADS) {
            insertAdsInItems();
            return;
        }

        AdLoader.Builder builder = new AdLoader.Builder(getActivity(), "ca-app-pub-3940256099942544/2247696110");
        AdLoader adLoader = builder.forAppInstallAd(new NativeAppInstallAd.OnAppInstallAdLoadedListener() {
            @Override
            public void onAppInstallAdLoaded(NativeAppInstallAd ad) {
                // An app install ad loaded successfully, call this method again to
                // load the next ad in the items list.
                mNativeAds.add(ad);
                loadNativeAd(adLoadCount + 1);

            }
        }).forContentAd(new NativeContentAd.OnContentAdLoadedListener() {
            @Override
            public void onContentAdLoaded(NativeContentAd ad) {
                // A content ad loaded successfully, call this method again to
                // load the next ad in the items list.
                mNativeAds.add(ad);
                loadNativeAd(adLoadCount + 1);
            }
        }).withAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(int errorCode) {
                // A native ad failed to load. Call this method again to load
                // the next ad in the items list.
                Log.e("MainActivity", "The previous native ad failed to load. Attempting to" +
                        " load another.");
                loadNativeAd(adLoadCount + 1);
            }
        }).build();

        // Load the Native Express ad.
        adLoader.loadAd(new AdRequest.Builder().addTestDevice("750C63CE8C1A0106CF1A8A4C5784DC17").build());
    }

    private void loadNativeAd() {
        loadNativeAd(0);
    }
}
