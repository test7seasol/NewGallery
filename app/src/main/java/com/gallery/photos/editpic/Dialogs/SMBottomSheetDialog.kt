package com.gallery.photos.editpic.Dialogs

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import com.gallery.photos.editpic.Activity.FavoriteAct
import com.gallery.photos.editpic.Activity.RecycleBinAct
import com.gallery.photos.editpic.Activity.SettingsActivity
import com.gallery.photos.editpic.Activity.VideoActivity
import com.gallery.photos.editpic.Extensions.onClick
import com.gallery.photos.editpic.Extensions.startActivityWithBundle
import com.gallery.photos.editpic.databinding.BottomSheetLayoutBinding
import com.google.android.material.bottomsheet.BottomSheetDialog

@SuppressLint("ResourceType")
class SMBottomSheetDialog(var activity: Activity, callback: (String) -> Unit) {
    var dialog: BottomSheetDialog = BottomSheetDialog(activity)
    var binding: BottomSheetLayoutBinding =
        BottomSheetLayoutBinding.inflate(LayoutInflater.from(activity))

    init {
        dialog.setContentView(binding.root)
        dialog.setCancelable(true)
//        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.window!!.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            .setBackgroundResource(android.R.color.transparent)
        binding.root.setBackgroundResource(Color.TRANSPARENT)

        binding.apply {
            settingid.onClick {
                activity.startActivityWithBundle<SettingsActivity>()
                dialog.dismiss()
            }
            recyclerbinid.onClick {
                activity.startActivityForResult(Intent(activity, RecycleBinAct::class.java), 120)
                dialog.dismiss()
            }
            favoriteId.onClick {
                activity.startActivityWithBundle<FavoriteAct>()
                callback("Favourite")
                dialog.dismiss()
            }
            recentid.onClick {
                callback("Recent")
                dialog.dismiss()
            }
            videoid.onClick {

                activity.startActivityWithBundle<VideoActivity>()

                dialog.dismiss()
            }
            hideshowid.onClick {
                callback("hideshowid")
                dialog.dismiss()
            }

            root.onClick {
                dialog.dismiss()
            }
            dialog.setOnDismissListener {
                callback("setOnDismiss")
                dialog.dismiss()
            }
        }
        dialog.show()
    }
}
