package com.gallery.photos.editpic.Adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gallery.photos.editpic.Extensions.loadImg
import com.gallery.photos.editpic.Extensions.onClick
import com.gallery.photos.editpic.Model.FolderModelItem
import com.gallery.photos.editpic.databinding.RvItemsLayoutBinding

class RvFolderAdapter(
    var activity: Activity,
    var list: ArrayList<FolderModelItem>,
    var onClick: (FolderModelItem) -> Unit
) : RecyclerView.Adapter<RvFolderAdapter.VH>() {
    class VH(var bind: RvItemsLayoutBinding) : RecyclerView.ViewHolder(bind.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(RvItemsLayoutBinding.inflate(activity.layoutInflater))

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = list[position]

        holder.bind.apply {
            ivItem.loadImg(activity, item.thumbnail)
            tvTital.text = item.bucketName
            tvSize.text = ("${item.totalSize / (1024 * 1024)} MB")
            tvFilesize.text = (item.fileCount).toString() + " items"
            root.onClick {
                onClick.invoke(item)
            }
        }
    }

    override fun getItemCount() = list.size
}
