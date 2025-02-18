package com.gallery.photos.editpic.Model

import androidx.annotation.Keep
import androidx.recyclerview.widget.DiffUtil

@Keep
data class VideoModel(
    val videoId: Long,
    var videoName: String,
    var videoPath: String,
    val videoSize: Long,
    val videoDuration: Long,
    val videoDateAdded: Long,
    val isSelect: Boolean = false,
    var isFav: Boolean = false,
) {
    class DiffCallback : DiffUtil.ItemCallback<VideoModel>() {
        override fun areItemsTheSame(oldItem: VideoModel, newItem: VideoModel) = oldItem.videoId == newItem.videoId
        override fun areContentsTheSame(oldItem: VideoModel, newItem: VideoModel) = oldItem == newItem
    }
}
