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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

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

//    private String TAG = getClass().getSimpleName();
//    private static final String endpoint = "https://api.androidhive.info/json/glide.json";
    private ArrayList<Image> images;
//    private ArrayList<Image> videos;

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
        mAdapter = new GalleryAdapter(getActivity(),images);


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
}
