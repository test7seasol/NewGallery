package com.gallery.photos.editpic.Dialogs

import android.app.Activity
import androidx.appcompat.app.AlertDialog
import com.gallery.photos.editpic.Extensions.onClick
import com.gallery.photos.editpic.databinding.DialogDeleteWithCustomRememberBinding

class DeleteWithRememberDialog(
    private val activity: Activity,
    private val isFromBin: Boolean = false,
    val callback: (skipRecycleBin: Boolean) -> Unit
) {

    private var dialog: AlertDialog? = null
    private val binding = DialogDeleteWithCustomRememberBinding.inflate(activity.layoutInflater)

    init {
        setupDialog()
    }

    private fun setupDialog() {
        // Set the custom message

        // Show or hide the "Skip Recycle Bin" checkbox
//        binding.skipTheRecycleBinCheckbox.isVisible = showSkipRecycleBinOption

        binding.tvName.text = if (isFromBin) "Permanently Delete?" else "Move to Recycle bin?"
        // Handle Cancel button click
        binding.tvCancel.onClick {
            dialog?.dismiss()
        }

        // Handle Delete button click
        binding.tvDelete.onClick {
            dialogConfirmed()
        }

        // Build and show the dialog
        dialog = AlertDialog.Builder(activity)
            .setView(binding.root) // Set the custom layout
            .create()

        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog?.show()
    }

    private fun dialogConfirmed() {
//        val remember = binding.rememberCheckbox.isChecked
//        val skipRecycleBin = binding.skipTheRecycleBinCheckbox.isChecked
        dialog?.dismiss()
        callback( false)
    }
}
