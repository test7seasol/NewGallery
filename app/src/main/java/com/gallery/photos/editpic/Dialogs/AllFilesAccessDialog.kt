package com.gallery.photos.editpic.Dialogs

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION
import android.util.Log
import com.gallery.photos.editpic.Extensions.log
import com.gallery.photos.editpic.databinding.DialogAllFilesPermissionBinding

class AllFilesAccessDialog(private val activity: Activity, val callback: () -> Unit) {

    private var dialog: AlertDialog? = null
    private val binding = DialogAllFilesPermissionBinding.inflate(activity.layoutInflater)

    init {
        setupDialog()
    }

    private fun setupDialog() {
        binding.btnCancel.setOnClickListener {
            dialog?.dismiss()
        }

        binding.btnAllow.setOnClickListener {
            ("Click Allopw").log()
            requestAllFilesAccessPermission()
            dialog?.dismiss()
        }

        dialog = AlertDialog.Builder(activity)
            .setView(binding.root)
            .setCancelable(false)
            .create()

        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog?.show()
    }

    private fun requestAllFilesAccessPermission() {
        Log.e("TAG", "dwsdwsdfwfw Error: Come here")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                val intent = Intent(ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                intent.data = Uri.parse("package:${activity.packageName}")
                activity.startActivity(intent) // ✅ Start the intent properly

            } catch (e: Exception) {
                Log.e("TAG", "requestAllFilesPermission Error: ${e.message}")
                e.printStackTrace()
            }
        }
    }
}
