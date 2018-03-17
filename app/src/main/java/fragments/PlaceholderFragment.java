package fragments;

/**
 * Created by JIMMY on 14-Feb-18.
 */

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import java.util.Iterator;
import java.util.List;

import co.realinventor.statusmanager.R;
import co.realinventor.statusmanager.SettingsPrefActivity;
import co.realinventor.statusmanager.ViewActivity;
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
    private int GRID_COUNT = 3;
    private AdLoader adLoader;


    /**Ad variables**/
    // The number of native ads to load and display.
    public static int NUMBER_OF_ADS = 2;

    // List of native ads that have been successfully loaded.
    private List<NativeAd> mNativeAds = new ArrayList<>();

    private ArrayList<Image> images;
    private ArrayList<Object> allObjects;

    private GalleryAdapter mAdapter;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;

    public PlaceholderFragment() {}


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_view, container, false);

        MEDIA_TYPE = getArguments().getInt("TYPE_MEDIA");
        Log.d("TYPE_MEDIA ", ""+MEDIA_TYPE);


        try {
            SharedPreferences sharedPref = getActivity().getSharedPreferences("GRID_NUMBERS", Context.MODE_PRIVATE);
            if(MEDIA_TYPE == FILE_VIDEO) {
                GRID_COUNT = sharedPref.getInt("video_grid_count", 3);
            }
            if(MEDIA_TYPE == FILE_IMAGE) {
                GRID_COUNT = sharedPref.getInt("image_grid_count", 3);
            }
            Log.d("Shared pref try", "GRID_COUNT "+GRID_COUNT);
        }
        catch (Exception e){
            Log.d("SHared pref ", "Not found 1");
        }



        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        swipeRefreshLayout = (SwipeRefreshLayout)rootView.findViewById(R.id.swipe_refresh_layout);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.i("SwipeRefreshLayout ", "onRefresh called from SwipeRefreshLayout");
                Intent intent = new Intent(getActivity(), ViewActivity.class);
                String title_value = (MEDIA_TYPE == FILE_IMAGE) ? "images" : "videos";
                intent.putExtra("title",title_value);
                getActivity().finish();
                startActivity(intent);
            }
        });

        images = new ArrayList<>();
        allObjects = new ArrayList<>();
        mAdapter = new GalleryAdapter(getActivity(),allObjects);


        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), GRID_COUNT);

//        RecyclerView.LayoutManager mLayoutManager = new StaggeredGridLayoutManager(GRID_COUNT, LinearLayoutManager.VERTICAL);

        if(MEDIA_TYPE == FILE_VIDEO){
            mLayoutManager = new GridLayoutManager(getActivity(), GRID_COUNT);
//            mLayoutManager = new StaggeredGridLayoutManager(GRID_COUNT, LinearLayoutManager.VERTICAL);
        }

//        ((GridLayoutManager)mLayoutManager).setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
//            @Override
//            public int getSpanSize(int position) {
//                boolean isAdPos = false;
//                int ad_place[] ={8,17,26,35,44,53,62,71,80,89,98,107,116,125,134,143,152,161,170,179,188,197,206,215,224,233,242,251,260};
//                for(int i: ad_place){
//                    if(position == i){
//                        isAdPos = true;
//                        break;
//                    }
//                }
//                if(isAdPos){
//                    return GRID_COUNT;
//                }
//                else{
//                    return 1;
//                }
//
//            }
//        });

        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);


        recyclerView.addOnItemTouchListener(new GalleryAdapter.RecyclerTouchListener(getContext(), recyclerView, new GalleryAdapter.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Log.d("Recycler view clicked","Pos "+position);

                if(allObjects.get(position) instanceof NativeAd){
                    Toast.makeText(getActivity(),"You clicked an ad", Toast.LENGTH_SHORT).show();
                }
                else {

                    removeAdsFromItems();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("images", allObjects);
                    bundle.putInt("position", position);
                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                    SlideshowDialogFragment newFragment = SlideshowDialogFragment.newInstance();
                    newFragment.setArguments(bundle);
                    newFragment.show(ft, "slideshow");
                }
            }

            @Override
            public void onLongClick(View view, int position) {
                Toast.makeText(getActivity(), "Long clicked!",Toast.LENGTH_SHORT).show();
            }
        }));


        processImages();

        Collections.sort(images, Image.dateComparator);

        NUMBER_OF_ADS = images.size()/8;
        NUMBER_OF_ADS = (NUMBER_OF_ADS > 6) ? 10 : NUMBER_OF_ADS;

        for(Image i: images){
            allObjects.add(i);
        }

