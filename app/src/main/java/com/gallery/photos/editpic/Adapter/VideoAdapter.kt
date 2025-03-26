package com.gallery.photos.editpic.Adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gallery.photos.editpic.Extensions.formatDate
import com.gallery.photos.editpic.Extensions.gone
import com.gallery.photos.editpic.Extensions.visible
import com.gallery.photos.editpic.Model.VideoModel
import com.gallery.photos.editpic.R
import com.gallery.photos.editpic.Utils.SelectionModeListener
import com.gallery.photos.editpic.Views.FastScroller
import com.gallery.photos.editpic.databinding.ItemVideoBinding
import java.lang.ref.WeakReference

class VideoAdapter(
    var activity: Activity,
    private val listener: SelectionModeListener,
    var onItemClick: (VideoModel) -> Unit,
    var onLongItemClick: (Boolean) -> Unit
) :
    ListAdapter<VideoModel, VideoAdapter.VideoViewHolder>(VideoModel.DiffCallback()),
    FastScroller.SectionIndexer {

    class VideoViewHolder(val binding: ItemVideoBinding) : RecyclerView.ViewHolder(binding.root)
    val selectedItems = HashSet<VideoModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val binding = ItemVideoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VideoViewHolder(binding)
    }

    private val weakActivity = WeakReference(activity)

    private fun getSafeActivity(): Activity? {
        return weakActivity.get()?.takeIf { !it.isFinishing && !it.isDestroyed }
    }

    @SuppressLint("CheckResult", "ResourceType")
    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        val item = currentList[position]
        val activity = getSafeActivity() ?: return

        holder.binding.apply {

            Glide.with(activity)
                .load(item.videoPath)
                .error(ContextCompat.getColor(activity, R.color.ripple_color))
                .placeholder(ContextCompat.getColor(activity, R.color.ripple_color))
                .centerCrop()
                .into(imageViewMedia)
            tvDuration.text = formatDuration(item.videoDuration)

            ivFav.visibility = if (item.isFav) View.VISIBLE else View.GONE

            if (selectedItems.isNotEmpty()) {
                // We are in selection mode
                if (selectedItems.contains(item)) {
                    // Item is selected → Show BLUE CHECK icon
                    selectionOverlay.visibility = View.VISIBLE
                    selectionOverlay.setImageResource(R.drawable.baseline_check_circle_24) // Blue icon
                    selectionOverlay1.visibility = View.GONE // Hide unselected icon
                    selectView.visible()
                } else {
                    // Item is NOT selected → Show GREY UNSELECTED icon
                    selectionOverlay.visibility = View.GONE
                    selectionOverlay1.visibility = View.VISIBLE
                    selectionOverlay1.setImageResource(R.drawable.baseline_radio_button_unchecked_24) // Grey icon
                    selectView.gone()
                }
            } else {
                // Selection mode is NOT active → Hide both icons
                selectionOverlay.visibility = View.GONE
                selectionOverlay1.visibility = View.GONE
                selectView.visibility = View.GONE
            }

            root.setOnClickListener {
                if (selectedItems.isEmpty()) {
                    val mediaList = currentList
                    val correctPosition = mediaList.indexOf(item)

                    if (correctPosition != -1) {
                        Log.e(
                            "TAGdd",
                            "Adapter clicked: $correctPosition, Media: ${item.videoPath}"
                        )
                        onItemClick!!.invoke(item)
                    }
                } else {
                    toggleSelection(item)
                }
            }

            root.setOnLongClickListener {
                enableSelectionMode()
                toggleSelection(item)
                onLongItemClick.invoke(true)
                true
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun toggleSelection(media: VideoModel) {
        try {

        if (selectedItems.contains(media)) {
            selectedItems.remove(media)
        } else {
            selectedItems.add(media)
        }

            // Safe update of title text
            activity?.findViewById<TextView>(R.id.tvTitalVideo)?.text =
                if (selectedItems.isEmpty()) {
                    activity.getString(R.string.videos)
                } else {
                "${selectedItems.size} selected"
                }

            if (selectedItems.isEmpty()) {
                disableSelectionMode()
        }
        notifyDataSetChanged()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun selectAllItems() {
        val allItems = currentList

        if (selectedItems.size == allItems.size) {
            selectedItems.clear()
            disableSelectionMode()
        } else {
            selectedItems.clear()
            selectedItems.addAll(allItems)
            activity?.findViewById<TextView>(R.id.tvTitalVideo)?.text =
                "${selectedItems.size} selected"
            enableSelectionMode()
        }
        updateSelectionUI()
    }

    fun unselectAllItems() {
        selectedItems.clear()
        activity?.findViewById<TextView>(R.id.tvTitalVideo)?.text =
            activity?.getString(R.string.videos)
        disableSelectionMode()
        updateSelectionUI()
    }

    fun deleteSelectedItems() {
        if (selectedItems.isEmpty()) return

        val updatedList = currentList.filter {
            !selectedItems.contains(it)
        }

        selectedItems.clear() // Clear the selection after deletion
        submitList(updatedList) // Update the adapter with the new list
        disableSelectionMode() // Exit selection mode
    }


    @SuppressLint("NotifyDataSetChanged")
    private fun enableSelectionMode() {
        listener.toggleTopBar(true) // Use the listener
        notifyDataSetChanged()
        /*notifyDataSetChanged()
        (activity as? AllVideosFragment)?.toggleTopBar(true) // Hide top bar
        notifyDataSetChanged() // Refresh list to show selection icons*/
    }

    @SuppressLint("NotifyDataSetChanged")
    fun disableSelectionMode() {
        selectedItems.clear()
        listener.toggleTopBar(false)
//        (activity as AllVideosFragment).toggleTopBarVisibility(false)
        notifyDataSetChanged()
        onLongItemClick.invoke(false)
    }

    private fun updateSelectionUI() {
//        actionbarTxt.text = "${selectedItems.size} Selected"

        // Notify only media items to refresh their UI (avoiding unnecessary reloads)
        currentList.forEachIndexed { index, item ->
            notifyItemChanged(index) // Refresh only media items
        }
    }

    @SuppressLint("DefaultLocale")
    private fun formatDuration(durationMillis: Long): String {
        val seconds = durationMillis / 1000
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60
        return String.format("%02d:%02d", minutes, remainingSeconds)
    }

    private fun findNearestHeader(position: Int): String {
        for (i in currentList) { // Search upwards for the latest header
            return formatDate(i.videoDateAdded) // Return the found header's date
        }
        return "Unknown" // Fallback if no header is found
    }

    override fun getSectionText(position: Int) = findNearestHeader(position)

}
