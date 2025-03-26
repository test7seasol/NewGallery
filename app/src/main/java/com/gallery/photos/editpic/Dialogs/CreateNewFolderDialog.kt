package com.gallery.photos.editpic.Dialogs

import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.media.MediaScannerConnection
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.RelativeLayout
import androidx.annotation.RequiresApi
import com.gallery.photos.editpic.Extensions.delayInMillis
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
            delayInMillis(500) {
                showKeyboard(activity, binding.edAlbum)
            }
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
                            Log.e("FileMove", "Failed to delete existing file")
                        }

                        val moved = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            moveFileWithScopedStorage(sourceFile, destinationFile)
                        } else {
                            // For Android 9 and below
                            sourceFile.renameTo(destinationFile) || run {
                                if (copyFileLegacy(sourceFile, destinationFile)) {
                                    sourceFile.delete()
                                } else false
                            }
                        }

                        if (moved) {
                            successCount++
                            updateMediaStore(sourceFile, destinationFile, true)
                        }
                    }
                } catch (e: Exception) {
                    Log.e("FileMove", "Error moving ${sourceFile.path}", e)
                }

                updateProgress(progressDialog, index + 1, filePaths.size)
            }

            showFinalResult(successCount, filePaths.size, "moved")
        }
    }

    private fun copyFilesToNewFolder(filePaths: List<String>, destinationFolder: String) {
        showProgressDialog("Copying files...", filePaths.size) { progressDialog ->
            var successCount = 0

            filePaths.forEachIndexed { index, filePath ->
                val sourceFile = File(filePath)
                val destinationFile = File(destinationFolder, sourceFile.name)

                try {
                    if (sourceFile.exists()) {
                        val copied = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            copyFileWithScopedStorage(sourceFile, destinationFile)
                        } else {
                            copyFileLegacy(sourceFile, destinationFile)
                        }

                        if (copied) {
                            successCount++
                            updateMediaStore(sourceFile, destinationFile, false)
                        }
                    }
                } catch (e: Exception) {
                    Log.e("FileCopy", "Error copying ${sourceFile.path}", e)
                }

                updateProgress(progressDialog, index + 1, filePaths.size)
            }

            showFinalResult(successCount, filePaths.size, "copied")
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun copyFileWithScopedStorage(sourceFile: File, destinationFile: File): Boolean {
        return try {
            val mimeType = getMimeType(sourceFile.path)
            val collection = when {
                mimeType?.startsWith("video/") == true ->
                    MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)

                else ->
                    MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
            }

            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, sourceFile.name)
                put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
                put(
                    MediaStore.MediaColumns.RELATIVE_PATH,
                    "${Environment.DIRECTORY_PICTURES}/${File(destinationFile.parent).name}"
                )
                put(MediaStore.MediaColumns.IS_PENDING, 1)
            }

            val uri = activity.contentResolver.insert(collection, contentValues) ?: return false

            activity.contentResolver.openOutputStream(uri)?.use { outputStream ->
                sourceFile.inputStream().use { inputStream ->
                    inputStream.copyTo(outputStream)
                }
            }

            contentValues.clear()
            contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
            activity.contentResolver.update(uri, contentValues, null, null)
            true
        } catch (e: Exception) {
            Log.e("FileCopy", "Scoped copy failed", e)
            false
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun moveFileWithScopedStorage(sourceFile: File, destinationFile: File): Boolean {
        return try {
            if (copyFileWithScopedStorage(sourceFile, destinationFile)) {
                // Delete original file using MediaStore
                val collection = when {
                    getMimeType(sourceFile.path)?.startsWith("video/") == true ->
                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI

                    else ->
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                }

                val mediaId = getMediaId(sourceFile)
                if (mediaId != -1L) {
                    activity.contentResolver.delete(
                        ContentUris.withAppendedId(collection, mediaId),
                        null,
                        null
                    ) > 0
                } else {
                    false
                }
            } else {
                false
            }
        } catch (e: Exception) {
            Log.e("FileMove", "Scoped move failed", e)
            false
        }
    }

    private fun copyFileLegacy(sourceFile: File, destinationFile: File): Boolean {
        return try {
            FileInputStream(sourceFile).use { input ->
                FileOutputStream(destinationFile).use { output ->
                    input.copyTo(output)
                }
            }
            true
        } catch (e: Exception) {
            Log.e("FileCopy", "Legacy copy failed", e)
            false
        }
    }

    private fun getMediaId(file: File): Long {
        val projection = arrayOf(MediaStore.MediaColumns._ID)
        val selection = "${MediaStore.MediaColumns.DATA} = ?"
        val selectionArgs = arrayOf(file.absolutePath)
        val uri = if (file.path.endsWith(".mp4")) {
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        } else {
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }

        return activity.contentResolver.query(uri, projection, selection, selectionArgs, null)
            ?.use {
                if (it.moveToFirst()) it.getLong(0) else -1L
            } ?: -1L
    }

    private fun updateMediaStore(sourceFile: File, destinationFile: File, isMove: Boolean) {
        MediaScannerConnection.scanFile(
            activity,
            arrayOf(destinationFile.absolutePath),
            null,
            null
        )
        if (isMove) {
            MediaScannerConnection.scanFile(
                activity,
                arrayOf(sourceFile.absolutePath),
                null,
                null
            )
        }
    }

    private fun getMimeType(path: String): String? {
        return when {
            path.endsWith(".jpg", ignoreCase = true) -> "image/jpeg"
            path.endsWith(".jpeg", ignoreCase = true) -> "image/jpeg"
            path.endsWith(".png", ignoreCase = true) -> "image/png"
            path.endsWith(".gif", ignoreCase = true) -> "image/gif"
            path.endsWith(".mp4", ignoreCase = true) -> "video/mp4"
            path.endsWith(".mkv", ignoreCase = true) -> "video/x-matroska"
            path.endsWith(".webp", ignoreCase = true) -> "image/webp"
            else -> null
        }
    }

    private fun showFinalResult(successCount: Int, totalFiles: Int, operation: String) {
        activity.runOnUiThread {
            val message = when {
                successCount == totalFiles ->
                    "Successfully $operation all $successCount files"

                successCount > 0 ->
                    "Successfully $operation $successCount of $totalFiles files"

                else ->
                    "Failed to $operation files"
            }
            message.tos(activity)
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