//        loadNativeAd();
//        if(adLoader.isLoading()) {
//            mAdapter.notifyDataSetChanged();
//        }

        mAdapter.notifyDataSetChanged();
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.e("Fragment Started ","Placeholder "+MEDIA_TYPE);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e("Fragment Paused ","Placeholder "+MEDIA_TYPE);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("Fragment Resumed ","Placeholder "+MEDIA_TYPE);
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

    private void removeAdsFromItems() {
        Iterator<Object> iter = allObjects.iterator();
        while(iter.hasNext()) {
            Object obj = iter.next();
            if(obj instanceof NativeAd){
                iter.remove();
            }
        }
    }


    private void insertAdsInItems() {
        if (mNativeAds.size() <= 0) {
            return;
        }

        int ad_place[] ={8,17,26,35,44,53,62,71,80,89,98,107,116,125,134,143,152,161,170,179,188,197,206,215,224,233,242,251,260};

        int total_no_of_objects = NUMBER_OF_ADS + images.size();

        int image_pos = 0;
        int ad_array_pos = 0;
        for(int i=0;i<total_no_of_objects;i++){
            boolean adPlaced = false;
            if(ad_array_pos < NUMBER_OF_ADS) {
                if (ad_place[ad_array_pos] == i) {
                    allObjects.add(i, mNativeAds.get(ad_array_pos));
                    adPlaced = true;
                }
            }
            if(ad_place[ad_array_pos] != i){
                allObjects.add(i,images.get(image_pos));
                image_pos++;
            }
            if(adPlaced){
                ad_array_pos++;
            }

        }


//        int offset = (images.size() / mNativeAds.size()) + 1;
//        int index = 0;
//        for (NativeAd ad: mNativeAds) {
//            allObjects.add(index, ad);
//            index = index + offset;
//        }
//        loadMenu();

//        if (mNativeAds.size() <= 0) {
//            return;
//        }
//
//        int offset = 8;
//        int index = 8;
//        for (NativeAd ad: mNativeAds) {
//            allObjects.add(index, ad);
//            index = index + offset+1;
//        }
    }


    private void loadNativeAd(final int adLoadCount) {

        if (adLoadCount >= NUMBER_OF_ADS) {
            insertAdsInItems();
            return;
        }

        AdLoader.Builder builder = new AdLoader.Builder(getActivity(), "ca-app-pub-3940256099942544/2247696110");
        adLoader = builder.forAppInstallAd(new NativeAppInstallAd.OnAppInstallAdLoadedListener() {
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
        }).withAdListener(new AdListener(){
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
            }
        }).build();

        // Load the Native Express ad.
        adLoader.loadAd(new AdRequest.Builder().addTestDevice("750C63CE8C1A0106CF1A8A4C5784DC17").build());
    }

    private void loadNativeAd() {
        loadNativeAd(0);
    }



    //Menu thing
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(getActivity(), SettingsPrefActivity.class));
            return true;
        }

        if(id == R.id.action_grid){

            if(GRID_COUNT >= 4){
                GRID_COUNT = 1;
            }
            else{
                GRID_COUNT++;
            }

            try {
                SharedPreferences sharedPref = getActivity().getSharedPreferences("GRID_NUMBERS", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                if (MEDIA_TYPE == FILE_VIDEO) {
                    editor.putInt("video_grid_count", GRID_COUNT);
                }
                if (MEDIA_TYPE == FILE_IMAGE) {
                    editor.putInt("image_grid_count", GRID_COUNT);
                }
                editor.apply();
            }
            catch (Exception e){
                Log.e("Shared pref", "Error writing to sharedprefs");
            }

            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), GRID_COUNT);
            recyclerView.setLayoutManager(mLayoutManager);
        }

        if(id == R.id.action_refresh){
            Log.i("Menu refresh", "Refresh menu item selected");

            // Signal SwipeRefreshLayout to start the progress indicator
            swipeRefreshLayout.setRefreshing(true);
            final Intent intent = new Intent(getActivity(), ViewActivity.class);
            String title_value = (MEDIA_TYPE == FILE_IMAGE) ? "images" : "videos";
            intent.putExtra("title",title_value);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    getActivity().finish();
                    startActivity(intent);
                }
            },1000);


            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
