package com.gallery.photos.editpic.Adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gallery.photos.editpic.Activity.PictureActivity
import com.gallery.photos.editpic.Extensions.gone
import com.gallery.photos.editpic.Extensions.log
import com.gallery.photos.editpic.Extensions.visible
import com.gallery.photos.editpic.Model.MediaModel
import com.gallery.photos.editpic.R
import com.gallery.photos.editpic.databinding.ItemPictureBinding

class PictureAdapter(
    var activity: Activity,
    private val onLongItemClick: (Boolean) -> Unit
) :
    ListAdapter<MediaModel, PictureAdapter.PictureViewHolder>(DiffCallback()) {

    var onItemClick: ((MediaModel) -> Unit)? = null

    inner class PictureViewHolder(var bind: ItemPictureBinding) :
        RecyclerView.ViewHolder(bind.root)

    val selectedItems = HashSet<MediaModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        PictureViewHolder(ItemPictureBinding.inflate(LayoutInflater.from(parent.context)))

    override fun onBindViewHolder(holder: PictureViewHolder, position: Int) {
        val item = currentList[position]

        holder.bind.apply {
            Glide.with(activity.applicationContext)
                .load(item.mediaPath)
                .placeholder(R.color.ripple_color)
                .error(R.color.ripple_color)
                .centerCrop()
                .into(imageViewMedia)

            imageViewVideoIcon.visibility = if (item.isVideo) View.VISIBLE else View.GONE
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
                            "Adapter clicked: $correctPosition, Media: ${item.mediaPath}"
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
    private fun toggleSelection(media: MediaModel) {
        if (selectedItems.contains(media)) {
            selectedItems.remove(media)
        } else {
            selectedItems.add(media)
        }

        if (selectedItems.isEmpty()) {
            disableSelectionMode()
        } else {
//            actionbarTxt.text = "${selectedItems.size} Selected"
            activity.findViewById<TextView>(R.id.tvAlbumName).text =
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
            activity.findViewById<TextView>(R.id.tvAlbumName).setText("Pictures")
            selectedItems.clear()
            selectedItems.addAll(allItems)
            activity.findViewById<TextView>(R.id.tvAlbumName).text =
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
        activity.findViewById<TextView>(R.id.tvAlbumName).text = "${selectedItems.size} selected"
    }


    @SuppressLint("NotifyDataSetChanged")
    private fun enableSelectionMode() {
        (activity as PictureActivity).toggleTopBarVisibility(true) // Hide top bar
        notifyDataSetChanged() // Refresh list to show selection icons
    }


    @SuppressLint("NotifyDataSetChanged")
    fun disableSelectionMode() {
        selectedItems.clear()
        (activity as PictureActivity).toggleTopBarVisibility(false)
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

    class DiffCallback : DiffUtil.ItemCallback<MediaModel>() {
        override fun areItemsTheSame(oldItem: MediaModel, newItem: MediaModel): Boolean {
            return oldItem.mediaId == newItem.mediaId
        }

        override fun areContentsTheSame(oldItem: MediaModel, newItem: MediaModel): Boolean {
            return oldItem == newItem
        }
    }
}
