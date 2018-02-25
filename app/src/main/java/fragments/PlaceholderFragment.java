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
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;


import java.io.File;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

import co.realinventor.statusmanager.R;
import helpers.GalleryAdapter;
import helpers.Image;
import helpers.MediaFiles;
import helpers.Videos;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlaceholderFragment extends Fragment {

    public static final int FILE_VIDEO = 100;
    public static final int FILE_IMAGE = 101;
    private int MEDIA_TYPE ;

//    private String TAG = getClass().getSimpleName();
//    private static final String endpoint = "https://api.androidhive.info/json/glide.json";
    private ArrayList<Image> images;
    private ArrayList<Videos> videos;

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


        if(MEDIA_TYPE == FILE_IMAGE){
            images = new ArrayList<>();
            mAdapter = new GalleryAdapter(getActivity(), images);
        }

        if(MEDIA_TYPE == FILE_VIDEO){
            videos = new ArrayList<>();
            mAdapter = new GalleryAdapter(videos, getActivity());
        }



        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 3);

        if(MEDIA_TYPE == FILE_VIDEO){
            mLayoutManager = new GridLayoutManager(getActivity(), 2);
        }
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);


        recyclerView.addOnItemTouchListener(new GalleryAdapter.RecyclerTouchListener(getContext(), recyclerView, new GalleryAdapter.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                if(MEDIA_TYPE == FILE_IMAGE){
                    Bundle bundle = new Bundle();
                    bundle.putInt("MEDIA_TYPE",101);
                    bundle.putSerializable("images", images);
                    bundle.putInt("position", position);

                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                    SlideshowDialogFragment newFragment = SlideshowDialogFragment.newInstance();
                    newFragment.setArguments(bundle);
                    newFragment.show(ft, "slideshow");
                }
                else{
                    Log.d("Note me ", "ENtered  else");
                    Bundle bundle = new Bundle();
                    bundle.putInt("MEDIA_TYPE",100);
                    bundle.putSerializable("videos", videos);
                    bundle.putInt("position", position);

                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                    SlideshowDialogFragment newFragment = SlideshowDialogFragment.newInstance();
                    newFragment.setArguments(bundle);
                    newFragment.show(ft, "slideshow");
                }

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));


        processImages();
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
            Videos video = new Videos();
            File file = new File(PATH+i);
            Date date = new Date(file.lastModified());


            if(MEDIA_TYPE == FILE_IMAGE) {
                image.setTimestamp(df.format("hh:mm:ss a | dd MMMM, yyyy", date).toString());
                //change method to setSIze()
                image.setName(String.format("%.02f", (file.length() / 1024.0)) + " KB");
                image.setLarge(PATH + i);
                images.add(image);
            }
            if(MEDIA_TYPE == FILE_VIDEO){
                video.setTimestamp(df.format("hh:mm:ss a | dd MMMM, yyyy", date).toString());
                //change method to setSIze()
                video.setName(String.format("%.02f", (file.length() / 1024.0)) + " KB");
                video.setLarge(PATH + i);
                videos.add(video);
            }

        }
    }
}
