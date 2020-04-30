package ru.bingosoft.teploObhod.util.photoSliderHelper

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import ru.bingosoft.teploObhod.R
import java.io.File


class HorizontalAdapter(val horizontalList: List<String>, val _pager: ViewPager, val ctx: Context) :
    RecyclerView.Adapter<HorizontalAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HorizontalAdapter.MyViewHolder {
        val itemView: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_thumb_image, parent, false)

        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return horizontalList.size
    }

    override fun onBindViewHolder(holder: HorizontalAdapter.MyViewHolder, position: Int) {
        val options: RequestOptions = RequestOptions()
            .placeholder(R.drawable.placeholder)
            .error(R.drawable.placeholder)

        val file = File(horizontalList[position])
        val imageUri: Uri = Uri.fromFile(file)

        Glide.with(ctx).load(imageUri)
            .apply(options)
            .into(holder.imgCards)

        holder.imgCards.setOnClickListener { _pager.setCurrentItem(position) }
    }

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var imgCards: ImageView = view.findViewById(R.id.imgDisplay) as ImageView

    }
}