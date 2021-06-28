package co.realinventor.statusmanager.Helpers

import android.content.Context
import android.graphics.Rect
import android.net.Uri
import android.os.Build
import android.view.*
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import co.realinventor.statusmanager.GlideApp
import co.realinventor.statusmanager.R
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import helpers.Image
import java.io.File


class GalleryAdapter(private val mContext: Context, private val images: List<Image>) : RecyclerView.Adapter<GalleryAdapter.MyViewHolder>() {

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var thumbnail: ImageView = view.findViewById(R.id.thumbnail) as ImageView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.gallery_thumbnail, parent, false)

        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val image : Image = images[position]

        if(image.isVideo()) {
            val viewOverlay = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                holder.thumbnail.overlay
            } else {
                TODO("VERSION.SDK_INT < JELLY_BEAN_MR2")
            }
            val drawable = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mContext.getDrawable(R.drawable.video_layered)
            } else {
                TODO("VERSION.SDK_INT < LOLLIPOP")
            }
            drawable!!.bounds = Rect(
                    0,
                    0,
                    holder.thumbnail.width,
                    holder.thumbnail.height
            )
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                viewOverlay.add(drawable)
            }
        }

        GlideApp.with(mContext).load(Uri.fromFile(File(image.getLarge())))
                .thumbnail(0.5f)
                .transition(withCrossFade())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.thumbnail)

    }

    override fun getItemCount(): Int {
        return images.size
    }

    interface ClickListener {
        fun onClick(view: View, position: Int)

        fun onLongClick(view: View?, position: Int)
    }

    class RecyclerTouchListener(context: Context, recyclerView: RecyclerView, private val clickListener: GalleryAdapter.ClickListener?) : RecyclerView.OnItemTouchListener {

        private val gestureDetector: GestureDetector

        init {
            gestureDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
                override fun onSingleTapUp(e: MotionEvent): Boolean {
                    val child = recyclerView.findChildViewUnder(e.x, e.y)
                    if (child != null && clickListener != null) {
                        clickListener.onClick(child, recyclerView.getChildAdapterPosition(child))
                    }
                    return true
                }

                override fun onLongPress(e: MotionEvent) {
                    val child = recyclerView.findChildViewUnder(e.x, e.y)
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildAdapterPosition(child))
                    }
                }
            })
        }

        override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {

//            val child = rv.findChildViewUnder(e.x, e.y)
//            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
//                clickListener.onClick(child, rv.getChildPosition(child))
//            }
//            return false
            return gestureDetector?.onTouchEvent(e) ?: false
        }

        override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {}

        override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {

        }
    }
}