package com.gallery.photos.editpic.Dialogs

import android.app.Activity
import androidx.appcompat.app.AlertDialog
import com.gallery.photos.editpic.Extensions.onClick
import com.gallery.photos.editpic.databinding.SlideshowDialogBinding

class SlideShowDialog(
    private val activity: Activity, val callback: (String) -> Unit
) {

    private var dialog: AlertDialog? = null
    private val binding = SlideshowDialogBinding.inflate(activity.layoutInflater)

    init {
        setupDialog()
    }

    private fun setupDialog() {
        // Set the custom message
        // Show or hide the "Skip Recycle Bin" checkbox
//        binding.skipTheRecycleBinCheckbox.isVisible = showSkipRecycleBinOption

        binding.lloneSec.onClick {
            callback("lloneSec")
            dialog?.dismiss()
        }
        binding.lltwoSec.onClick {
            callback("lltwoSec")
            dialog?.dismiss()
        }
        binding.llthreeSec.onClick {
            callback("llthreeSec")
            dialog?.dismiss()
        }
        binding.llfourSec.onClick {
            callback("llfourSec")
            dialog?.dismiss()
        }
        binding.llfiveec.onClick {
            callback("llfiveSec")
            dialog?.dismiss()
        }

        // Build and show the dialog
        dialog = AlertDialog.Builder(activity).setView(binding.root) // Set the custom layout
            .create()

        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog?.show()
    }
}
