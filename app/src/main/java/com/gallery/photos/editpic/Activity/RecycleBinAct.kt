package com.gallery.photos.editpic.Activity

import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.webkit.MimeTypeMap
import androidx.appcompat.app.AppCompatActivity
import com.gallery.photos.editpic.Adapter.DeleteAdapter
import com.gallery.photos.editpic.Dialogs.AllFilesAccessDialog
import com.gallery.photos.editpic.Dialogs.DeleteWithRememberDialog
import com.gallery.photos.editpic.Extensions.PREF_LANGUAGE_CODE
import com.gallery.photos.editpic.Extensions.gone
import com.gallery.photos.editpic.Extensions.handleBackPress
import com.gallery.photos.editpic.Extensions.hasAllFilesAccessAs
import com.gallery.photos.editpic.Extensions.log
import com.gallery.photos.editpic.Extensions.name.getMediaDatabase
import com.gallery.photos.editpic.Extensions.onClick
import com.gallery.photos.editpic.Extensions.setLanguageCode
import com.gallery.photos.editpic.Extensions.tos
import com.gallery.photos.editpic.Extensions.visible
import com.gallery.photos.editpic.Model.DeleteMediaModel
import com.gallery.photos.editpic.PopupDialog.TopMenuRecycleBinCustomPopup
import com.gallery.photos.editpic.R
import com.gallery.photos.editpic.RoomDB.Dao.DeleteMediaDao
import com.gallery.photos.editpic.databinding.ActivityRecycleBinBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException

class RecycleBinAct : AppCompatActivity() {
    private var deleteAdapter: DeleteAdapter? = null
    private lateinit var progressDialog: ProgressDialog
    private lateinit var deleteMediaDao: DeleteMediaDao
    private lateinit var deleteMediaModel: DeleteMediaModel
    private var deleteList: ArrayList<DeleteMediaModel> = arrayListOf()
    lateinit var bind: ActivityRecycleBinBinding

