package com.gallery.photos.editpic.Adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gallery.photos.editpic.Activity.DeleteViewPagerActivity
import com.gallery.photos.editpic.Extensions.beVisibleIf
import com.gallery.photos.editpic.Extensions.gone
import com.gallery.photos.editpic.Extensions.loadImg
import com.gallery.photos.editpic.Extensions.onClick
import com.gallery.photos.editpic.Extensions.startActivityWithBundle
import com.gallery.photos.editpic.Extensions.visible
import com.gallery.photos.editpic.Model.DeleteMediaModel
import com.gallery.photos.editpic.R
import com.gallery.photos.editpic.Utils.DeleteMediaStoreSingleton
import com.gallery.photos.editpic.databinding.ItemRecyclebinMediaBinding

class DeleteAdapter(var activity: Activity, var list: ArrayList<DeleteMediaModel>) :
    RecyclerView.Adapter<DeleteAdapter.VH>() {
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

            root.onClick {
                DeleteMediaStoreSingleton.deleteimageList = list
                DeleteMediaStoreSingleton.deleteselectedPosition = list.indexOf(item)

                activity.startActivityWithBundle<DeleteViewPagerActivity>()
            }
            root.setOnLongClickListener {
                selectionOverlay.visible()
                item.isSelect = !item.isSelect
                notifyDataSetChanged()
                true
            }
        }
    }

    override fun getItemCount() = list.size
}