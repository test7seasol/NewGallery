package com.example.ekta.gallery.Adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.MediaController
import android.widget.SeekBar
import androidx.recyclerview.widget.RecyclerView
import com.gallery.photos.editpic.Model.MediaModel
import com.gallery.photos.editpic.Model.VideoModel
import com.gallery.photos.editpic.R
import com.gallery.photos.editpic.databinding.ItemViewPagerVideoBinding

class ViedoViewPagerAdapter(
    private val context: Context,
    private val videoList: ArrayList<VideoModel>
) : RecyclerView.Adapter<ViedoViewPagerAdapter.ViewHolder>() {

    private var currentPlayingViewHolder: ViewHolder? = null

    inner class ViewHolder(val binding: ItemViewPagerVideoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private var seekBarUpdateRunnable: Runnable? = null

        fun bind(videoModel: VideoModel) {
            Log.d("VideoAdapter", "Binding video: ${videoModel.videoPath}")


            binding.videoView.setVideoPath(videoModel.videoPath)
            val mediaController = MediaController(context)
            mediaController.setAnchorView(binding.videoView)
            // binding.videoView.setMediaController(mediaController)  // Optional

            // 🔹 Ensure SeekBar starts from 0 every time
            binding.seekBar.progress = 0

            // 🔹 Ensure the video starts from the beginning
            binding.videoView.seekTo(0)

            binding.videoView.setOnPreparedListener { mediaPlayer ->
                binding.seekBar.max = mediaPlayer.duration
                binding.videoView.start()  // Auto-play video
                binding.btnPlayPause.setImageResource(R.drawable.ic_pause_vector)
                startSeekBarUpdate()
            }

            // 🔹 Play/Pause logic
            binding.btnPlayPause.setOnClickListener {
                if (binding.videoView.isPlaying) {
                    binding.videoView.pause()
                    binding.btnPlayPause.setImageResource(R.drawable.ic_play_vector)
                } else {
                    binding.videoView.start()
                    binding.btnPlayPause.setImageResource(R.drawable.ic_pause_vector)
                    startSeekBarUpdate()
                }
            }

            // 🔹 SeekBar Change Listener
            binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    if (fromUser) binding.videoView.seekTo(progress)
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            })

            // 🔹 Reset video & SeekBar when the video completes
            binding.videoView.setOnCompletionListener {
                binding.seekBar.progress = 0
                binding.btnPlayPause.setImageResource(R.drawable.ic_play_vector)
                stopSeekBarUpdate()
            }
        }

        // ✅ Method to update SeekBar progress
        private fun startSeekBarUpdate() {
            stopSeekBarUpdate() // Stop any existing updates first
            seekBarUpdateRunnable = object : Runnable {
                override fun run() {
                    if (binding.videoView.isPlaying) {
                        binding.seekBar.progress = binding.videoView.currentPosition
                        binding.seekBar.postDelayed(this, 500)  // Update every 500ms
                    }
                }
            }
            binding.seekBar.postDelayed(seekBarUpdateRunnable!!, 500)
        }

        // ✅ Stop SeekBar updates
        private fun stopSeekBarUpdate() {
            seekBarUpdateRunnable?.let { binding.seekBar.removeCallbacks(it) }
        }

        // ✅ Stop video when ViewHolder is not visible
        fun stopVideo() {
            if (binding.videoView.isPlaying) {
                binding.videoView.pause()
                binding.videoView.seekTo(0)  // Ensure it starts from the beginning
                binding.btnPlayPause.setImageResource(R.drawable.ic_play_vector)
            }
            stopSeekBarUpdate()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemViewPagerVideoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    fun onDestroyVideoView(){
        currentPlayingViewHolder?.stopVideo()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // ✅ Stop the previous video's playback before playing the new one
        currentPlayingViewHolder?.stopVideo()

        currentPlayingViewHolder = holder
        holder.binding.seekBar.progress = 0

        holder.bind(videoList[position])
    }

    override fun getItemCount(): Int = videoList.size
}
