package com.gallery.photos.editpic.Dialogs

import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.RelativeLayout
import com.gallery.photos.editpic.Extensions.gone
import com.gallery.photos.editpic.Extensions.tos
import com.gallery.photos.editpic.Extensions.visible
import com.gallery.photos.editpic.R
import com.gallery.photos.editpic.databinding.DialogCreateNewFolderBinding
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class CreateNewFolderDialog(
    private val activity: Activity,
    private val initialPath: String = "",
    private val selectedPaths: List<String> = arrayListOf(),
    private val isFromWhere: String = "Move",
    private val callback: (newFolderPath: String) -> Unit
) {
    private var dialog: Dialog

    init {
        val view = DialogCreateNewFolderBinding.inflate(activity.layoutInflater, null, false)
        dialog = Dialog(activity).apply {
            setContentView(view.root)
            setCancelable(true)
            setTitle("Create New Album")
            window?.setBackgroundDrawableResource(android.R.color.transparent)
            window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }

        // **Check if views exist before modifying them**
        activity.findViewById<RelativeLayout>(R.id.mainTopTabsContainer)?.let { it.gone() }
        activity.findViewById<RelativeLayout>(R.id.footer)?.let { it.gone() }

        view.root.post {
            view.edAlbum.requestFocus()
            showKeyboard(activity, view.edAlbum)
        }

        view.tvCancel.setOnClickListener {
            activity.findViewById<RelativeLayout>(R.id.mainTopTabsContainer)?.let { it.visible() }
            activity.findViewById<RelativeLayout>(R.id.footer)?.let { it.visible() }
            dialog.dismiss()
        }

        dialog.setOnDismissListener {
            activity.findViewById<RelativeLayout>(R.id.mainTopTabsContainer)?.let { it.visible() }
            activity.findViewById<RelativeLayout>(R.id.footer)?.let { it.visible() }
        }

        view.tvCreate.setOnClickListener {
            val folderName = view.edAlbum.text.toString().trim()
            if (isFromWhere == "CreateFolder") {
                if (folderName.isEmpty()) {
                    ("Album name is not empty").tos(activity)
                } else {
                    val newFolder = File("$initialPath/$folderName")
                    if (newFolder.exists()) {
                        ("Folder already exists").tos(activity)
                    } else {
                        if (newFolder.mkdirs()) {
                            callback.invoke(folderName)
                            dialog.dismiss()
                        }
                    }
                }
            } else {
                if (folderName.isEmpty()) {
                    (activity.getString(R.string.please_enter_a_valid_album_name)).tos(activity)
                } else {
                    val newFolderPath = "$initialPath/$folderName"
                    val newFolder = File(newFolderPath)

                    if (newFolder.exists()) {
                        (activity.getString(R.string.folder_already_exists)).tos(activity)
                    } else {
                        if (newFolder.mkdirs()) {
                            if (isFromWhere == "Move") {
                                moveFilesToNewFolder(selectedPaths, newFolderPath)
                            } else {
                                copyFilesToNewFolder(selectedPaths, newFolderPath)
                            }
                            callback(newFolderPath)
                            dialog.dismiss()
                        } else {
                            (activity.getString(R.string.could_not_create_folder)).tos(activity)
                        }
                    }
                }
            }
        }
        dialog.show()
    }

    private fun showKeyboard(activity: Activity, view: View) {
        val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        view.postDelayed({ imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT) }, 100)
    }

    private fun moveFilesToNewFolder(filePaths: List<String>, destinationFolder: String) {
        showProgressDialog("Moving files...", filePaths.size) { progressDialog ->
            var progress = 0
            var allSuccess = true

            filePaths.forEach { filePath ->
                val sourceFile = File(filePath)
                val destinationFile = File(destinationFolder, sourceFile.name)

                try {
                    if (sourceFile.exists()) {
                        if (destinationFile.exists()) destinationFile.delete()
                        if (sourceFile.renameTo(destinationFile)) {
                            Log.d("FileMove", "File moved successfully to ${destinationFile.path}")
                        } else {
                            Log.e("FileMove", "Failed to move file: ${sourceFile.path}")
                            allSuccess = false
                        }
                    } else {
                        Log.e("FileMove", "Source file does not exist: ${sourceFile.path}")
                        allSuccess = false
                    }
                } catch (e: Exception) {
                    Log.e("FileMove", "Error while moving file: ${e.message}")
                    allSuccess = false
                }

                progress++
                activity.runOnUiThread { progressDialog.progress = progress }
            }

            activity.runOnUiThread {
                progressDialog.dismiss()
                if (allSuccess) (activity.getString(R.string.files_moved_successfully)).tos(activity) else (activity.getString(
                    R.string.some_files_failed_to_move
                )).tos(
                    activity
                )
            }
        }
    }

    private fun copyFilesToNewFolder(filePaths: List<String>, destinationFolder: String) {
        showProgressDialog(activity.getString(R.string.copying_files), filePaths.size) { progressDialog ->
            var progress = 0
            var allSuccess = true

            filePaths.forEach { filePath ->
                val sourceFile = File(filePath)
                val destinationFile = File(destinationFolder, sourceFile.name)

                try {
                    if (sourceFile.exists()) {
                        FileInputStream(sourceFile).use { input ->
                            FileOutputStream(destinationFile).use { output ->
                                input.copyTo(output)
                            }
                        }
                        Log.d("FileCopy", "File copied successfully to ${destinationFile.path}")
                    } else {
                        Log.e("FileCopy", "Source file does not exist: ${sourceFile.path}")
                        allSuccess = false
                    }
                } catch (e: Exception) {
                    Log.e("FileCopy", "Error while copying file: ${e.message}")
                    allSuccess = false
                }

                progress++
                activity.runOnUiThread { progressDialog.progress = progress }
            }

            activity.runOnUiThread {
                progressDialog.dismiss()
                if (allSuccess) (activity.getString(R.string.files_copied_successfully)).tos(activity) else (activity.getString(
                    R.string.some_files_failed_to_copy
                )).tos(
                    activity
                )
            }
        }
    }

    private fun showProgressDialog(
        message: String,
        maxProgress: Int,
        task: (progressDialog: ProgressDialog) -> Unit
    ) {
        val progressDialog = ProgressDialog(activity).apply {
            setMessage(message)
            setCancelable(false)
            setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
            max = maxProgress
            show()
        }

        Thread { task(progressDialog) }.start()
    }
}