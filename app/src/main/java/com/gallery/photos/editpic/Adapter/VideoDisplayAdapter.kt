package com.gallery.photos.editpic.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.VideoView
import androidx.appcompat.widget.AppCompatTextView
import androidx.cardview.widget.CardView
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.gallery.photos.editpic.Model.MediaModel
import com.gallery.photos.editpic.Model.VideoModel
import com.gallery.photos.editpic.R


class VideoDisplayAdapter(
    var context: Context,
    private var videosList: List<VideoModel>, private var videoPreviousNext: VideoPreviousNext?,
) : PagerAdapter() {

    private var onEverySecond: Runnable? = null
    private var h: Handler? = null
    private var run: Runnable? = null

    private var videoView: VideoView? = null
    private var loutCenter: RelativeLayout? = null
    private var btnPlayPause: CardView? = null
    private var ivCenterPlayPause: ImageView? = null
    private var previous: ImageView? = null
    private var next: ImageView? = null
    private var videoSeek: SeekBar? = null
    private var txtVideoCurrentDur: AppCompatTextView? = null
    private var txtVideoTotalDur: AppCompatTextView? = null
    private var loutBottom: LinearLayout? = null

    private var layoutInflater =
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    @SuppressLint("UseCompatLoadingForDrawables", "NewApi")
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val itemView = layoutInflater.inflate(R.layout.item_full_screen_video, container, false)
        loutCenter = itemView?.findViewById(R.id.lout_center)
        btnPlayPause = itemView?.findViewById(R.id.btn_play_pause)
        ivCenterPlayPause = itemView?.findViewById(R.id.iv_center_play_pause)
        previous = itemView?.findViewById(R.id.previous)
        next = itemView?.findViewById(R.id.next)
        videoView = itemView?.findViewById(R.id.video_view)
        videoSeek = itemView?.findViewById(R.id.video_seek)
        txtVideoCurrentDur = itemView?.findViewById(R.id.txt_video_current_dur)
        txtVideoTotalDur = itemView?.findViewById(R.id.txt_video_total_dur)
        loutBottom = itemView?.findViewById(R.id.lout_bottom)
        val ivDisplay =
            itemView?.findViewById<androidx.appcompat.widget.AppCompatImageView>(R.id.iv_display)
        val options = RequestOptions()
            .diskCacheStrategy(DiskCacheStrategy.ALL).override(900, 900).dontTransform()
        if (ivDisplay != null) {
            Glide.with(context).setDefaultRequestOptions(options)
                .load(videosList[position].videoPath)
                .into(ivDisplay)
        }

        run = Runnable {
            loutCenter!!.visibility = View.GONE
        }
        h = Handler(Looper.getMainLooper())
        onEverySecond = Runnable {
            if (videoSeek != null) {
                videoSeek!!.progress = videoView!!.currentPosition
                val duration = videoView!!.currentPosition / 1000
                val hours = duration / 3600
                val minutes = duration / 60 - hours * 60
                val seconds = duration - hours * 3600 - minutes * 60
                val formatted = String.format("%02d:%02d", minutes, seconds)
                txtVideoCurrentDur?.text = formatted
            }
            videoSeek?.postDelayed(onEverySecond, 1000)
            if (videoView!!.isPlaying) {
                ivCenterPlayPause!!.setImageDrawable(
                    context.getDrawable(R.drawable.ic_pause_vector)
                )
            } else {
                ivCenterPlayPause!!.setImageDrawable(
                    context.getDrawable(R.drawable.ic_play_vector)
                )
            }
        }


        if (videoView!!.isPlaying) {
            ivCenterPlayPause!!.setImageDrawable(
                context.getDrawable(R.drawable.ic_play_vector)
            )
            videoView!!.pause()
        } else {
            if (ivDisplay?.visibility == View.VISIBLE) ivDisplay.visibility = View.GONE
            ivCenterPlayPause!!.setImageDrawable(
                context.getDrawable(R.drawable.ic_pause_vector)
            )
            videoView!!.start()
            h!!.removeCallbacks(run!!)
            h!!.postDelayed(run!!, 2000)
        }

        btnPlayPause!!.setOnClickListener {
            if (videoView!!.isPlaying) {
                ivCenterPlayPause!!.setImageDrawable(
                    context.getDrawable(R.drawable.ic_play_vector)
                )
                videoView!!.pause()
            } else {
                if (ivDisplay?.visibility == View.VISIBLE) ivDisplay.visibility = View.GONE
                ivCenterPlayPause!!.setImageDrawable(
                    context.getDrawable(R.drawable.ic_pause_vector)
                )
                videoView!!.start()
                h!!.removeCallbacks(run!!)
                h!!.postDelayed(run!!, 2000)
            }
        }

        next!!.setOnClickListener { videoPreviousNext!!.onNext(position) }

        previous!!.setOnClickListener { videoPreviousNext!!.onPrevious(position) }

        videoView!!.setOnPreparedListener { mp ->
            ivCenterPlayPause!!.setImageDrawable(context.getDrawable(R.drawable.ic_play_vector))
            videoSeek?.max = videoView!!.duration
            val duration = mp.duration / 1000
            val hours = duration / 3600
            val minutes = duration / 60 - hours * 60
            val seconds = duration - hours * 3600 - minutes * 60
            val formatted = String.format("%02d:%02d", minutes, seconds)
            txtVideoTotalDur?.text = formatted
            videoSeek?.postDelayed(onEverySecond, 1000)
        }



        videoView!!.setOnCompletionListener {
            ivCenterPlayPause!!.visibility = View.VISIBLE
            ivCenterPlayPause!!.setImageDrawable(context.getDrawable(R.drawable.ic_play_vector))
            videoPreviousNext!!.onNext(position)
        }


        itemView.setOnClickListener {
            loutCenter!!.visibility = View.VISIBLE
            videoSeek?.visibility = View.VISIBLE
            txtVideoCurrentDur?.visibility = View.VISIBLE
            txtVideoTotalDur?.visibility = View.VISIBLE
            previous!!.visibility = View.VISIBLE
            next!!.visibility = View.VISIBLE
            btnPlayPause!!.visibility = View.VISIBLE
        }


        videoSeek?.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onStopTrackingTouch(seekBar: SeekBar) {
                videoView!!.start()
                ivCenterPlayPause!!.setImageDrawable(context.getDrawable(R.drawable.ic_pause_vector))
                if (videoView!!.isPlaying) {
                    videoSeek?.postDelayed(onEverySecond, 1000)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                videoView!!.pause()
            }

            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                val duration = videoView!!.currentPosition / 1000
                val hours = duration / 3600
                val minutes = duration / 60 - hours * 60
                val seconds = duration - hours * 3600 - minutes * 60
                val formatted = String.format("%02d:%02d", minutes, seconds)
                txtVideoCurrentDur?.text = formatted
                if (fromUser) {
                    videoView!!.seekTo(progress)
                }
            }
        })
        videoSeek?.progress = 0
        videoView!!.setVideoPath(videosList[position].videoPath)
        videoView!!.requestFocus()
        ivCenterPlayPause!!.setImageDrawable(context.getDrawable(R.drawable.ic_play_vector))
        container.addView(itemView)
        return itemView
    }

    override fun getCount(): Int {
        return videosList.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as RelativeLayout)
    }
}

interface VideoPreviousNext {
    fun onPrevious(currentPos: Int)
    fun onNext(currentPos: Int)
}
