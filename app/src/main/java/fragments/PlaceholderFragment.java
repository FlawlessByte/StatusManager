package fragments;

/**
 * Created by JIMMY on 14-Feb-18.
 */

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.formats.NativeAd;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import co.realinventor.statusmanager.Helpers.GalleryAdapter;
import co.realinventor.statusmanager.R;
import co.realinventor.statusmanager.SettingsPrefActivity;
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
    private ArrayList<Image> allObjects;
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
                processImages();
                Collections.sort(images, Image.dateComparator);

                for(Image i: images){
                    allObjects.add(i);
                }

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.notifyDataSetChanged();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                },600);
                Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.refreshed),Toast.LENGTH_SHORT).show();
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

        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        recyclerView.addOnItemTouchListener(new GalleryAdapter.RecyclerTouchListener(getContext(), recyclerView, new GalleryAdapter.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Log.d("Recycler view clicked","Pos "+position);
                Bundle bundle = new Bundle();
                bundle.putSerializable("images", allObjects);
                bundle.putString("title", getArguments().getString("title"));
                bundle.putInt("position", position);
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                SlideshowDialogFragment newFragment = SlideshowDialogFragment.newInstance();
                newFragment.setArguments(bundle);
                newFragment.show(ft, "slideshow");
            }

            @Override
            public void onLongClick(View view, int position) {
                //Toast.makeText(getActivity(), "Long clicked!",Toast.LENGTH_SHORT).show();
            }
        }));


        processImages();

        Collections.sort(images, Image.dateComparator);


        for(Image i: images){
            allObjects.add(i);
        }

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
        MediaFiles.initMediaFiles();
        allObjects.clear();
        images.clear();

        if(MEDIA_TYPE == FILE_IMAGE){
            fils = MediaFiles.getImageFiles();
        }
        else{
            fils = MediaFiles.getVideoFiles();
        }

        String PATH = MediaFiles.WHATSAPP_STATUS_FOLDER_PATH;

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
            processImages();
            Collections.sort(images, Image.dateComparator);

            for(Image i: images){
                allObjects.add(i);
            }

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mAdapter.notifyDataSetChanged();
                    swipeRefreshLayout.setRefreshing(false);
                }
            },800);

            Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.refreshed),Toast.LENGTH_SHORT).show();


            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
