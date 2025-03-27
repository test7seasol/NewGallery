package com.gallery.photos.editpic.Activity

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
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
import java.io.FileNotFoundException
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
                            restoreSelectFileFromRecycleBin(item, this@RecycleBinAct)
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
    private fun restoreSelectFileFromRecycleBin(item: DeleteMediaModel, context: Context) {
        val sourceFile = File(item.binPath)
        if (!sourceFile.exists()) {
            Log.e("RestoreFile", "Source file not found: ${item.binPath}")
            return
        }

        try {
            val contentResolver = context.contentResolver
            val mimeType = getMimeType(context, sourceFile) ?: "application/octet-stream"

            when {
                item.mediaPath.startsWith("content://") -> {
                    val originalUri = Uri.parse(item.mediaPath)

                    // First verify if the original URI still exists and is writable
                    if (isUriValidAndWritable(contentResolver, originalUri)) {
                        try {
                            restoreViaContentUri(context, sourceFile, originalUri, mimeType)
                            return
                        } catch (e: Exception) {
                            Log.w("RestoreFile", "Failed to restore to original URI, falling back", e)
                        }
                    }

                    // If original URI is invalid, create new entry in appropriate location
                    Log.w("RestoreFile", "Original URI not found, creating new entry")
                    val newUri = restoreViaMediaStore(
                        context = context,
                        sourceFile = sourceFile,
                        displayName = sourceFile.name,
                        mimeType = mimeType,
                        preferredLocation = getPreferredLocationFromOriginalUri(context, originalUri, mimeType)
                    )

                    // Update database with new URI if needed
                    CoroutineScope(Dispatchers.IO).launch {
                        Log.w("RestoreFile", "Udpate Database")

//                        deleteMediaDao.deleteMedia(deleteMediaModel)
                    }
                }

                Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && !isLegacyStorageEnabled(context) -> {
                    restoreViaMediaStore(context, sourceFile, sourceFile.name, mimeType)
                }

                else -> {
                    // For file paths, try original path first, then fallback
                    val destinationFile = File(item.mediaPath)
                    if (destinationFile.parentFile?.exists() == true) {
                        restoreViaFileSystem(sourceFile, destinationFile, context, mimeType)
                    } else {
                        // Fallback to standard location
                        val fallbackPath = getFallbackPath(context, mimeType, sourceFile.name)
                        restoreViaFileSystem(sourceFile, File(fallbackPath), context, mimeType)
                    }
                }
            }

            // Remove from database
            CoroutineScope(Dispatchers.IO).launch {
                deleteMediaDao.deleteMedia(item)
            }

        } catch (e: Exception) {
            Log.e("RestoreFile", "Error restoring file: ${e.message}", e)
            // Consider showing error to user
        }
    }

    private fun isUriValidAndWritable(contentResolver: ContentResolver, uri: Uri): Boolean {
        return try {
            contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                cursor.count > 0
            } ?: false
        } catch (e: Exception) {
            false
        }
    }

    private fun getPreferredLocationFromOriginalUri(context: Context, originalUri: Uri, mimeType: String): String? {
        return try {
            context.contentResolver.query(originalUri,
                arrayOf(MediaStore.MediaColumns.RELATIVE_PATH),
                null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    cursor.getString(0)
                } else {
                    getDefaultRelativePath(mimeType)
                }
            }
        } catch (e: Exception) {
            getDefaultRelativePath(mimeType)
        }
    }

    private fun restoreViaMediaStore(
        context: Context,
        sourceFile: File,
        displayName: String,
        mimeType: String,
        preferredLocation: String? = null
    ): Uri {
        val contentResolver = context.contentResolver
        val collectionUri = getMediaCollectionUri(mimeType)

        val values = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, displayName)
            put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
            put(MediaStore.MediaColumns.SIZE, sourceFile.length())
            put(MediaStore.MediaColumns.DATE_MODIFIED, sourceFile.lastModified() / 1000)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.MediaColumns.RELATIVE_PATH,
                    preferredLocation ?: getDefaultRelativePath(mimeType))
                put(MediaStore.MediaColumns.IS_PENDING, 1)
            } else {
                put(MediaStore.MediaColumns.DATA,
                    preferredLocation?.let { "$it/$displayName" }
                        ?: getFallbackPath(context, mimeType, displayName))
            }
        }

        val uri = contentResolver.insert(collectionUri, values)
            ?: throw IOException("Failed to create MediaStore entry")

        try {
            contentResolver.openOutputStream(uri)?.use { outputStream ->
                sourceFile.inputStream().use { inputStream ->
                    inputStream.copyTo(outputStream)
                }
            } ?: throw IOException("Failed to open output stream")

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                values.clear()
                values.put(MediaStore.MediaColumns.IS_PENDING, 0)
                contentResolver.update(uri, values, null, null)
            } else {
                MediaScannerConnection.scanFile(
                    context,
                    arrayOf(getPathFromContentValues(values)),
                    arrayOf(mimeType),
                    null
                )
            }

            if (!sourceFile.delete()) {
                Log.w("RestoreFile", "Failed to delete source file")
            }

            return uri
        } catch (e: Exception) {
            contentResolver.delete(uri, null, null)
            throw IOException("Failed to write file content", e)
        }
    }

    private fun getMediaCollectionUri(mimeType: String): Uri {
        return when {
            mimeType.startsWith("image/") -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
                } else {
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                }
            }
            mimeType.startsWith("video/") -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
                } else {
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                }
            }
            mimeType.startsWith("audio/") -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
                } else {
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                }
            }
            else -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
                } else {
                    MediaStore.Files.getContentUri("external")
                }
            }
        }
    }

    private fun restoreViaContentUri(context: Context, sourceFile: File, destinationUri: Uri, mimeType: String) {
        try {
            context.contentResolver.openOutputStream(destinationUri)?.use { outputStream ->
                sourceFile.inputStream().use { inputStream ->
                    inputStream.copyTo(outputStream)
                }
            } ?: throw IOException("Failed to open output stream")

            if (!sourceFile.delete()) {
                Log.w("RestoreFile", "Failed to delete source file")
            }
        } catch (e: FileNotFoundException) {
            Log.w("RestoreFile", "Original URI not writable, creating new entry")
            restoreViaMediaStore(context, sourceFile, sourceFile.name, mimeType)
        } catch (e: Exception) {
            Log.e("RestoreFile", "Error restoring via content URI", e)
            throw e
        }
    }

    private fun restoreViaFileSystem(sourceFile: File, destinationFile: File, context: Context, mimeType: String) {
        try {
            destinationFile.parentFile?.let { parent ->
                if (!parent.exists() && !parent.mkdirs()) {
                    throw IOException("Failed to create parent directory")
                }
            }

            sourceFile.copyTo(destinationFile, overwrite = true)

            if (!sourceFile.delete()) {
                Log.w("RestoreFile", "Failed to delete source file")
            }

            MediaScannerConnection.scanFile(
                context,
                arrayOf(destinationFile.absolutePath),
                arrayOf(mimeType),
                null
            )
        } catch (e: Exception) {
            Log.e("RestoreFile", "Error restoring via file system", e)
            throw e
        }
    }

    // Helper functions
    private fun getFallbackPath(context: Context, mimeType: String, fileName: String): String {
        val baseDir = when {
            mimeType.startsWith("image/") -> Environment.DIRECTORY_PICTURES
            mimeType.startsWith("video/") -> Environment.DIRECTORY_MOVIES
            mimeType.startsWith("audio/") -> Environment.DIRECTORY_MUSIC
            else -> Environment.DIRECTORY_DOWNLOADS
        }
        return File(context.getExternalFilesDir(baseDir), fileName).absolutePath
    }

    private fun getDefaultRelativePath(mimeType: String): String {
        return when {
            mimeType.startsWith("image/") -> Environment.DIRECTORY_PICTURES + "/Restored"
            mimeType.startsWith("video/") -> Environment.DIRECTORY_MOVIES + "/Restored"
            mimeType.startsWith("audio/") -> Environment.DIRECTORY_MUSIC + "/Restored"
            else -> Environment.DIRECTORY_DOWNLOADS + "/Restored"
        }
    }

    private fun getPathFromContentValues(values: ContentValues): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Environment.getExternalStorageDirectory().absolutePath +
                    "/${values.getAsString(MediaStore.MediaColumns.RELATIVE_PATH)}/" +
                    values.getAsString(MediaStore.MediaColumns.DISPLAY_NAME)
        } else {
            values.getAsString(MediaStore.MediaColumns.DATA) ?: ""
        }
    }

    private fun isLegacyStorageEnabled(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageLegacy()
        } else {
            context.getSharedPreferences("storage_prefs", Context.MODE_PRIVATE)
                .getBoolean("legacy_storage", false)
        }
    }




    private fun restoreViaMediaStore(context: Context, sourceFile: File, originalPath: String?, mimeType: String): Uri {
        val contentResolver = context.contentResolver

        // 1. Determine the correct MediaStore collection URI based on Android version
        val collectionUri = when {
            mimeType.startsWith("image/") -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
                } else {
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                }
            }
            mimeType.startsWith("video/") -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
                } else {
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                }
            }
            mimeType.startsWith("audio/") -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
                } else {
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                }
            }
            else -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
                } else {
                    MediaStore.Files.getContentUri("external")
                }
            }
        }

        // 2. Extract file information
        val fileName = sourceFile.name
        val relativePath = originalPath?.let { getRelativePath(it) } ?: getDefaultRelativePath(mimeType)

        // 3. Prepare ContentValues with all required fields
        val values = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, mimeType)

            // Only add RELATIVE_PATH for Android Q+
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.MediaColumns.RELATIVE_PATH, relativePath)
            } else {
                // For older versions, we need to set the full path
                val destinationPath = if (originalPath != null && File(originalPath).parentFile?.exists() == true) {
                    originalPath
                } else {
                    getFallbackPath(context, mimeType, fileName)
                }
                put(MediaStore.MediaColumns.DATA, destinationPath)
            }

            put(MediaStore.MediaColumns.SIZE, sourceFile.length())
            put(MediaStore.MediaColumns.DATE_MODIFIED, sourceFile.lastModified() / 1000)

            // For Android Q+, use IS_PENDING
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.MediaColumns.IS_PENDING, 1)
            }
        }

        // 4. Try to insert the MediaStore entry
        val uri = contentResolver.insert(collectionUri, values)
            ?: throw IOException("Failed to create MediaStore entry - insert returned null")

        try {
            // 5. Write the file content
            contentResolver.openOutputStream(uri)?.use { outputStream ->
                sourceFile.inputStream().use { inputStream ->
                    inputStream.copyTo(outputStream)
                }
            } ?: throw IOException("Failed to open output stream for $uri")

            // 6. For Android Q+, mark as not pending
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                values.clear()
                values.put(MediaStore.MediaColumns.IS_PENDING, 0)
                contentResolver.update(uri, values, null, null)
            }

            // 7. Delete the source file from recycle bin
            if (!sourceFile.delete()) {
                Log.w("RestoreFile", "Failed to delete source file: ${sourceFile.absolutePath}")
            }

            // 8. For pre-Q, notify media scanner
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                MediaScannerConnection.scanFile(
                    context,
                    arrayOf(getPathFromContentValues(values)),
                    arrayOf(mimeType),
                    null
                )
            }

            return uri
        } catch (e: Exception) {
            // Clean up if anything went wrong
            contentResolver.delete(uri, null, null)
            throw IOException("Failed to write file content: ${e.message}", e)
        }
    }



    private fun getRelativePath(mediaPath: String): String {
        return try {
            val externalStoragePath = Environment.getExternalStorageDirectory().absolutePath
            if (mediaPath.startsWith(externalStoragePath)) {
                mediaPath.removePrefix("$externalStoragePath/").substringBeforeLast("/")
            } else {
                getDefaultRelativePathFromMime(mediaPath)
            }
        } catch (e: Exception) {
            getDefaultRelativePathFromMime(mediaPath)
        }
    }

    private fun getDefaultRelativePathFromMime(path: String): String {
        val extension = path.substringAfterLast('.', "").lowercase()
        return when (extension) {
            "jpg", "jpeg", "png", "gif", "webp" -> Environment.DIRECTORY_PICTURES + "/Restored"
            "mp4", "mkv", "mov", "avi" -> Environment.DIRECTORY_MOVIES + "/Restored"
            "mp3", "wav", "ogg", "m4a" -> Environment.DIRECTORY_MUSIC + "/Restored"
            else -> Environment.DIRECTORY_DOWNLOADS + "/Restored"
        }
    }

    private fun getMimeType(context: Context, file: File): String? {
        return try {
            val extension = MimeTypeMap.getFileExtensionFromUrl(file.path)?.lowercase()
            MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
                ?: context.contentResolver.getType(Uri.fromFile(file))
                ?: "application/octet-stream"
        } catch (e: Exception) {
            "application/octet-stream"
        }
    }



    // Restore using MediaStore by recreating the file
    @SuppressLint("NewApi")


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
}