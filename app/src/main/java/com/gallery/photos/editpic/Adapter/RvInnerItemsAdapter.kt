package com.gallery.photos.editpic.Adapter

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gallery.photos.editpic.Extensions.loadImg
import com.gallery.photos.editpic.Extensions.onClick
import com.gallery.photos.editpic.Model.MediaModelItem
import com.gallery.photos.editpic.R
import com.gallery.photos.editpic.databinding.RvFileitemsLayoutBinding

class RvInnerItemsAdapter(
    var activity: Activity,
    var list: ArrayList<MediaModelItem>,
    var onClick: (MediaModelItem) -> Unit
) :
    RecyclerView.Adapter<RvInnerItemsAdapter.VH>() {
    class VH(var bind: RvFileitemsLayoutBinding) : RecyclerView.ViewHolder(bind.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(RvFileitemsLayoutBinding.inflate(activity.layoutInflater))

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = list[position]

        holder.bind.apply {
            ivItem.loadImg(activity, item.path)
            ivRadio.setImageResource(if (item.isSelect) R.drawable.ic_radio_button_checked else R.drawable.ic_radio_button_unchecked)
            ivIsVideo.visibility = if (item.isVideo) View.VISIBLE else View.GONE

            root.onClick {
                onClick.invoke(item)
            }
        }
    }

    override fun getItemCount() = list.size
}
