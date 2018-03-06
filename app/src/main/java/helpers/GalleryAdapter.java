package helpers;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.ads.formats.MediaView;
import com.google.android.gms.ads.formats.NativeAd;
import com.google.android.gms.ads.formats.NativeAppInstallAd;
import com.google.android.gms.ads.formats.NativeAppInstallAdView;
import com.google.android.gms.ads.formats.NativeContentAd;
import com.google.android.gms.ads.formats.NativeContentAdView;
import java.io.File;
import java.util.List;
import co.realinventor.statusmanager.R;


/**
 * Created by JIMMY on 15-Feb-18.
 */

public class GalleryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    /** For integrating ads**/
    // A menu item view type.
    private static final int IMAGE_ASSET_TYPE = 0;

    // The native app install ad view type.
    private static final int NATIVE_APP_INSTALL_AD_VIEW_TYPE = 1;

    // The native content ad view type.
    private static final int NATIVE_CONTENT_AD_VIEW_TYPE = 2;
    /** For integrating ads**//////////


    private List<Object> images;
    private Context mContext;

    public GalleryAdapter(Context context, List<Object> images) {
        mContext = context;
        this.images = images;
    }



    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView thumbnail;

        public MyViewHolder(View view) {
            super(view);
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
        }
    }


    /** Custom view holder for native app install ad **/
    public class NativeAppInstallAdViewHolder extends RecyclerView.ViewHolder {
        NativeAppInstallAdViewHolder(View view) {
            super(view);
            NativeAppInstallAdView adView = (NativeAppInstallAdView) view;

            // Register the view used for each individual asset.
            // The MediaView will display a video asset if one is present in the ad, and the
            // first image asset otherwise.
            MediaView mediaView = (MediaView) adView.findViewById(R.id.appinstall_media);
            adView.setMediaView(mediaView);
            adView.setHeadlineView(adView.findViewById(R.id.appinstall_headline));
            adView.setBodyView(adView.findViewById(R.id.appinstall_body));
            adView.setCallToActionView(adView.findViewById(R.id.appinstall_call_to_action));
            adView.setIconView(adView.findViewById(R.id.appinstall_app_icon));
            adView.setPriceView(adView.findViewById(R.id.appinstall_price));
            adView.setStarRatingView(adView.findViewById(R.id.appinstall_stars));
            adView.setStoreView(adView.findViewById(R.id.appinstall_store));
        }
    }
    /** Custom view holder for native app install ad **/////////


    /** Custom view for native content ad **/
    public class NativeContentAdViewHolder extends RecyclerView.ViewHolder {
        NativeContentAdViewHolder(View view) {
            super(view);
            NativeContentAdView adView = (NativeContentAdView) view;

            // Register the view used for each individual asset.
            adView.setHeadlineView(adView.findViewById(R.id.contentad_headline));
            adView.setImageView(adView.findViewById(R.id.contentad_image));
            adView.setBodyView(adView.findViewById(R.id.contentad_body));
            adView.setCallToActionView(adView.findViewById(R.id.contentad_call_to_action));
            adView.setLogoView(adView.findViewById(R.id.contentad_logo));
            adView.setAdvertiserView(adView.findViewById(R.id.contentad_advertiser));
        }
    }
    /** Custom view for native content ad **/////////



    /**For selecting view types**/
    @Override
    public int getItemViewType(int position) {

        Object recyclerViewItem = images.get(position);
        if (recyclerViewItem instanceof NativeAppInstallAd) {
            return NATIVE_APP_INSTALL_AD_VIEW_TYPE;
        } else if (recyclerViewItem instanceof NativeContentAd) {
            return NATIVE_CONTENT_AD_VIEW_TYPE;
        }
        return IMAGE_ASSET_TYPE;
    }

    /**Populate NATIVE AD APP INSTALLED**/
    private void populateAppInstallAdView(NativeAppInstallAd nativeAppInstallAd,
                                          NativeAppInstallAdView adView) {

        // Some assets are guaranteed to be in every NativeAppInstallAd.
        ((ImageView) adView.getIconView()).setImageDrawable(nativeAppInstallAd.getIcon()
                .getDrawable());
        ((TextView) adView.getHeadlineView()).setText(nativeAppInstallAd.getHeadline());
        ((TextView) adView.getBodyView()).setText(nativeAppInstallAd.getBody());
        ((Button) adView.getCallToActionView()).setText(nativeAppInstallAd.getCallToAction());

        // These assets aren't guaranteed to be in every NativeAppInstallAd, so it's important to
        // check before trying to display them.
        if (nativeAppInstallAd.getPrice() == null) {
            adView.getPriceView().setVisibility(View.INVISIBLE);
        } else {
            adView.getPriceView().setVisibility(View.VISIBLE);
            ((TextView) adView.getPriceView()).setText(nativeAppInstallAd.getPrice());
        }

        if (nativeAppInstallAd.getStore() == null) {
            adView.getStoreView().setVisibility(View.INVISIBLE);
        } else {
            adView.getStoreView().setVisibility(View.VISIBLE);
            ((TextView) adView.getStoreView()).setText(nativeAppInstallAd.getStore());
        }

        if (nativeAppInstallAd.getStarRating() == null) {
            adView.getStarRatingView().setVisibility(View.INVISIBLE);
        } else {
            ((RatingBar) adView.getStarRatingView())
                    .setRating(nativeAppInstallAd.getStarRating().floatValue());
            adView.getStarRatingView().setVisibility(View.VISIBLE);
        }

        // Assign native ad object to the native view.
        adView.setNativeAd(nativeAppInstallAd);
    }

    /**Populate NATIVE AD APP INSTALLED**/
    private void populateContentAdView(NativeContentAd nativeContentAd,
                                       NativeContentAdView adView) {
        // Some assets are guaranteed to be in every NativeContentAd.
        ((TextView) adView.getHeadlineView()).setText(nativeContentAd.getHeadline());
        ((TextView) adView.getBodyView()).setText(nativeContentAd.getBody());
        ((TextView) adView.getCallToActionView()).setText(nativeContentAd.getCallToAction());
        ((TextView) adView.getAdvertiserView()).setText(nativeContentAd.getAdvertiser());

        List<NativeAd.Image> images = nativeContentAd.getImages();

        if (images.size() > 0) {
            ((ImageView) adView.getImageView()).setImageDrawable(images.get(0).getDrawable());
        }

        // Some aren't guaranteed, however, and should be checked.
        NativeAd.Image logoImage = nativeContentAd.getLogo();

        if (logoImage == null) {
            adView.getLogoView().setVisibility(View.INVISIBLE);
        } else {
            ((ImageView) adView.getLogoView()).setImageDrawable(logoImage.getDrawable());
            adView.getLogoView().setVisibility(View.VISIBLE);
        }

        // Assign native ad object to the native view.
        adView.setNativeAd(nativeContentAd);
    }





    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        switch (viewType) {
            case NATIVE_APP_INSTALL_AD_VIEW_TYPE:
                View nativeAppInstallLayoutView = LayoutInflater.from(
                        parent.getContext()).inflate(R.layout.ad_app_install,
                        parent, false);
                return new NativeAppInstallAdViewHolder(nativeAppInstallLayoutView);
            case NATIVE_CONTENT_AD_VIEW_TYPE:
                View nativeContentLayoutView = LayoutInflater.from(
                        parent.getContext()).inflate(R.layout.ad_content,
                        parent, false);
                return new NativeContentAdViewHolder(nativeContentLayoutView);
            case IMAGE_ASSET_TYPE:
                // Fall through.
            default:
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.gallery_thumbnail, parent, false);

                return new MyViewHolder(itemView);
        }


