package com.gallery.photos.editpic.Dialogs

import android.annotation.SuppressLint
import android.app.Activity
import androidx.appcompat.app.AlertDialog
import com.gallery.photos.editpic.Extensions.log
import com.gallery.photos.editpic.Extensions.onClick
import com.gallery.photos.editpic.Extensions.toGson
import com.gallery.photos.editpic.Model.MediaModel
import com.gallery.photos.editpic.databinding.DialogPropertiesBinding


class PropertiesDialog(
    private val activity: Activity,
    private val model: MediaModel,
    val callback: (skipRecycleBin: String) -> Unit
) {

    private var dialog: AlertDialog? = null
    private val binding = DialogPropertiesBinding.inflate(activity.layoutInflater)

    init {
        setupDialog()
    }

    @SuppressLint("SetTextI18n")
    private fun setupDialog() {
        // Set the custom message
        // Build and show the dialog
        dialog = AlertDialog.Builder(activity)
            .setView(binding.root) // Set the custom layout
            .create()

        model.toGson().log()

        binding.apply {
            tvName.text = model.mediaName
            tvPath.text = model.mediaPath
            tvSize.text = formatFileSize(model.mediaSize)
            tvresolution.text = "1.6 mp 1280 x 1280"
            tvLastModify.text = model.displayDate

            tvCancel.onClick {
                dialog?.dismiss()
                callback("")
            }
        }


        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog?.show()
    }


    fun formatFileSize(bytes: Long): String {
        val kb = bytes / 1024.0
        val mb = kb / 1024.0
        return when {
            mb >= 1 -> String.format("%.2f MB", mb)
            kb >= 1 -> String.format("%.2f KB", kb)
            else -> String.format("%d Bytes", bytes)
        }
    }

}