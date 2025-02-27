package com.gallery.photos.editphotovideo.callendservice.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gallery.photos.editpic.Activity.MainActivity
import com.gallery.photos.editpic.Extensions.beGone
import com.gallery.photos.editpic.Extensions.beVisible
import com.gallery.photos.editpic.Model.MediaListItem
import com.gallery.photos.editpic.Model.MediaModel
import com.gallery.photos.editpic.R
import com.gallery.photos.editpic.Views.FastScroller
import com.gallery.photos.editpic.databinding.ItemMediaCallBinding
import com.gallery.photos.editpic.databinding.ItemMediaDateHeaderBinding
import java.io.File

class AllVideoAdaptercall(
    private val activity: AppCompatActivity,
    private val onItemClick: (List<MediaModel>, Int) -> Unit,
) : ListAdapter<MediaListItem, RecyclerView.ViewHolder>(DiffCallback()),
    FastScroller.SectionIndexer {
    companion object {
        private const val VIEW_TYPE_HEADER = 0
        private const val VIEW_TYPE_MEDIA = 1
    }

    private val selectedItems = HashSet<MediaModel>()


    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is MediaListItem.Header -> VIEW_TYPE_HEADER
            is MediaListItem.Media -> VIEW_TYPE_MEDIA
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_HEADER -> {
                val binding = ItemMediaDateHeaderBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                HeaderViewHolder(binding)
            }

            VIEW_TYPE_MEDIA -> {
                val binding = ItemMediaCallBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
//                MediaViewHolder(binding)
                MediaViewHolder(binding, onItemClick, currentList)
            }

            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is MediaListItem.Header -> (holder as HeaderViewHolder).bind(item)
            is MediaListItem.Media -> (holder as MediaViewHolder).bind(item.media)
        }
    }

    inner class HeaderViewHolder(private val binding: ItemMediaDateHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(header: MediaListItem.Header) {
            binding.textViewDate.text = header.date
        }
    }

    inner class MediaViewHolder(
        private val binding: ItemMediaCallBinding,
        private val onItemClick: (List<MediaModel>, Int) -> Unit,
        private val allItems: List<MediaListItem>,
    ) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("ResourceType")
        fun bind(media: MediaModel) {


            Glide.with(binding.imageViewMedia.context).load(media.mediaPath)
                .placeholder(Color.WHITE).error(R.drawable.ic_placeholder).centerCrop()
                .into(binding.imageViewMedia)

            binding.imageViewVideoIcon.visibility = if (media.isVideo) View.VISIBLE else View.GONE

            if (media.isFav) {
                binding.ivFav.beVisible()
            } else {
                binding.ivFav.beGone()
            }

            if (selectedItems.isNotEmpty()) {
                // We are in selection mode
                if (selectedItems.contains(media)) {
                    // Item is selected → Show BLUE CHECK icon
                    binding.selectionOverlay.visibility = View.VISIBLE
                    binding.selectionOverlay.setImageResource(R.drawable.baseline_check_circle_24) // Blue icon
                    binding.selectionOverlay1.visibility = View.GONE // Hide unselected icon
                } else {
                    // Item is NOT selected → Show GREY UNSELECTED icon
                    binding.selectionOverlay.visibility = View.GONE
                    binding.selectionOverlay1.visibility = View.VISIBLE
                    binding.selectionOverlay1.setImageResource(R.drawable.baseline_radio_button_unchecked_24) // Grey icon
                }
            } else {
                // Selection mode is NOT active → Hide both icons
                binding.selectionOverlay.visibility = View.GONE
                binding.selectionOverlay1.visibility = View.GONE
            }

            binding.root.setOnClickListener {


                activity.startActivity(Intent(activity, MainActivity::class.java))
                activity.finish()
            }
        }
    }


    private fun shareSelectedItems() {
        if (selectedItems.isEmpty()) return // No items selected

        val maxShareLimit = 30
        if (selectedItems.size > maxShareLimit) {
            Toast.makeText(
                activity, "Uh oh! You can only share 100 items at once", Toast.LENGTH_SHORT
            ).show()
            return
        }

        val uris = ArrayList<Uri>()
        var mimeType: String? = null

        selectedItems.forEach { media ->
            val fileUri = FileProvider.getUriForFile(
                activity, "${activity.packageName}.provider", File(media.mediaPath)
            )
            uris.add(fileUri)

            // Detect MIME type dynamically
            val currentMimeType = when {
                media.mediaPath.endsWith(".mp4", true) || media.mediaPath.endsWith(
                    ".mkv", true
                ) || media.mediaPath.endsWith(".avi", true) -> "video/*"

                media.mediaPath.endsWith(".gif", true) -> "image/gif"

                media.mediaPath.endsWith(".jpg", true) || media.mediaPath.endsWith(
                    ".jpeg", true
                ) || media.mediaPath.endsWith(".png", true) -> "image/*"

                else -> "*/*" // Fallback for unknown files
            }

            // If different file types exist, use */* to support mixed content sharing
            mimeType =
                if (mimeType == null) currentMimeType else if (mimeType != currentMimeType) "*/*" else mimeType
        }

        if (uris.isNotEmpty()) {
            val shareIntent = Intent().apply {
                action = if (uris.size == 1) Intent.ACTION_SEND else Intent.ACTION_SEND_MULTIPLE
                type = mimeType
                if (uris.size == 1) {
                    putExtra(Intent.EXTRA_STREAM, uris.first()) // Single file sharing
                } else {
                    putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris) // Multiple file sharing
                }
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) // Important for sharing
            }

            activity.startActivity(Intent.createChooser(shareIntent, "Share media via"))
        }
    }


    class DiffCallback : DiffUtil.ItemCallback<MediaListItem>() {
        override fun areItemsTheSame(oldItem: MediaListItem, newItem: MediaListItem) =
            oldItem == newItem

        override fun areContentsTheSame(oldItem: MediaListItem, newItem: MediaListItem) =
            oldItem == newItem
    }

    private fun findNearestHeader(position: Int): String {
        for (i in position downTo 0) { // Search upwards for the latest header
            val item = currentList.getOrNull(i)
            if (item is MediaListItem.Header) {
                return item.date // Return the found header's date
            }
        }
        return "Unknown" // Fallback if no header is found
    }

    override fun getSectionText(position: Int) = findNearestHeader(position)

}
