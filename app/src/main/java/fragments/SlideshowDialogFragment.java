package fragments;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import co.realinventor.statusmanager.R;
import helpers.Favourites;
import helpers.Image;
import helpers.MediaFiles;


public class SlideshowDialogFragment extends DialogFragment {
    private String TAG = SlideshowDialogFragment.class.getSimpleName();
    private ArrayList<Image> images;
    private ViewPager viewPager;
    private MyViewPagerAdapter myViewPagerAdapter;
    private TextView lblCount, lblTitle, lblDate;
    private int selectedPosition = 0;
    private MediaController mc;
    private ImageButton imageDownload,imageShare,imageLove;

    static SlideshowDialogFragment newInstance() {
        SlideshowDialogFragment f = new SlideshowDialogFragment();
        return f;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_slideshow_dialog, container, false);


        viewPager = (ViewPager) v.findViewById(R.id.viewpagerss);
        viewPager.setOffscreenPageLimit(0);
        lblCount = (TextView) v.findViewById(R.id.lbl_count);
        lblTitle = (TextView) v.findViewById(R.id.titles);
        lblDate = (TextView) v.findViewById(R.id.date);

        imageDownload = (ImageButton)v.findViewById(R.id.imageDownloadButton);
        imageShare = (ImageButton)v.findViewById(R.id.imageShareButton);
        imageLove =(ImageButton)v.findViewById(R.id.imageLoveButton);


        images = (ArrayList<Image>) getArguments().getSerializable("images");

        selectedPosition = getArguments().getInt("position");

        Log.e(TAG, "position: " + selectedPosition);
        Log.e(TAG, "images size: " + images.size());

        myViewPagerAdapter = new MyViewPagerAdapter();
        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);

        setCurrentItem(selectedPosition);

        return v;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if(isVisibleToUser){
            Toast.makeText(getActivity(),"Visible to user frag "+selectedPosition,Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(getActivity(),"Not visible frag "+selectedPosition,Toast.LENGTH_SHORT).show();
        }
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
            setListeners(position);
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

    private void setListeners(int position){
        final int selectedPosition = position;
        imageDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String filepath = images.get(selectedPosition).getLarge();
                Log.d("File to be downloaded",filepath);
                MediaFiles.copyToDownload(filepath);
                Toast.makeText(getActivity(),"File saved!",Toast.LENGTH_SHORT).show();
                Animation anims = AnimationUtils.loadAnimation(getContext(), R.anim.blink);
                imageDownload.startAnimation(anims);
                Log.d("ImageButton", "Pressed");
            }
        });

        imageLove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(getActivity(),"Added to favourites!",Toast.LENGTH_SHORT).show();
                imageLove.setBackgroundResource(R.drawable.ic_action_love_red);
                Animation anims = AnimationUtils.loadAnimation(getContext(), R.anim.blink);
                imageLove.startAnimation(anims);
                anims.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        imageLove.setBackgroundResource(R.drawable.ic_action_love);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                Log.d("ImageButton", "Pressed");
                addFavs(images.get(selectedPosition).getLarge());
            }
        });



        imageShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "Preparing file for sharing..", Toast.LENGTH_SHORT).show();
                String filepath = images.get(selectedPosition).getLarge();
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(filepath));
                shareIntent.setType("image/jpeg");
                startActivity(Intent.createChooser(shareIntent, "Shared with StatusManager"));
            }
        });
    }

    private void displayMetaInfo(int position) {

        lblCount.setText((position + 1) + " of " + images.size());
        Image image = images.get(position);
        lblTitle.setText(image.getSize());
        lblDate.setText(image.getTimestamp());

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
    }


    //Private File Access
    private void addFavs(String favs){
        favs = favs.replace(Environment.getExternalStorageDirectory() + "/Whatsapp/Media/.Statuses/","");
        Log.d("Filename to be written",favs);
        boolean readingSuccess = false;
        ArrayList<String> lines = new ArrayList<>();

        try{
            FileInputStream fis = getActivity().openFileInput(Favourites.FAV_FILENAME);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader bufferedReader = new BufferedReader(isr);

            String line;

            while ((line = bufferedReader.readLine()) != null) {
                Log.d("String lines", line);
                lines.add(line);
            }
            readingSuccess = true;
        }
        catch (FileNotFoundException e){
            Log.e("File open ", "File not found..");
        }
        catch (IOException ios){
            Log.e("File read", "Error reading file");
        }

        if(readingSuccess){
            boolean entry_exists = false;
            for(String ln : lines){
                if(favs.equals(ln)){
                    entry_exists = true;
                    break;
                }
            }
            if(!entry_exists){
                try{
                    FileOutputStream fos = getActivity().openFileOutput(Favourites.FAV_FILENAME, Context.MODE_APPEND);
                    fos.write("\n".getBytes());
                    fos.write(favs.getBytes());
                    fos.close();
                }
                catch (IOException ioexc){
                    ioexc.printStackTrace();
                }

            }

        }


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


            if(!(images.get(position).isVideo())){
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