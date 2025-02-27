package com.gallery.photos.editpic.Dialogs

import android.annotation.SuppressLint
import android.app.Activity
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import com.gallery.photos.editpic.Extensions.onClick
import com.gallery.photos.editpic.databinding.BottomSheetMoveCopyBinding
import com.google.android.material.bottomsheet.BottomSheetDialog

@SuppressLint("SetTextI18n")
class SMCopyMoveBottomSheetDialog(
    var activity: Activity,
    var size: String,
    var folderName: String,
    callback: (String) -> Unit
) {
    var dialog: BottomSheetDialog = BottomSheetDialog(activity)
    var binding: BottomSheetMoveCopyBinding =
        BottomSheetMoveCopyBinding.inflate(LayoutInflater.from(activity))

    init {
        dialog.setContentView(binding.root)
        dialog.setCancelable(false)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.window!!.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            .setBackgroundResource(android.R.color.transparent)

        binding.apply {

            tvTitle.text = Html.fromHtml("Copy or move $size files to <b>$folderName</b>?", Html.FROM_HTML_MODE_LEGACY)

            tvCancel.onClick {
                dialog.dismiss()
            }
            tvCopy.onClick {
                callback("tvCopy")
//                dialog.dismiss()
            }
            tvMove.onClick {
                callback("tvMove")
//                dialog.dismiss()
            }
            dialog.setOnDismissListener {
//                callback("setOnDismiss")
                dialog.dismiss()
            }
        }
        dialog.show()
    }

    fun onDismissDialog() {
        dialog.dismiss()
    }

    fun onShowDialog() {
        dialog.show()
    }
}