    fun toggleTopBarVisibility(isVisible: Boolean) {
        bind.rvDeleted.visibility = View.VISIBLE
        ("is RecycleBinAct Visisble: $isVisible").log()
        if (isVisible) {
            bind.selectedcontainerRecycleid.visible()
        } else {
            bind.selectedcontainerRecycleid.gone()
            bind.menuthreeid.visible()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLanguageCode(this, MyApplicationClass.getString(PREF_LANGUAGE_CODE)!!)
        bind = ActivityRecycleBinBinding.inflate(layoutInflater)
        setContentView(bind.root)

        deleteMediaModel = DeleteMediaModel()

        handleBackPress {
            if (bind.tvRecycleTital.text != getString(R.string.recyclebin)) {
                deleteAdapter!!.unselectAllItems()
                bind.tvRecycleTital.text = getString(R.string.recyclebin)
            } else {
                finish()
            }
        }

        progressDialog = ProgressDialog(this)
        progressDialog.setMessage(getString(R.string.please_wait))
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER) // STYLE_SPINNER for circular
        progressDialog.setCancelable(false)
        progressDialog.progress = 0
        progressDialog.max = 100

// Simulate progress

        deleteMediaDao = getMediaDatabase(this@RecycleBinAct).deleteMediaDao()

        deleteMediaDao.getAllMediaLive().observe(this) { mediaList ->
            // Update your RecyclerView adapter or UI

            deleteList.clear()

            mediaList.forEach {
                it.isSelect = false
            }

            deleteList.addAll(
                ArrayList(
                    mediaList
                )
            )
            runOnUiThread {
                if (deleteList.isNotEmpty()) {
                    bind.rvDeleted.visible()
                    bind.tvDataNotFound.gone()
                    bind.menuthreeid.visible()
                    bind.rvDeleted.adapter?.notifyDataSetChanged()
                } else {
                    bind.menuthreeid.gone()
                    bind.rvDeleted.gone()
                    bind.tvDataNotFound.visible()
                }
            }
            Log.d("LiveData", "Media list updated: ${mediaList.size} items")
        }

        deleteAdapter = DeleteAdapter(this@RecycleBinAct, deleteList) { onLongItemClick ->
            if (onLongItemClick) {
                bind.selectedcontainerRecycleid.visible()
//                binding.ivSearch.gone()
                bind.menuthreeid.gone()

            } else {
                bind.tvRecycleTital.text = getString(R.string.recyclebin)
//                binding.ivSearch.visible()
                bind.selectedcontainerRecycleid.gone()
                bind.menuthreeid.visible()
            }
        }
        bind.rvDeleted.adapter = deleteAdapter

        bind.apply {
            ivBack.onClick {
                if (bind.tvRecycleTital.text != getString(R.string.recyclebin)) {
                    deleteAdapter!!.unselectAllItems()
                    bind.tvRecycleTital.text = getString(R.string.recyclebin)
                } else {
                    finish()
                }
            }

            llRestore.onClick {
                if (!hasAllFilesAccessAs(this@RecycleBinAct)) {
                    (getString(R.string.all_files_access_required)).tos(this@RecycleBinAct)
                    AllFilesAccessDialog(this@RecycleBinAct){

                    }
//                    startActivityWithBundle<AllFilePermissionActivity>(Bundle().apply {
//                        putString("isFrom", "Activitys")
//                    })
                    return@onClick
                }

                val selectedList = deleteAdapter!!.selectedItems
                if (selectedList.size <= 100) {
                    restoreSelectedFilesFromRecycleBin(selectedList.toList())
                } else {
                    (getString(R.string.max_selection_limit_is_100)).tos(this@RecycleBinAct)
                }
            }


            llDelete.setOnClickListener {

                if (!hasAllFilesAccessAs(this@RecycleBinAct)) {
                    (getString(R.string.all_files_access_required)).tos(this@RecycleBinAct)
                    AllFilesAccessDialog(this@RecycleBinAct){

                    }
//                    startActivityWithBundle<AllFilePermissionActivity>(Bundle().apply {
//                        putString("isFrom", "Activitys")
//                    })
                    return@setOnClickListener
                }

                val selectedFiles = deleteAdapter!!.selectedItems

                if (selectedFiles.size <= 100) {
                    DeleteWithRememberDialog(this@RecycleBinAct, true) {
                        CoroutineScope(Dispatchers.Main).launch {
                            // Show Progress Dialog
                            val progressDialog = ProgressDialog(this@RecycleBinAct).apply {
                                setMessage(getString(R.string.deleting_files))
                                setCancelable(false)
                                show()
                            }

                            withContext(Dispatchers.IO) {
                                val deletionJobs = selectedFiles.map { mediaItem ->
                                    async {
                                        deleteFilePermanently(mediaItem)
                                    }
                                }

                                // Wait for all deletion tasks to complete
                                val results = deletionJobs.awaitAll()

                                // Remove successfully deleted files from the list
                                val successfullyDeleted =
                                    selectedFiles.filterIndexed { index, _ -> results[index] }
                                successfullyDeleted.forEach {
                                    deleteMediaDao.deleteMedia(it)
                                }
                            }

                            // Dismiss Progress Dialog and update UI
                            progressDialog.dismiss()
                            deleteAdapter!!.deleteSelectedItems()
                            deleteAdapter!!.unselectAllItems()
                            bind.tvRecycleTital.text = getString(R.string.recycle_bin)
                        }
                    }
                } else {
                    (getString(R.string.max_selection_limit_is_100)).tos(this@RecycleBinAct)
                }
            }

            menuthreeid.onClick {
                val topcustomtopcustompopup = TopMenuRecycleBinCustomPopup(this@RecycleBinAct) {
                    when (it) {
                        "tvSelectAll" -> {
                            deleteAdapter!!.selectAllItems()
                            bind.selectedcontainerRecycleid.visible()
                            bind.menuthreeid.gone()
                        }
                        "recyclebinid" -> {
                            if (deleteList.isNotEmpty()) {
                                restoreSelectedFilesFromRecycleBin(
                                    deleteList, true
                                )
                            }
                            else
                                (getString(R.string.do_not_have_any_recent_files)).tos(this@RecycleBinAct)
                        }
                    }
                }
                topcustomtopcustompopup.show(menuthreeid, 0, 0)
            }
        }
    }

