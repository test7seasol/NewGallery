package com.gallery.photos.editphotovideo.callendservice.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.gallery.photos.editpic.Activity.MainActivity
import com.gallery.photos.editpic.Extensions.beGone
import com.gallery.photos.editpic.Extensions.beVisible
import com.gallery.photos.editpic.Model.MediaListItem
import com.gallery.photos.editpic.Model.MediaModel
import com.gallery.photos.editpic.R
import com.gallery.photos.editpic.RoomDB.Dao.DeleteMediaDao
import com.gallery.photos.editpic.Views.FastScroller
import com.gallery.photos.editpic.databinding.ItemMediaCallBinding
import com.gallery.photos.editpic.databinding.ItemMediaDateHeaderBinding
import kotlin.math.ln
import kotlin.math.pow

class RecentPictureAdaptercall(
    var activity: Activity,
    private val onItemClick: (List<MediaModel>, Int) -> Unit,
) : ListAdapter<MediaListItem, RecyclerView.ViewHolder>(DiffCallback()),
    FastScroller.SectionIndexer {


    var deleteMediaDao: DeleteMediaDao? = null

    companion object {
        private const val VIEW_TYPE_HEADER = 0
        private const val VIEW_TYPE_MEDIA = 1
    }

    val selectedItems = HashSet<MediaModel>()
//    private lateinit var actionbarTxt: TextView
//    private lateinit var shareimgmenu: ImageView
//    private lateinit var deleteimgmenu: ImageView
//    private lateinit var moreimgmenu: ImageView
//    private lateinit var closeSelection: ImageView
//    private var selectionToolbar: Toolbar? = null


    /* init {
         deleteMediaDao = getMediaDatabase(activity).mediaDao()

         // Find selection toolbar in the activity
         selectionToolbar = activity.findViewById(R.id.selectionToolbar)
         actionbarTxt = activity.findViewById(R.id.actionbar_title)
         shareimgmenu = activity.findViewById(R.id.shareimgmenu)
         deleteimgmenu = activity.findViewById(R.id.deleteimgmenu)
         moreimgmenu = activity.findViewById(R.id.moreimgmenu)
         closeSelection = activity.findViewById(R.id.closeSelection)

         // Handle selection toolbar clicks
         closeSelection.setOnClickListener { disableSelectionMode() }
         shareimgmenu.setOnClickListener {
             shareSelectedItems()
         }
         deleteimgmenu.setOnClickListener { deleteSelectedItems() }
         moreimgmenu.setOnClickListener { openMenuDialog(moreimgmenu) }
     }*/

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


            Glide.with(binding.imageViewMedia.context)
                .load(media.mediaPath)
                .placeholder(R.color.ripple_color)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
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


    private fun Long.withSuffix(): String {
        if (this < 1000) return "" + this
        val exp = (ln(this.toDouble()) / ln(1000.0)).toInt()
        return String.format(
            "%.1f%c",
            this / 1000.0.pow(exp.toDouble()),
            "kMBTPE"[exp - 1]
        )
    }

    private fun getColor(power: Int): Int {
        val h = (itemCount - power) * 100 / itemCount
        val s = 1 // Saturation
        val v = 0.8 // Value
        return Color.HSVToColor(floatArrayOf(h.toFloat(), s.toFloat(), v.toFloat()))
    }


    override fun getSectionText(position: Int) = findNearestHeader(position)


}
