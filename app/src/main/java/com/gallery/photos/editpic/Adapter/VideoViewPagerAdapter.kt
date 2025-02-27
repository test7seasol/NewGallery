package com.gallery.photos.editpic.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gallery.photos.editpic.Model.DeleteMediaModel
import com.gallery.photos.editpic.Model.MediaModel
import com.gallery.photos.editpic.Model.VideoModel
import com.gallery.photos.editpic.R
import com.gallery.photos.editpic.databinding.ItemViewPagerImageBinding

class VideoViewPagerAdapter(private val context: Context, private val imageList: ArrayList<VideoModel>) :
    RecyclerView.Adapter<VideoViewPagerAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemViewPagerImageBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("ResourceType")
        fun bind(imagePath: String) {

            Log.e("TAGee", "bind: "+imagePath)
            Glide.with(context)
                .load(imagePath)
                .placeholder(R.color.ripple_color)
                .into(binding.photoView)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemViewPagerImageBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(imageList[position].videoPath)
    }

    override fun getItemCount(): Int = imageList.size
}