    fun deleteFilePermanently(fileModel: DeleteMediaModel): Boolean {
        val file = File(fileModel.binPath)

        return if (!file.exists()) {
            Log.e(
                "DeleteFile",
                "File does not exist: ${fileModel.binPath}, removing from database."
            )

            CoroutineScope(Dispatchers.IO).launch {
                deleteMediaDao.getMediaById(fileModel.mediaId)?.let {
                    deleteMediaDao.deleteMedia(it)
                }
            }
            true
        } else {
            try {
                if (file.delete()) {
                    Log.d(
                        "DeleteFile",
                        getString(R.string.file_deleted_successfully, file.absolutePath)
                    )

                    CoroutineScope(Dispatchers.IO).launch {
                        deleteMediaDao.getMediaById(fileModel.mediaId)?.let {
                            deleteMediaDao.deleteMedia(it)
                        }
                    }
                    true
                } else {
                    Log.e("DeleteFile", "Failed to delete file: ${file.absolutePath}")
                    false
                }
            } catch (e: Exception) {
                Log.e("DeleteFile", "Error deleting file: ${e.message}")
                e.printStackTrace()
                false
            }
        }
    }

    fun restoreAllFilesFromRecycleBin(deleteList: List<DeleteMediaModel>) {
        progressDialog.show()  // Show loader before starting the restore process

        CoroutineScope(Dispatchers.Main).launch {
            try {
                withContext(Dispatchers.IO) {
                    // Launch individual coroutines for each file restore and wait for all of them to complete
                        async {
                            restoreSelectedFilesFromRecycleBin(
                                deleteList
                            )
                        }

                    // Delete all records from the recycle bin table
                    deleteMediaDao.deleteAllMedia()
                }

                // Update UI on the main thread after everything is done
                progressDialog.dismiss()
                ("${deleteList.size} files restored successfully").tos(this@RecycleBinAct)
                setResult(RESULT_OK)
                finish()  // Close the screen

                Log.d("RestoreAllFiles", "All media records removed from the recycle bin table.")
            } catch (e: Exception) {
                progressDialog.dismiss()
                Log.e("RestoreAllFiles", "Error while restoring files: ${e.message}")
                "Failed to restore files".tos(this@RecycleBinAct)
            }
        }
    }

    fun restoreSelectedFilesFromRecycleBin(
        selectedList: List<DeleteMediaModel>,
        isFromRestoreAll: Boolean = false
    ) {
        if (selectedList.isEmpty()) {
            Log.e("RestoreSelectedFiles", "No files selected for restoration.")
            return
        }

        progressDialog.setMessage(getString(R.string.restoring)) // Show restoring message
        progressDialog.show()  // Display progress dialog

        CoroutineScope(Dispatchers.Main).launch {
            try {
                withContext(Dispatchers.IO) {
                    selectedList.map { item ->
                        async {
                            restoreSelectFileFromRecycleBin(item)
                        }
                    }.awaitAll()  // Wait until all files are restored
                }
                withContext(Dispatchers.Main) {
                    progressDialog.dismiss()  // Dismiss the progress dialog
                    ("${selectedList.size} files restored successfully").tos(this@RecycleBinAct)
                    deleteAdapter!!.unselectAllItems()
                    bind.tvRecycleTital.text = getString(R.string.recycle_bin)

                    if (isFromRestoreAll)
                        finish()

                    Log.d("RestoreSelectedFiles", "All selected files restored successfully.")

                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    progressDialog.dismiss()
                    Log.e("RestoreSelectedFiles", "Error restoring files: ${e.message}")
                    getString(R.string.failed_to_restore_selected_files).tos(this@RecycleBinAct)
                }
            }
        }
    }

    // ✅ Handles different Android versions for restoring files
    private fun restoreSelectFileFromRecycleBin(item: DeleteMediaModel) {
        val sourceFile = File(item.binPath)
        val destinationFile = File(item.mediaPath)

        if (!sourceFile.exists()) {
            Log.e("RestoreFile", "Source file not found: ${item.binPath}")
            return
        }

        destinationFile.absolutePath.log()

        try {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                // ✅ Android 9 and below: Move file normally
                val parentDir = destinationFile.parentFile
                if (parentDir != null && !parentDir.exists()) {
                    parentDir.mkdirs() // Create parent directories if they don’t exist
                }
                sourceFile.copyTo(destinationFile, overwrite = true)
                sourceFile.delete() // Remove from recycle bin
            } else {
                // ✅ Android 10+ (Scoped Storage): Use MediaStore API
                val contentResolver = applicationContext.contentResolver
                val mimeType = getMimeType(sourceFile) ?: "image/jpeg" // Default to image/jpeg

                val collectionUri = when {
                    mimeType.startsWith("image/") -> MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    mimeType.startsWith("video/") -> MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                    mimeType.startsWith("audio/") -> MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                    else -> MediaStore.Downloads.EXTERNAL_CONTENT_URI // Fallback for non-media files
                }

                // Extract the relative path from the original mediaPath
                val originalPath = item.mediaPath
                val relativePath =
                    getRelativePath(originalPath) // Custom function to compute relative path

                val values = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, destinationFile.name)
                    put(
                        MediaStore.MediaColumns.RELATIVE_PATH,
                        relativePath
                    ) // Use original directory
                    put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
                    put(MediaStore.MediaColumns.IS_PENDING, 1) // Mark as pending
                }

