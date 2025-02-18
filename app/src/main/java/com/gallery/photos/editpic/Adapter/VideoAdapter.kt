package com.gallery.photos.editpic.Adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gallery.photos.editpic.Extensions.formatDate
import com.gallery.photos.editpic.Extensions.gone
import com.gallery.photos.editpic.Extensions.log
import com.gallery.photos.editpic.Extensions.visible
import com.gallery.photos.editpic.Fragment.AllVideosFragment
import com.gallery.photos.editpic.Model.VideoModel
import com.gallery.photos.editpic.R
import com.gallery.photos.editpic.Views.FastScroller
import com.gallery.photos.editpic.databinding.ItemVideoBinding

class VideoAdapter(var activity: AllVideosFragment, var onItemClick: (VideoModel) -> Unit
,var onLongItemClick:(Boolean) -> Unit) :
    ListAdapter<VideoModel, VideoAdapter.VideoViewHolder>(VideoModel.DiffCallback()),
    FastScroller.SectionIndexer {

    class VideoViewHolder(val binding: ItemVideoBinding) : RecyclerView.ViewHolder(binding.root)
    val selectedItems = HashSet<VideoModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val binding = ItemVideoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VideoViewHolder(binding)
    }

    @SuppressLint("CheckResult", "ResourceType")
    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        val item = currentList[position]
        holder.binding.apply {

            Glide.with(activity.requireActivity()).load(item.videoPath).error(activity.requireActivity().getColor(R.color.ripple_color)).placeholder(activity.requireActivity().getColor(R.color.ripple_color)).centerCrop()
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
        if (selectedItems.contains(media)) {
            selectedItems.remove(media)
        } else {
            selectedItems.add(media)
        }

        if (selectedItems.isEmpty()) {
            disableSelectionMode()
        } else {
//            actionbarTxt.text = "${selectedItems.size} Selected"
            activity.requireActivity().findViewById<TextView>(R.id.tvTitalVideo).text =
                "${selectedItems.size} selected"
        }
        notifyDataSetChanged()
    }

    fun selectAllItems() {
        val allItems = currentList

        if (selectedItems.size == allItems.size) {
            ("11").log()
            selectedItems.clear() // Deselect all if already selected
            disableSelectionMode() // Exit selection mode if everythi
            // ng is deselected
        } else {
            activity.requireActivity().findViewById<TextView>(R.id.tvTitalVideo).setText("Videos")
            selectedItems.clear()
            selectedItems.addAll(allItems)
            activity.requireActivity().findViewById<TextView>(R.id.tvTitalVideo).text =
                "${selectedItems.size} selected"
            ("22").log("allItems: ${allItems.size}")
            enableSelectionMode() // Ensure selection mode stays active
        }
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

    fun unselectAllItems() {
        val allItems = currentList

        if (selectedItems.size == allItems.size) {
            ("11").log()
            selectedItems.clear() // Deselect all if already selected
            disableSelectionMode() // Exit selection mode if everything is deselected
        } else {
            selectedItems.clear()
            ("22").log("allItems: ${allItems.size}")
            disableSelectionMode() // Exit selection mode if everything is deselected
        }
        updateSelectionUI()
        activity.requireActivity().findViewById<TextView>(R.id.tvTitalVideo).text = "${selectedItems.size} selected"
    }


    @SuppressLint("NotifyDataSetChanged")
    private fun enableSelectionMode() {
        (activity as AllVideosFragment).toggleTopBarVisibility(true) // Hide top bar
        notifyDataSetChanged() // Refresh list to show selection icons
    }


    @SuppressLint("NotifyDataSetChanged")
    fun disableSelectionMode() {
        selectedItems.clear()
        (activity as AllVideosFragment).toggleTopBarVisibility(false)
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
