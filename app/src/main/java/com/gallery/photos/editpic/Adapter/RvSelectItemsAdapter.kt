package com.gallery.photos.editpic.Adapter

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gallery.photos.editpic.Extensions.loadImg
import com.gallery.photos.editpic.Extensions.onClick
import com.gallery.photos.editpic.Model.MediaModelItem
import com.gallery.photos.editpic.databinding.RvSelectedItemLayoutBinding

class RvSelectItemsAdapter(
    var activity: Activity,
    var list: ArrayList<MediaModelItem>,
    var onClick: (MediaModelItem) -> Unit
) :
    RecyclerView.Adapter<RvSelectItemsAdapter.VH>() {
    class VH(var bind: RvSelectedItemLayoutBinding) : RecyclerView.ViewHolder(bind.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(RvSelectedItemLayoutBinding.inflate(activity.layoutInflater))

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = list[position]

        holder.bind.apply {
            ivItem.loadImg(activity, item.path)
            ivIsVideo.visibility = if (item.isVideo) View.VISIBLE else View.GONE

            ivRadio.onClick {
                onClick.invoke(item)
            }
        }
    }

    override fun getItemCount() = list.size
}
