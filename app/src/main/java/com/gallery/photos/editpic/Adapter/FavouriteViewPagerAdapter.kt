package com.gallery.photos.editpic.Adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gallery.photos.editpic.Adapter.HideViewPagerAdapter.ImageViewHolder
import com.gallery.photos.editpic.Adapter.HideViewPagerAdapter.VideoViewHolder
import com.gallery.photos.editpic.Extensions.beVisible
import com.gallery.photos.editpic.Extensions.onClick
import com.gallery.photos.editpic.Model.FavouriteMediaModel
import com.gallery.photos.editpic.R
import com.gallery.photos.editpic.databinding.ItemViewPagerImageBinding
import com.gallery.photos.editpic.databinding.ItemViewPagerVideoBinding
import java.io.File

class FavouriteViewPagerAdapter(
    private val context: Context, private val imageList: ArrayList<FavouriteMediaModel>,
    var onClick: () -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_IMAGE = 1
        private const val VIEW_TYPE_VIDEO = 2
    }

    override fun getItemViewType(position: Int): Int {
        return if (isVideoFile(imageList[position].mediaPath)) VIEW_TYPE_VIDEO else VIEW_TYPE_IMAGE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_IMAGE) {
            val binding = ItemViewPagerImageBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
            ImageViewHolder(binding)
        } else {
            val binding = ItemViewPagerVideoBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
            VideoViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val mediaPath = imageList[position].mediaPath
        if (holder is ImageViewHolder) {
            holder.bind(mediaPath)
        } else if (holder is VideoViewHolder) {
            holder.bind(mediaPath)
        }
    }

    override fun getItemCount(): Int = imageList.size

    inner class ImageViewHolder(private val binding: ItemViewPagerImageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(imagePath: String) {
            Glide.with(context).load(imagePath).placeholder(R.color.ripple_color)
                .into(binding.photoView)

            binding.photoView.beVisible()
            binding.photoView.onClick {
                onClick.invoke()
            }
        }
    }

    inner class VideoViewHolder(private val binding: ItemViewPagerVideoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private var isPlaying = false

        fun bind(videoPath: String) {
            val videoUri = Uri.fromFile(File(videoPath))
            binding.videoView.setVideoURI(videoUri)

            // Show video UI components
            binding.videoView.beVisible()
            binding.btnPlayPause.beVisible()

            binding.videoView.setOnPreparedListener { mediaPlayer ->
                binding.seekBar.max = mediaPlayer.duration
                mediaPlayer.start() // *Auto-play the video when prepared*
                binding.btnPlayPause.setImageResource(R.drawable.ic_pause_vector)
                isPlaying = true
                updateSeekBar()
            }
            binding.root.onClick {
                onClick.invoke()
            }
            // Play/Pause logic
            binding.btnPlayPause.setOnClickListener {
                if (isPlaying) {
                    binding.videoView.pause()
                    binding.btnPlayPause.setImageResource(R.drawable.ic_play_vector)
                } else {
                    binding.videoView.start()
                    binding.btnPlayPause.setImageResource(R.drawable.ic_pause_vector)
                }
                isPlaying = !isPlaying
            }

            // Reset when video completes
            binding.videoView.setOnCompletionListener {
                binding.btnPlayPause.setImageResource(R.drawable.ic_play_vector)
                binding.seekBar.progress = 0
                isPlaying = false
            }

            // SeekBar logic
            binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?, progress: Int, fromUser: Boolean
                ) {
                    if (fromUser) binding.videoView.seekTo(progress)
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            })
        }

        private fun updateSeekBar() {
            binding.seekBar.postDelayed(object : Runnable {
                override fun run() {
                    if (binding.videoView.isPlaying) {
                        binding.seekBar.progress = binding.videoView.currentPosition
                        binding.seekBar.postDelayed(this, 1000)
                    }
                }
            }, 1000)
        }

        /* 🛑 Stop video playback when view is detached */
        fun stopVideoPlayback() {
            if (binding.videoView.isPlaying) {
                binding.videoView.stopPlayback()
                isPlaying = false
                binding.seekBar.progress = 0
                binding.btnPlayPause.setImageResource(R.drawable.ic_play_vector)
            }
        }
    }


    private fun isVideoFile(filePath: String): Boolean {
        val videoExtensions = listOf("mp4", "mkv", "mov", "avi", "wmv")
        val fileExtension = filePath.substringAfterLast('.', "").lowercase()
        return videoExtensions.contains(fileExtension)
    }
}