                val uri = contentResolver.insert(collectionUri, values)
                uri?.let {
                    contentResolver.openOutputStream(it)?.use { outputStream ->
                        sourceFile.inputStream()
                            .use { inputStream -> inputStream.copyTo(outputStream) }
                    }
                    values.clear()
                    values.put(MediaStore.MediaColumns.IS_PENDING, 0) // Mark as complete
                    contentResolver.update(it, values, null, null)
                } ?: run {
                    Log.e("RestoreFile", "Failed to insert into MediaStore")
                    return
                }
                sourceFile.delete() // Remove from recycle bin
            }

            // ✅ Remove from Room database
            CoroutineScope(Dispatchers.IO).launch {
                deleteMediaDao?.deleteMedia(item)
            }

            Log.d("RestoreFile", "File restored successfully: ${destinationFile.absolutePath}")
        } catch (e: Exception) {
            Log.e("RestoreFile", "Failed to restore file: ${e.message}")
        }
    }

    // Helper function to compute the relative path
    private fun getRelativePath(fullPath: String): String {
        val storageRoot = "/storage/emulated/0/"
        return if (fullPath.startsWith(storageRoot)) {
            val relative = fullPath.substringAfter(storageRoot)
            val directory = relative.substringBeforeLast("/")
            directory.ifEmpty { "DCIM" } // Fallback to DCIM if no directory
        } else {
            "DCIM" // Fallback if path doesn’t match expected structure
        }
    }

    // Helper function to get MIME type (unchanged, assuming you have it)
    private fun getMimeType(file: File): String? {
        return MimeTypeMap.getSingleton()
            .getMimeTypeFromExtension(file.extension.lowercase())
    }
    // ✅ Function to get MIME type from file extension

    fun notifySystemGallery(filePath: String) {
        val file = File(filePath)
        val uri = Uri.fromFile(file)
        val intent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE).apply {
            data = uri
        }
        sendBroadcast(intent)
        Log.d("ContentResolver", "Broadcast sent for $filePath")
    }


    fun restoreFileFromRecycleBin(
        binFilePath: String, originalFilePath: String, mediaDateAdded: Long
    ): Boolean {
        val binFile = File(binFilePath)
        if (!binFile.exists()) {
            Log.e("RestoreFile", "File does not exist in recycle bin: $binFilePath")
            return false
        }

        val originalFile = File(originalFilePath)
        return try {
            binFile.copyTo(originalFile, overwrite = true)
            originalFile.setLastModified(mediaDateAdded)
            binFile.delete()
            Log.d("RestoreFile", "File restored to: ${originalFile.absolutePath}")
            notifySystemGallery(originalFilePath)  // Notify system gallery
            true
        } catch (e: IOException) {
            Log.e("RestoreFile", "IOException occurred: ${e.message}")
            false
        }
    }

    fun restoreSelectFileFromRecycleBin(
        binFilePath: String, originalFilePath: String, mediaDateAdded: Long, item: DeleteMediaModel
    ): Boolean {
        val binFile = File(binFilePath)
        if (!binFile.exists()) {
            Log.e("RestoreFile", "Selected File does not exist in recycle bin: $binFilePath")
            return false
        }

        val originalFile = File(originalFilePath)
        return try {
            binFile.copyTo(originalFile, overwrite = true)
            originalFile.setLastModified(mediaDateAdded)
            binFile.delete()
            Log.d("RestoreFile", "Selected File restored to: ${originalFile.absolutePath}")
            notifySystemGallery(originalFilePath)  // Notify system gallery
            CoroutineScope(Dispatchers.IO).launch {
                deleteMediaDao.getMediaById(item.mediaId)?.let { deleteMediaDao.deleteMedia(it) }
            }
            true
        } catch (e: IOException) {
            Log.e("RestoreFile", "Selected IOException occurred: ${e.message}")
            false
        }
    }
}