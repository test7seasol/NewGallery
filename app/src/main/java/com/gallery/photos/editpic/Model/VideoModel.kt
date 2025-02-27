package com.gallery.photos.editpic.Model

import androidx.annotation.Keep
import androidx.recyclerview.widget.DiffUtil

@Keep
data class VideoModel(
    val videoId: Long = 0L,
    var videoName: String = "",
    var videoPath: String = "",
    val videoSize: Long = 0L,
    val videoDuration: Long = 0L,
    val videoDateAdded: Long = 0L,
    val isSelect: Boolean = false,
    var isFav: Boolean = false,
) {
    class DiffCallback : DiffUtil.ItemCallback<VideoModel>() {
        override fun areItemsTheSame(oldItem: VideoModel, newItem: VideoModel) = oldItem.videoId == newItem.videoId
        override fun areContentsTheSame(oldItem: VideoModel, newItem: VideoModel) = oldItem == newItem
    }
}
