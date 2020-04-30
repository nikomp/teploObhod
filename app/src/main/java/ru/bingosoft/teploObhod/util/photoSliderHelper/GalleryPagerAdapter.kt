package ru.bingosoft.teploObhod.util.photoSliderHelper

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import ru.bingosoft.teploObhod.R
import java.io.File


class GalleryPagerAdapter(val _images: List<String>, val _pager: ViewPager, val ctx: Context) :
    PagerAdapter() {
    val _inflater: LayoutInflater =
        ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val itemView: View =
            _inflater.inflate(R.layout.pager_gallery_item, container, false)
        container.addView(itemView)

        val params = LinearLayout.LayoutParams(300, 260)
        params.setMargins(3, 3, 3, 3)

        val thumbView = ImageView(ctx)
        thumbView.tag = position
        thumbView.scaleType = ImageView.ScaleType.CENTER_CROP
        thumbView.layoutParams = params
        thumbView.minimumHeight = 260

        thumbView.setOnClickListener(View.OnClickListener {
            _pager.currentItem = position
        })

        val imageView: ImageView = itemView.findViewById<View>(R.id.image) as ImageView

        val options: RequestOptions = RequestOptions()
            .placeholder(R.drawable.placeholder)
            .error(R.drawable.placeholder)

        val file = File(_images[position])
        val imageUri: Uri = Uri.fromFile(file)

        Glide.with(ctx).load(imageUri)
            .apply(options)
            .into(imageView)

        imageView.setOnClickListener(
            OnImageClickListener(
                position,
                _images
            )
        )

        return itemView
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object` as LinearLayout
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as LinearLayout)
    }

    override fun getCount(): Int {
        return _images.size
    }

    private class OnImageClickListener(val position: Int, val _images: List<String>) :
        View.OnClickListener {
        override fun onClick(v: View?) {
            val list: ArrayList<String> = ArrayList(_images)
        }
    }


}