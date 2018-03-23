package fragments;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import co.realinventor.statusmanager.R;
import co.realinventor.statusmanager.SettingsPrefActivity;
import helpers.Favourites;
import helpers.GalleryAdapter;
import helpers.Image;
import helpers.MediaFiles;

/**
 * Created by JIMMY on 27-Feb-18.
 */

public class DownViewFragment extends Fragment {

    private GalleryAdapter mAdapter;
    private RecyclerView recyclerView;
    private ArrayList<Image> images;
    private ArrayList<Object> allObjects;
    private int GRID_COUNT = 3;
    private SwipeRefreshLayout swipeRefreshLayout;


    public DownViewFragment(){};

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_view, container, false);


        try {
            SharedPreferences sharedPref = getActivity().getSharedPreferences("GRID_NUMBERS", Context.MODE_PRIVATE);
            if(getArguments().getString("title").equals("favs")) {
                GRID_COUNT = sharedPref.getInt("favs_grid_count", 3);
            }
            if(getArguments().getString("title").equals("downloads")) {
                GRID_COUNT = sharedPref.getInt("downloads_grid_count", 3);
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

                Toast.makeText(getActivity(), "Refreshed!",Toast.LENGTH_SHORT).show();

            }
        });

        images = new ArrayList<>();
        allObjects = new ArrayList<>();
        mAdapter = new GalleryAdapter(getActivity(),allObjects);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), GRID_COUNT);

        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        recyclerView.addOnItemTouchListener(new GalleryAdapter.RecyclerTouchListener(getContext(), recyclerView, new GalleryAdapter.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("images", images);
                bundle.putInt("position", position);
                bundle.putString("title",getArguments().getString("title"));
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

        mAdapter.notifyDataSetChanged();

        return rootView;
    }

    private void processImages(){

        ArrayList<String> fils;

        images.clear();
        allObjects.clear();
        mAdapter.notifyDataSetChanged();
        MediaFiles.initSavedFiles();
        fils = MediaFiles.getSavedFiles();

        if(getArguments().getString("title").equals("favs")){
            fils.clear();
            fils = getFavFiles();
            for(String str : fils){
                Log.d("Fav files ",str);
            }
        }



        //DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(getContext());

        android.text.format.DateFormat df = new android.text.format.DateFormat();


        for (String i:fils){
            Image image = new Image();
            File file = new File(MediaFiles.DOWNLOADED_IMAGE_PATH+i);
            Date date = new Date(file.lastModified());


            String DATA_TAG = "KB";
            String FILE_SIZE = String.format("%.02f", (file.length() / 1024.0));
            if(i.endsWith(".mp4")){
                FILE_SIZE = String.format("%.02f", ((file.length() / 1024.0)/1024.0));
                DATA_TAG = "MB";
                image.setIsVideo(true);
            }

            image.setTimestamp(df.format("hh:mm:ss a | dd MMMM, yyyy", date).toString());
            //change method to setSIze()
            image.setSize(FILE_SIZE + " "+DATA_TAG);
            image.setLarge(MediaFiles.DOWNLOADED_IMAGE_PATH + i);
            image.setTime(new Date(file.lastModified()));
            images.add(image);


        }
    }


    private ArrayList<String> getFavFiles(){
        ArrayList<String> lines= new ArrayList<>();
        try{
            FileInputStream fis = getActivity().openFileInput(Favourites.FAV_FILENAME);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader bufferedReader = new BufferedReader(isr);

            String line;

            while ((line = bufferedReader.readLine()) != null) {
                Log.d("String lines", line);
                lines.add(line);
            }
        }
        catch (FileNotFoundException e){
            Log.e("File open ", "File not found..");
        }
        catch (IOException ios){
            Log.e("File read", "Error reading file");
        }

        Iterator<String> iter = lines.iterator();
        while(iter.hasNext()){
            String str = iter.next();
            File file = new File(MediaFiles.DOWNLOADED_IMAGE_PATH +str);
            if(!file.exists()){
                iter.remove();
            }
            if(str.equals("")){
                iter.remove();
            }
        }

        return lines;
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

                if (getArguments().getString("title").equals("favs")) {
                    editor.putInt("favs_grid_count", GRID_COUNT);
                }
                if (getArguments().getString("title").equals("downloads")) {
                    editor.putInt("downloads_grid_count", GRID_COUNT);
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

            Toast.makeText(getActivity(), "Refreshed!",Toast.LENGTH_SHORT).show();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
