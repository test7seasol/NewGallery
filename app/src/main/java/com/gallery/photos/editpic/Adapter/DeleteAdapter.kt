package com.gallery.photos.editpic.Adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.gallery.photos.editpic.Activity.DeleteViewPagerActivity
import com.gallery.photos.editpic.Activity.RecycleBinAct
import com.gallery.photos.editpic.Extensions.beVisibleIf
import com.gallery.photos.editpic.Extensions.gone
import com.gallery.photos.editpic.Extensions.loadImg
import com.gallery.photos.editpic.Extensions.log
import com.gallery.photos.editpic.Extensions.startActivityWithBundle
import com.gallery.photos.editpic.Extensions.visible
import com.gallery.photos.editpic.Model.DeleteMediaModel
import com.gallery.photos.editpic.R
import com.gallery.photos.editpic.Utils.DeleteMediaStoreSingleton
import com.gallery.photos.editpic.databinding.ItemRecyclebinMediaBinding

class DeleteAdapter(
    var activity: Activity, var list: ArrayList<DeleteMediaModel>,
    private val onLongItemClick: (Boolean) -> Unit
) :
    RecyclerView.Adapter<DeleteAdapter.VH>() {

    val selectedItems = HashSet<DeleteMediaModel>()

    class VH(var bind: ItemRecyclebinMediaBinding) : RecyclerView.ViewHolder(bind.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(
        ItemRecyclebinMediaBinding.inflate(
            LayoutInflater.from(activity)
        )
    )

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = list[position]

        holder.bind.apply {
            imageViewMedia.loadImg(activity, item.binPath)
            imageViewVideoIcon.beVisibleIf(item.isVideo)
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
                        DeleteMediaStoreSingleton.deleteimageList = list
                        DeleteMediaStoreSingleton.deleteselectedPosition = list.indexOf(item)
                        activity.startActivityWithBundle<DeleteViewPagerActivity>()
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
    private fun toggleSelection(media: DeleteMediaModel) {
        if (selectedItems.contains(media)) {
            selectedItems.remove(media)
        } else {
            selectedItems.add(media)
        }

        if (selectedItems.isEmpty()) {
            disableSelectionMode()
        } else {
//            actionbarTxt.text = "${selectedItems.size} Selected"
            activity.findViewById<TextView>(R.id.tvRecycleTital).text =
                "${selectedItems.size} selected"
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
            activity.findViewById<TextView>(R.id.tvRecycleTital)
                .setText(activity.getString(R.string.recyclebin))
            selectedItems.clear()
            selectedItems.addAll(allItems)
            activity.findViewById<TextView>(R.id.tvRecycleTital).text =
                "${selectedItems.size} selected"
            ("22").log("allItems: ${allItems.size}")
            enableSelectionMode() // Ensure selection mode stays active
        }
        updateSelectionUI()
    }

    fun deleteSelectedItems() {
        if (selectedItems.isEmpty()) return

        val updatedList = ArrayList(list) // Create a new list instance
        updatedList.removeAll { selectedItems.contains(it) }
        list = updatedList // Ensure a new list is passed

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
        activity.findViewById<TextView>(R.id.tvRecycleTital).text =
            "${selectedItems.size} selected"
    }


    @SuppressLint("NotifyDataSetChanged")
    private fun enableSelectionMode() {
        (activity as RecycleBinAct).toggleTopBarVisibility(true) // Hide top bar
        notifyDataSetChanged() // Refresh list to show selection icons
    }


    @SuppressLint("NotifyDataSetChanged")
    fun disableSelectionMode() {
        selectedItems.clear()
        (activity as RecycleBinAct).toggleTopBarVisibility(false)
        notifyDataSetChanged()
        onLongItemClick.invoke(false)
    }

    private fun updateSelectionUI() {
//        actionbarTxt.text = "${selectedItems.size} Selected"

        // Notify only media items to refresh their UI (avoiding unnecessary reloads)
        list.forEachIndexed { index, item ->
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
    override fun getItemCount() = list.size
}