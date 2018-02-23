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


import java.io.File;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

import co.realinventor.statusmanager.R;
import helpers.GalleryAdapter;
import helpers.Image;
import helpers.MediaFiles;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlaceholderFragment extends Fragment {

//    private String TAG = getClass().getSimpleName();
//    private static final String endpoint = "https://api.androidhive.info/json/glide.json";
    private ArrayList<Image> images;

    private GalleryAdapter mAdapter;
    private RecyclerView recyclerView;

    public PlaceholderFragment() {}

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
//        public static PlaceholderFragment newInstance(int sectionNumber) {
//            PlaceholderFragment fragment = new PlaceholderFragment();
//            Bundle args = new Bundle();
//            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
//            fragment.setArguments(args);
//            return fragment;
//        }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_view, container, false);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);

        images = new ArrayList<>();
        mAdapter = new GalleryAdapter(getActivity(), images);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 2);
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

            }
        }));


        processImages();
        mAdapter.notifyDataSetChanged();

        return rootView;
    }



    private void processImages(){

        ArrayList<String> imgs = MediaFiles.getImageFiles();

        String PATH = Environment.getExternalStorageDirectory() + "/Whatsapp/Media/.Statuses/";

        //DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(getContext());

        android.text.format.DateFormat df = new android.text.format.DateFormat();


        for (String i:imgs){
            Image image = new Image();
            //image.setName(i);
            File file = new File(PATH+i);
            Date date = new Date(file.lastModified());
            image.setTimestamp(df.format("hh:mm:ss a | dd MMMM, yyyy",date).toString());

            //change method to setSIze()
            image.setName(String.format("%.02f",(file.length()/1024.0))+" KB");
            image.setLarge(PATH+i);
            images.add(image);
        }
    }
}
