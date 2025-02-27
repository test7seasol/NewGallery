package com.gallery.photos.editpic.Dialogs

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION
import android.util.Log
import com.gallery.photos.editpic.Extensions.log
import com.gallery.photos.editpic.databinding.ExitDialogBinding

class ExitDialog(private val activity: Activity, val callback: () -> Unit) {

    private var dialog: AlertDialog? = null
    private val binding = ExitDialogBinding.inflate(activity.layoutInflater)

    init {
        setupDialog()
    }

    private fun setupDialog() {
        binding.btnCancel.setOnClickListener {
            dialog?.dismiss()
        }

        binding.btnCancel.setOnClickListener {
            ("Click Allopw").log()
            dialog?.dismiss()
        }
        binding.btnExit.setOnClickListener {
            ("Click Allopw").log()
            activity.finishAffinity()
            dialog?.dismiss()
        }

        dialog = AlertDialog.Builder(activity)
            .setView(binding.root)
            .setCancelable(false)
            .create()

        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog?.show()
    }


}
