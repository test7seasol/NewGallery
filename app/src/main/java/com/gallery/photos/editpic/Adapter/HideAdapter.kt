package com.gallery.photos.editpic.Adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.gallery.photos.editpic.Activity.HideActivity
import com.gallery.photos.editpic.Activity.HideViewPagerActivity
import com.gallery.photos.editpic.Extensions.beVisibleIf
import com.gallery.photos.editpic.Extensions.gone
import com.gallery.photos.editpic.Extensions.isVideoFile
import com.gallery.photos.editpic.Extensions.loadImg
import com.gallery.photos.editpic.Extensions.log
import com.gallery.photos.editpic.Extensions.startActivityWithBundle
import com.gallery.photos.editpic.Extensions.visible
import com.gallery.photos.editpic.Model.HideMediaModel
import com.gallery.photos.editpic.R
import com.gallery.photos.editpic.Utils.HideMediaStoreSingleton
import com.gallery.photos.editpic.databinding.ItemRecyclebinMediaBinding

class HideAdapter(
    var activity: Activity,
    var list: ArrayList<HideMediaModel>,
    private val onLongItemClick: (Boolean) -> Unit
) : RecyclerView.Adapter<HideAdapter.VH>() {
    class VH(var bind: ItemRecyclebinMediaBinding) : RecyclerView.ViewHolder(bind.root)

    val selectedItems = HashSet<HideMediaModel>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(
        ItemRecyclebinMediaBinding.inflate(
            LayoutInflater.from(activity)
        )
    )

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = list[position]

        holder.bind.apply {
            imageViewMedia.loadImg(activity, item.mediaPath)
            imageViewVideoIcon.beVisibleIf(isVideoFile(item.mediaPath))
            selectionOverlay.setImageResource(if (item.isSelect) R.drawable.baseline_check_circle_24 else R.drawable.baseline_radio_button_unchecked_24)

            ivFav.gone()

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
                    val mediaList = list
                    val correctPosition = mediaList.indexOf(item)

                    if (correctPosition != -1) {
                        HideMediaStoreSingleton.hideimageList = list
                        HideMediaStoreSingleton.hideselectedPosition = list.indexOf(item)
                        activity.startActivityWithBundle<HideViewPagerActivity>()
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

    override fun getItemCount() = list.size

    private fun toggleSelection(media: HideMediaModel) {
        if (selectedItems.contains(media)) {
            selectedItems.remove(media)
        } else {
            selectedItems.add(media)
        }

        if (selectedItems.isEmpty()) {
            disableSelectionMode()
        } else {
//            actionbarTxt.text = "${selectedItems.size} Selected"
            activity.findViewById<TextView>(R.id.tvHide).text = "${selectedItems.size} selected"
        }
        notifyDataSetChanged()
    }

    fun selectAllItems() {
        val allItems = list

        if (selectedItems.size == allItems.size) {
            ("11").log()
            selectedItems.clear() // Deselect all if already selected
            disableSelectionMode() // Exit selection mode if everythi
            // ng is deselected
        } else {
            selectedItems.clear()
            selectedItems.addAll(allItems)
            activity.findViewById<TextView>(R.id.tvHide).text = "${selectedItems.size} selected"
            ("22").log("allItems: ${allItems.size}")
            enableSelectionMode() // Ensure selection mode stays active
        }
        updateSelectionUI()
    }

    fun deleteSelectedItems() {
        if (selectedItems.isEmpty()) return

        val updatedList = list.filter {
            !selectedItems.contains(it)
        }

        selectedItems.clear() // Clear the selection after deletion
        list = ArrayList(updatedList) // Update the adapter with the new list
        disableSelectionMode() // Exit selection mode
    }

    fun unselectAllItems() {
        val allItems = list

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
        activity.findViewById<TextView>(R.id.tvHide).text = "${selectedItems.size} selected"
    }


    @SuppressLint("NotifyDataSetChanged")
    private fun enableSelectionMode() {
        (activity as HideActivity).toggleTopBarVisibility(true) // Hide top bar
        notifyDataSetChanged() // Refresh list to show selection icons
    }

    @SuppressLint("NotifyDataSetChanged")
    fun disableSelectionMode() {
        selectedItems.clear()
        (activity as HideActivity).toggleTopBarVisibility(false)
        onLongItemClick.invoke(false)
        notifyDataSetChanged()
    }

    private fun updateSelectionUI() {
//        actionbarTxt.text = "${selectedItems.size} Selected"

        // Notify only media items to refresh their UI (avoiding unnecessary reloads)
        list.forEachIndexed { index, item ->
            notifyItemChanged(index) // Refresh only media items
        }
    }
}