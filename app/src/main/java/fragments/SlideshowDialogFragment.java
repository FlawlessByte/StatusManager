package fragments;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.TransitionDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.github.chrisbanes.photoview.PhotoView;

import java.util.ArrayList;

import co.realinventor.statusmanager.R;
import helpers.Image;
import helpers.MediaFiles;


public class SlideshowDialogFragment extends DialogFragment {
    private String TAG = SlideshowDialogFragment.class.getSimpleName();
    private ArrayList<Image> images;
    private ViewPager viewPager;
    private MyViewPagerAdapter myViewPagerAdapter;
    private TextView lblCount, lblTitle, lblDate;
    private int selectedPosition = 0;

    public static final int FILE_VIDEO = 100;
    public static final int FILE_IMAGE = 101;
    private int MEDIA_TYPE;
    private MediaController mc;

    static SlideshowDialogFragment newInstance() {
        SlideshowDialogFragment f = new SlideshowDialogFragment();
        return f;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_slideshow_dialog, container, false);


        viewPager = (ViewPager) v.findViewById(R.id.viewpagerss);
        lblCount = (TextView) v.findViewById(R.id.lbl_count);
        lblTitle = (TextView) v.findViewById(R.id.titles);
        lblDate = (TextView) v.findViewById(R.id.date);

        final ImageButton imageButton = (ImageButton)v.findViewById(R.id.imageDownloadButton);


        MEDIA_TYPE = getArguments().getInt("MEDIA_TYPE");

        if(MEDIA_TYPE == FILE_IMAGE) {
            images = (ArrayList<Image>) getArguments().getSerializable("images");
        }
        else {
            images = (ArrayList<Image>) getArguments().getSerializable("images");
        }

        selectedPosition = getArguments().getInt("position");

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String filepath = images.get(selectedPosition).getLarge();
                MediaFiles.copyToDownload(filepath);
                Toast.makeText(getActivity(),"File saved!",Toast.LENGTH_SHORT).show();
                Animation anims = AnimationUtils.loadAnimation(getContext(), R.anim.blink);
                imageButton.startAnimation(anims);
                Log.d("ImageButton", "Pressed");
            }
        });

        Log.e(TAG, "position: " + selectedPosition);
        Log.e(TAG, "images size: " + images.size());

        myViewPagerAdapter = new MyViewPagerAdapter();
        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);

        setCurrentItem(selectedPosition);

        return v;
    }

    private void setCurrentItem(int position) {
        viewPager.setCurrentItem(position, false);
        displayMetaInfo(selectedPosition);
    }

    //  page change listener
    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            displayMetaInfo(position);
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

    private void displayMetaInfo(int position) {


        if(MEDIA_TYPE == FILE_IMAGE){
            lblCount.setText((position + 1) + " of " + images.size());
            Image image = images.get(position);
            lblTitle.setText(image.getSize());
            lblDate.setText(image.getTimestamp());
        }
        else{
            lblCount.setText((position + 1) + " of " + images.size());
            Image video =images.get(position);
            lblTitle.setText(video.getSize());
            lblDate.setText(video.getTimestamp());
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
    }


    //	adapter
    public class MyViewPagerAdapter extends PagerAdapter {

        private LayoutInflater layoutInflater;

        public MyViewPagerAdapter() {
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(R.layout.image_fullscreen_preview, container, false);

            if(MEDIA_TYPE == FILE_IMAGE){
                PhotoView photoView = (PhotoView) view.findViewById(R.id.image_preview);
                photoView.setVisibility(View.VISIBLE);

                Image image = images.get(position);

                Glide.with(getActivity()).load(image.getLarge())
                        .thumbnail(0.5f)
                        .crossFade()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(photoView);

                container.addView(view);
            }
            else{

                Image video = images.get(position);

                final VideoView videoView = (VideoView) view.findViewById(R.id.video_preview);

                videoView.setVisibility(View.VISIBLE);
                Log.d("Video to be played ",video.getLarge());
                String large = video.getLarge();
                videoView.setVideoURI(Uri.parse(large));

                videoView.requestFocus();

                videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mediaPlayer) {
                        Log.d("On prepared ", "video prepared");
                        mc = new MediaController(getActivity());
                        videoView.setMediaController(mc);
                        mc.setAnchorView(videoView);
                    }
                });

                videoView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        videoView.pause();
                    }
                });

               videoView.setOnTouchListener(new View.OnTouchListener() {
                   @Override
                   public boolean onTouch(View view, MotionEvent motionEvent) {
                       if(videoView.isPlaying()){
                           videoView.pause();
                       }
                       else{
                           videoView.start();
                       }
                       return false;
                   }
               });


                videoView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View view, boolean hasFocus) {
                        if (hasFocus)
                            videoView.start();
                        else
                            videoView.pause();
                    }
                });


                container.addView(view);

            }

            return view;
        }


        @Override
        public int getCount() {
            return images.size();

        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == ((View) obj);
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }


}