//        View itemView = LayoutInflater.from(parent.getContext())
//                .inflate(R.layout.gallery_thumbnail, parent, false);
//
//        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        int viewType = getItemViewType(position);
        switch (viewType) {
            case NATIVE_APP_INSTALL_AD_VIEW_TYPE:
                NativeAppInstallAd appInstallAd = (NativeAppInstallAd) images.get(position);
                populateAppInstallAdView(appInstallAd, (NativeAppInstallAdView) holder.itemView);
                break;
            case NATIVE_CONTENT_AD_VIEW_TYPE:
                NativeContentAd contentAd = (NativeContentAd) images.get(position);
                populateContentAdView(contentAd, (NativeContentAdView) holder.itemView);
                break;
            case IMAGE_ASSET_TYPE:
                // fall through
            default:
                MyViewHolder myViewHolder = (MyViewHolder) holder;
                Image image = (Image)images.get(position);
                if(!image.isVideo()){

                    Glide.with(mContext).load(Uri.fromFile(new File(image.getLarge())))
                            .thumbnail(0.5f)
                            .crossFade()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(myViewHolder.thumbnail);

                }
                else{

                    Glide.with(mContext).load(Uri.fromFile(new File(image.getLarge())))
                            .thumbnail(0.5f)
                            .into(myViewHolder.thumbnail);
                }
        }



//        Image image = images.get(position);
//        if(!image.isVideo()){
//
//            Glide.with(mContext).load(Uri.fromFile(new File(image.getLarge())))
//                    .thumbnail(0.5f)
//                    .crossFade()
//                    .diskCacheStrategy(DiskCacheStrategy.ALL)
//                    .into(holder.thumbnail);
//
//        }
//        else{
//
//            Glide.with(mContext).load(Uri.fromFile(new File(image.getLarge())))
//                    .thumbnail(0.5f)
//                    .into(holder.thumbnail);
//        }
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private GalleryAdapter.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final GalleryAdapter.ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {

                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {}

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {}
    }
}