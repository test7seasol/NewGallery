package com.gallery.photos.editpic.Adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gallery.photos.editpic.Activity.LanguageModel
import com.gallery.photos.editpic.Extensions.log
import com.gallery.photos.editpic.Extensions.onClick
import com.gallery.photos.editpic.R
import com.gallery.photos.editpic.databinding.ItemChangeLanguageBinding

class RVLanguageAdapter(
    var activity: Activity, var list: ArrayList<LanguageModel>, var onNext: (LanguageModel) -> Unit
) : RecyclerView.Adapter<RVLanguageAdapter.VH>() {
    class VH(var bind: ItemChangeLanguageBinding) : RecyclerView.ViewHolder(bind.root)

    @SuppressLint("NotifyDataSetChanged", "UseCompatLoadingForDrawables")
    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = list[position]

        holder.bind.apply {
            iviconid.setImageResource(item.src)
            tvnameid.text = (item.language)
            tvsubnameid.text = (item.sub_language)
            llid.setBackgroundResource(if (item.isSelected) R.drawable.selectedframe else 0)
            root.onClick {
                list.forEach {
                    it.isSelected = false
                }

                item.isSelected = true
                ("Click Item").log()
                onNext.invoke(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(ItemChangeLanguageBinding.inflate(activity.layoutInflater))

    override fun getItemCount() = list.size
}
