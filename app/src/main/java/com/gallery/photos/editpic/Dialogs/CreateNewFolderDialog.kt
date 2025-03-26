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
    private var binding: DialogCreateNewFolderBinding = DialogCreateNewFolderBinding.inflate(activity.layoutInflater)

    init {
        dialog = Dialog(activity).apply {
            setContentView(binding.root)
            setCancelable(true)
            window?.setBackgroundDrawableResource(android.R.color.transparent)
            window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }

        // Safe view hiding with null checks
        activity.findViewById<RelativeLayout>(R.id.mainTopTabsContainer)?.gone()
        activity.findViewById<RelativeLayout>(R.id.footer)?.gone()

        binding.root.post {
            binding.edAlbum.requestFocus()
            showKeyboard(activity, binding.edAlbum)
        }

        binding.tvCancel.setOnClickListener {
            restoreActivityViews()
            dialog.dismiss()
        }

        dialog.setOnDismissListener {
            restoreActivityViews()
        }

        binding.tvCreate.setOnClickListener {
            handleCreateFolder()
        }

        dialog.show()
    }

    private fun restoreActivityViews() {
        activity.findViewById<RelativeLayout>(R.id.mainTopTabsContainer)?.visible()
        activity.findViewById<RelativeLayout>(R.id.footer)?.visible()
    }

    private fun handleCreateFolder() {
        val folderName = binding.edAlbum.text.toString().trim()

        when {
            folderName.isEmpty() -> {
                showErrorMessage(activity.getString(R.string.please_enter_a_valid_album_name))
                return
            }

            isFromWhere == "CreateFolder" -> handleSimpleFolderCreation(folderName)
            else -> handleFileOperation(folderName)
        }
    }

    private fun handleSimpleFolderCreation(folderName: String) {
        val newFolder = File("$initialPath/$folderName")
        when {
            newFolder.exists() -> showErrorMessage("Folder already exists")
            newFolder.mkdirs() -> {
                callback.invoke(folderName)
                dialog.dismiss()
            }

            else -> showErrorMessage("Failed to create folder")
        }
    }

    private fun handleFileOperation(folderName: String) {
        val newFolderPath = "$initialPath/$folderName"
        val newFolder = File(newFolderPath)

        when {
            newFolder.exists() -> showErrorMessage(activity.getString(R.string.folder_already_exists))
            newFolder.mkdirs()
                .not() -> showErrorMessage(activity.getString(R.string.could_not_create_folder))

            else -> performFileOperation(newFolderPath)
        }
    }

    private fun performFileOperation(newFolderPath: String) {
        when (isFromWhere) {
            "Move" -> moveFilesToNewFolder(selectedPaths, newFolderPath)
            else -> copyFilesToNewFolder(selectedPaths, newFolderPath)
        }.also {
            callback(newFolderPath)
            dialog.dismiss()
        }
    }

    private fun showErrorMessage(message: String) {
        message.tos(activity)
    }

    private fun showKeyboard(activity: Activity, view: View) {
        try {
            val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
        } catch (e: Exception) {
            Log.e("CreateNewFolderDialog", "Error showing keyboard", e)
        }
    }

    private fun moveFilesToNewFolder(filePaths: List<String>, destinationFolder: String) {
        showProgressDialog("Moving files...", filePaths.size) { progressDialog ->
            var successCount = 0

            filePaths.forEachIndexed { index, filePath ->
                val sourceFile = File(filePath)
                val destinationFile = File(destinationFolder, sourceFile.name)

                try {
                    if (sourceFile.exists()) {
                        if (destinationFile.exists() && !destinationFile.delete()) {
                            Log.e(
                                "FileMove",
                                "Failed to delete existing file: ${destinationFile.path}"
                            )
                        }

                        if (sourceFile.renameTo(destinationFile)) {
                            successCount++
                            Log.d("FileMove", "Moved: ${sourceFile.path}")
                        }
                    }
                } catch (e: Exception) {
                    Log.e("FileMove", "Error moving ${sourceFile.path}", e)
                }

                updateProgress(progressDialog, index + 1, filePaths.size)
            }

            showFinalResult(
                successCount == filePaths.size,
                activity.getString(R.string.files_moved_successfully),
                activity.getString(R.string.some_files_failed_to_move)
            )
        }
    }

    private fun copyFilesToNewFolder(filePaths: List<String>, destinationFolder: String) {
        showProgressDialog(activity.getString(R.string.copying_files), filePaths.size) { progressDialog ->
            var successCount = 0

            filePaths.forEachIndexed { index, filePath ->
                val sourceFile = File(filePath)
                val destinationFile = File(destinationFolder, sourceFile.name)

                try {
                    if (sourceFile.exists()) {
                        FileInputStream(sourceFile).use { input ->
                            FileOutputStream(destinationFile).use { output ->
                                input.copyTo(output)
                                successCount++
                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e("FileCopy", "Error copying ${sourceFile.path}", e)
                }

                updateProgress(progressDialog, index + 1, filePaths.size)
            }

            showFinalResult(
                successCount == filePaths.size,
                activity.getString(R.string.files_copied_successfully),
                activity.getString(R.string.some_files_failed_to_copy)
            )
        }
    }

    private fun updateProgress(dialog: ProgressDialog, progress: Int, max: Int) {
        activity.runOnUiThread {
            dialog.progress = progress
            if (progress >= max) dialog.dismiss()
        }
    }

    private fun showFinalResult(success: Boolean, successMessage: String, errorMessage: String) {
        activity.runOnUiThread {
            if (success) successMessage.tos(activity) else errorMessage.tos(activity)
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
            this.max = maxProgress
            show()
        }

        Thread {
            try {
                task(progressDialog)
            } catch (e: Exception) {
                Log.e("CreateNewFolderDialog", "Error in background task", e)
                activity.runOnUiThread { progressDialog.dismiss() }
            }
        }.start()
    }
}