package com.gallery.photos.editpic.Adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gallery.photos.editpic.Extensions.gone
import com.gallery.photos.editpic.Extensions.log
import com.gallery.photos.editpic.Extensions.visible
import com.gallery.photos.editpic.Fragment.RecentsPictureFragment
import com.gallery.photos.editpic.Model.MediaListItem
import com.gallery.photos.editpic.Model.MediaModel
import com.gallery.photos.editpic.PopupDialog.CustomPopup
import com.gallery.photos.editpic.R
import com.gallery.photos.editpic.databinding.ItemMediaBinding
import com.gallery.photos.editpic.databinding.ItemMediaDateHeaderBinding
import com.qtalk.recyclerviewfastscroller.RecyclerViewFastScroller
import java.io.File

class RecentPictureAdapter(
    private val activity: AppCompatActivity,
    private val onItemClick: (List<MediaModel>, Int) -> Unit,
    private val onLongItemClick: (Boolean) -> Unit
) : ListAdapter<MediaListItem, RecyclerView.ViewHolder>(DiffCallback()),
    RecyclerViewFastScroller.OnPopupViewUpdate {

    companion object {
        private const val VIEW_TYPE_HEADER = 0
        private const val VIEW_TYPE_MEDIA = 1
    }

    val selectedItems = HashSet<MediaModel>()

    private val recentFragment =
        activity.supportFragmentManager.findFragmentById(R.id.framecontainer) as? RecentsPictureFragment


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
                val binding = ItemMediaBinding.inflate(
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

    /*    inner class MediaViewHolder(private val binding: ItemMediaBinding) :
            RecyclerView.ViewHolder(binding.root) {

            fun bind(media: MediaModel) {
                Glide.with(binding.imageViewMedia.context)
                    .load(media.mediaPath)
                    .placeholder(R.drawable.ic_placeholder)
                    .error(R.drawable.ic_placeholder)
                    .centerCrop()
                    .into(binding.imageViewMedia)

                binding.imageViewVideoIcon.visibility = if (media.isVideo) View.VISIBLE else View.GONE

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
                    if (selectedItems.isEmpty()) {
                        onItemClick.invoke(
                            currentList.filterIsInstance<MediaListItem.Media>().map { it.media },
                            layoutPosition
                        )
                    } else {
                        toggleSelection(media)
                    }
                }

                binding.root.setOnLongClickListener {
                    enableSelectionMode()
                    toggleSelection(media)
                    true
                }
            }
        }*/

    inner class MediaViewHolder(
        private val binding: ItemMediaBinding,
        private val onItemClick: (List<MediaModel>, Int) -> Unit,
        private val allItems: List<MediaListItem>
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(media: MediaModel) {
            Glide.with(binding.imageViewMedia.context).load(media.mediaPath)
                .placeholder(R.color.appgrey).error(R.color.appgrey).centerCrop()
                .into(binding.imageViewMedia)

            binding.imageViewVideoIcon.visibility = if (media.isVideo) View.VISIBLE else View.GONE

            if (selectedItems.isNotEmpty()) {
                // We are in selection mode
                if (selectedItems.contains(media)) {
                    // Item is selected → Show BLUE CHECK icon
                    binding.selectionOverlay.visibility = View.VISIBLE
                    binding.selectionOverlay.setImageResource(R.drawable.baseline_check_circle_24) // Blue icon
                    binding.selectionOverlay1.visibility = View.GONE // Hide unselected icon
                    binding.selectView.visible()
                } else {
                    // Item is NOT selected → Show GREY UNSELECTED icon
                    binding.selectionOverlay.visibility = View.GONE
                    binding.selectionOverlay1.visibility = View.VISIBLE
                    binding.selectionOverlay1.setImageResource(R.drawable.baseline_radio_button_unchecked_24) // Grey icon
                    binding.selectView.gone()
                }
            } else {
                // Selection mode is NOT active → Hide both icons
                binding.selectionOverlay.visibility = View.GONE
                binding.selectionOverlay1.visibility = View.GONE
                binding.selectView.visibility = View.GONE
            }

//            binding.ivFav.visibility = if (media.isFav) View.VISIBLE else View.GONE
            binding.ivFav.visibility = if (media.isFav) View.VISIBLE else View.GONE

            binding.root.setOnClickListener {
                if (selectedItems.isEmpty()) {
                    val mediaList =
                        allItems.filterIsInstance<MediaListItem.Media>().map { it.media }
                    val correctPosition = mediaList.indexOf(media) // Find actual media index

                    if (correctPosition != -1) {
                        Log.e(
                            "TAGdd", "Adapter clicked: $correctPosition, Media: ${media.mediaPath}"
                        )
                        onItemClick.invoke(mediaList, correctPosition) // Pass correct list & index
                    }
                } else {
                    toggleSelection(media)
                }
            }

            binding.root.setOnLongClickListener {
                enableSelectionMode()
                toggleSelection(media)
                onLongItemClick.invoke(true)
                true
            }
        }
    }

    private fun enableSelectionMode() {
        recentFragment?.toggleTopBarVisibility(false) // Hide top bar
        notifyDataSetChanged() // Refresh list to show selection icons
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


    fun disableSelectionMode() {
        activity.findViewById<RelativeLayout>(R.id.mainTopTabsContainer).visible()
        selectedItems.clear()
        recentFragment?.toggleTopBarVisibility(true)
        onLongItemClick.invoke(false)
        notifyDataSetChanged()
    }

    @SuppressLint("SetTextI18n")
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
            activity.findViewById<TextView>(R.id.tvSelection).text =
                "${selectedItems.size} selected"
        }

        notifyDataSetChanged() // Refresh all items for correct UI update
    }

    fun selectAllItems() {
        val allItems = currentList.filterIsInstance<MediaListItem.Media>().map { it.media }

        if (selectedItems.size == allItems.size) {
            ("11").log()
            selectedItems.clear() // Deselect all if already selected
            disableSelectionMode() // Exit selection mode if everything is deselected
        } else {
            activity.findViewById<TextView>(R.id.tvSelection).setText("Pictures")
            selectedItems.clear()
            selectedItems.addAll(allItems)
            activity.findViewById<TextView>(R.id.tvSelection).text =
                "${selectedItems.size} selected"
            ("22").log("allItems: ${allItems.size}")
            enableSelectionMode() // Ensure selection mode stays active
            activity.findViewById<RelativeLayout>(R.id.selectedcontainerid).visible()
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
        activity.findViewById<TextView>(R.id.tvSelection).text = "${selectedItems.size} selected"
    }

    private fun updateSelectionUI() {
//        actionbarTxt.text = "${selectedItems.size} Selected"

        // Notify only media items to refresh their UI (avoiding unnecessary reloads)
        currentList.forEachIndexed { index, item ->
            if (item is MediaListItem.Media) {
                notifyItemChanged(index) // Refresh only media items
            }
        }
    }


    private fun shareSelectedItems() {
        if (selectedItems.isEmpty()) return // No items selected

        val maxShareLimit = 100
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

    fun openMenuDialog(view: View) {
        val customPopup = CustomPopup(activity, onClick = {
            when (it) {
                "selectallid" -> {
                    selectAllItems()
                }
            }
        })
        customPopup.show(view, 0, 0)
    }

    fun deleteSelectedItems() {
        if (selectedItems.isEmpty()) return

        val updatedList = currentList.filter {
            it !is MediaListItem.Media || !selectedItems.contains(it.media)
        }

        selectedItems.clear() // Clear the selection after deletion
        submitList(updatedList) // Update the adapter with the new list
        disableSelectionMode() // Exit selection mode
    }


    class DiffCallback : DiffUtil.ItemCallback<MediaListItem>() {
        override fun areItemsTheSame(oldItem: MediaListItem, newItem: MediaListItem) =
            oldItem == newItem

        override fun areContentsTheSame(oldItem: MediaListItem, newItem: MediaListItem) =
            oldItem == newItem
    }

    override fun onUpdate(position: Int, popupTextView: TextView) {
        val item = currentList.getOrNull(position) ?: return

        // Get the exact header (date) at the given position
        val headerText = getHeaderForPosition(position) // Replace `findNearestHeader` with this

        popupTextView.text = headerText // Set the popup text to the header's date
    }

    // Function to get the exact header for a given position
    private fun getHeaderForPosition(position: Int): String {
        for (i in position downTo 0) {
            val item = currentList.getOrNull(i)
            if (item is MediaListItem.Header) { // Check if it's a header
                return item.date // Return the date from the header
            }
        }
        return "" // Default empty if no header is found
    }
}
