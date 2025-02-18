package com.gallery.photos.editpic.Adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gallery.photos.editpic.Activity.PictureActivity
import com.gallery.photos.editpic.Extensions.gone
import com.gallery.photos.editpic.Extensions.log
import com.gallery.photos.editpic.Extensions.visible
import com.gallery.photos.editpic.Fragment.AlbumFragment
import com.gallery.photos.editpic.Model.FolderModel
import com.gallery.photos.editpic.Model.MediaListItem
import com.gallery.photos.editpic.R
import com.gallery.photos.editpic.databinding.ItemFolderBinding

class FolderAdapter(
    var activity: Activity, val onLongItemClick: (Boolean) -> Unit
) : ListAdapter<FolderModel, FolderAdapter.FolderViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FolderViewHolder {
        val binding = ItemFolderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FolderViewHolder(binding)
    }

    private val recentFragment =
        activity.fragmentManager.findFragmentById(R.id.framecontainer) as? AlbumFragment

    val selectedItems = HashSet<FolderModel>()

    override fun onBindViewHolder(holder: FolderViewHolder, position: Int) {
        val folder = currentList[position] as FolderModel


        holder.binding.apply {
            textViewFolderName.text = folder.folderName
            textViewFileCount.text = "${folder.fileCount} items"
            textViewFolderSize.text = "${folder.totalSize / (1024 * 1024)} MB"

            Glide.with(imageViewThumbnail.context)
                .load(folder.thumbnail) // Load the latest media file as the thumbnail
                .placeholder(R.color.appgrey).error(R.color.appgrey).centerCrop()
                .into(imageViewThumbnail)


            root.setOnLongClickListener {
                enableSelectionMode()
                toggleSelection(folder)
                onLongItemClick.invoke(true)
                true
            }

            root.setOnClickListener {
                if (selectedItems.isEmpty()) {
                    val mediaList = currentList
                    val correctPosition = mediaList.indexOf(folder) // Find actual media index

                    if (correctPosition != -1) {

                        val intent = Intent(activity, PictureActivity::class.java).apply {
                            putExtra("BUCKET_ID", folder.bucketId) // Pass the bucket ID
                            putExtra("folderName", folder.folderName) // Pass the bucket ID
                        }
                        activity.startActivity(intent)
                    }
                } else {
                    toggleSelection(folder)
                }
            }

            if (selectedItems.isNotEmpty()) {
                // We are in selection mode
                if (selectedItems.contains(folder)) {
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
        }
    }

    class FolderViewHolder(val binding: ItemFolderBinding) : RecyclerView.ViewHolder(binding.root)

    class DiffCallback : DiffUtil.ItemCallback<FolderModel>() {
        override fun areItemsTheSame(oldItem: FolderModel, newItem: FolderModel): Boolean =
            oldItem.bucketId == newItem.bucketId

        override fun areContentsTheSame(oldItem: FolderModel, newItem: FolderModel): Boolean =
            oldItem == newItem
    }


    private fun enableSelectionMode() {
        recentFragment?.toggleTopBarVisibility(false) // Hide top bar
        notifyDataSetChanged() // Refresh list to show selection icons
    }


    fun disableSelectionMode() {
        activity.findViewById<RelativeLayout>(R.id.mainTopTabsContainer).visible()
        selectedItems.clear()
        recentFragment?.toggleTopBarVisibility(true)
        onLongItemClick.invoke(false)
        notifyDataSetChanged()
    }

    @SuppressLint("SetTextI18n")
    private fun toggleSelection(media: FolderModel) {
        if (selectedItems.contains(media)) {
            selectedItems.remove(media)
        } else {
            selectedItems.add(media)
        }

        if (selectedItems.isEmpty()) {
            disableSelectionMode()
        } else {
//            actionbarTxt.text = "${selectedItems.size} Selected"
            activity.findViewById<TextView>(R.id.tvAlbumeTitle).text =
                "${selectedItems.size} selected"
        }

        notifyDataSetChanged() // Refresh all items for correct UI update
    }

    fun selectAllItems() {
        val allItems = currentList

        if (selectedItems.size == allItems.size) {
            ("11").log()
            selectedItems.clear() // Deselect all if already selected
            disableSelectionMode() // Exit selection mode if everything is deselected
        } else {
            activity.findViewById<TextView>(R.id.tvAlbumeTitle).setText("Albums")
            selectedItems.clear()
            selectedItems.addAll(allItems)
            activity.findViewById<TextView>(R.id.tvAlbumeTitle).text =
                "${selectedItems.size} selected"
            ("22").log("allItems: ${allItems.size}")
            enableSelectionMode() // Ensure selection mode stays active
            activity.findViewById<RelativeLayout>(R.id.selectedcontaineralbumsid).visible()
            activity.findViewById<RelativeLayout>(R.id.mainTopTabsContainer).gone()
        }
        updateSelectionUI()
    }

    fun unselectAllItems() {
        val allItems = currentList.filterIsInstance<MediaListItem.Media>().map { it.media }

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
        activity.findViewById<TextView>(R.id.tvAlbumeTitle).text = "${selectedItems.size} selected"
    }

    private fun updateSelectionUI() {
//        actionbarTxt.text = "${selectedItems.size} Selected"
        currentList.forEachIndexed { index, item ->
            if (item is FolderModel) {
                notifyItemChanged(index) // Refresh only media items
            }
        }
    }
}
