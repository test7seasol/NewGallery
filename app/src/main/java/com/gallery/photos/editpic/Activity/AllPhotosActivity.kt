package com.gallery.photos.editpic.Activity

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.ContentUris
import android.content.ContentValues
import android.content.Intent
import android.media.MediaScannerConnection
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.gallery.photos.editpic.Adapter.RvFolderAdapter
import com.gallery.photos.editpic.Adapter.RvInnerItemsAdapter
import com.gallery.photos.editpic.Adapter.RvSelectItemsAdapter
import com.gallery.photos.editpic.Dialogs.AllFilesAccessDialog
import com.gallery.photos.editpic.Extensions.PREF_LANGUAGE_CODE
import com.gallery.photos.editpic.Extensions.gone
import com.gallery.photos.editpic.Extensions.handleBackPress
import com.gallery.photos.editpic.Extensions.hasAllFilesAccessAs
import com.gallery.photos.editpic.Extensions.log
import com.gallery.photos.editpic.Extensions.onClick
import com.gallery.photos.editpic.Extensions.setLanguageCode
import com.gallery.photos.editpic.Extensions.toGson
import com.gallery.photos.editpic.Extensions.tos
import com.gallery.photos.editpic.Extensions.visible
import com.gallery.photos.editpic.Model.FolderModelItem
import com.gallery.photos.editpic.Model.MediaModelItem
import com.gallery.photos.editpic.R
import com.gallery.photos.editpic.Utils.MediaServices
import com.gallery.photos.editpic.Utils.SelectionAlLPhotos.selectionArrayList
import com.gallery.photos.editpic.databinding.ActivityAllimagesmediaBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.Locale

class AllPhotosActivity : AppCompatActivity() {
    lateinit var bind: ActivityAllimagesmediaBinding
    private var fromWhere = ""
    private var selectedFolder: FolderModelItem? = null // Track selected folder

    private var mediaList: ArrayList<MediaModelItem> = arrayListOf()
    private var list: ArrayList<FolderModelItem> = arrayListOf()
    private var selectedList: ArrayList<MediaModelItem> = arrayListOf()
    var selectAdapter: RvSelectItemsAdapter? = null

    @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLanguageCode(this, MyApplicationClass.getString(PREF_LANGUAGE_CODE)!!)
        bind = ActivityAllimagesmediaBinding.inflate(layoutInflater)
        setContentView(bind.root)
        fromWhere = intent.extras!!.getString("from")!!

        selectedList.clear()
        list.clear()
        mediaList.clear()

        selectAdapter = RvSelectItemsAdapter(this, selectedList) { it ->
            selectedList.remove(it)
            mediaList.find { media -> media.bucketId == it.bucketId }?.isSelect = false
            bind.tvSelectedItems.text = "${selectedList.size} Selected"
            bind.tvFolderName.text = "${selectedList.size}/500"
            if (selectedList.isEmpty()) {
                bind.tvNoSelect.visible()
            } else {
                bind.tvNoSelect.gone()
            }
            selectAdapter?.notifyDataSetChanged()
            bind.rvItems.adapter?.notifyDataSetChanged()
        }

        handleBackPress {
            if (bind.rvItems.isVisible) {
                bind.rvItems.gone()
                bind.rvFiles.visible()
            } else {
                finish()
            }
        }

        val progressDialog = ProgressDialog(this).apply {
            setMessage(getString(R.string.processing_files))
            setCancelable(false)
        }

        val folderList = MediaServices.getAllMediaFolders(this)
        list.addAll(folderList)

        bind.apply {
            rvItems.gone()
            rvFiles.visible()
            rvSelectedItem.adapter = selectAdapter

            tvNoSelect.visibility = if (fromWhere == "CreateNew") View.VISIBLE else View.GONE
            tvFolderName.text = if (fromWhere == "CreateNew") "0/500" else "Select Files"
            cdBottom.visibility = if (fromWhere == "CreateNew") View.VISIBLE else View.GONE
            rvSelectedItem.visibility = if (fromWhere == "CreateNew") View.INVISIBLE else View.INVISIBLE
            tvFolderName.visibility = if (fromWhere == "CreateNew") View.VISIBLE else View.GONE
            tvCreate.text = if (fromWhere == "CreateNew") "Done" else "Create"

            icBack.onClick { onBackPressedDispatcher.onBackPressed() }

            tvImport.onClick {
                setResult(RESULT_OK, Intent().putExtra("selectedlist", selectedList.toGson()))
                finish()
            }

            tvCreate.onClick {
                if (fromWhere == "CreateNew") {
                    if (selectedList.isNotEmpty()) {
                        setResult(RESULT_OK, Intent().putExtra("selectedlist", selectedList.toGson()))
                        finish()
                    } else {
                        "Please select files".tos(this@AllPhotosActivity)
                    }
                } else {
                    if (!hasAllFilesAccessAs(this@AllPhotosActivity)) {
                        (getString(R.string.all_files_access_required)).tos(this@AllPhotosActivity)
//                    activity.startActivityWithBundle<AllFilePermissionActivity>(Bundle().apply {
//                        putString("isFrom", "Activitys")
//                    })
                        AllFilesAccessDialog(this@AllPhotosActivity) {

                        }
                        return@onClick
                    }

                    setResult(
                        RESULT_OK,
                        Intent().putExtra("isFrom", "CreateClick").putExtra("where", fromWhere)
                    )
                    ("Create Folder").log()
                    finish()
                }
            }

            ivDelete.onClick {
                mediaList.forEach { it.isSelect = false }
                selectedList.clear()
                selectAdapter?.notifyDataSetChanged()
                rvItems.adapter?.notifyDataSetChanged()
                cdBottom.gone()
            }

            rvFiles.adapter = RvFolderAdapter(this@AllPhotosActivity, list) { folder ->
                selectedFolder = folder
                mediaList.clear()

                if (fromWhere == "Move" || fromWhere == "Copy") {
                    if (!hasAllFilesAccessAs(this@AllPhotosActivity)) {
                        (getString(R.string.all_files_access_required)).tos(this@AllPhotosActivity)
                        AllFilesAccessDialog(this@AllPhotosActivity) {}
                        return@RvFolderAdapter
                    }

                    progressDialog.show()
                    CoroutineScope(Dispatchers.IO).launch {
                        var successCount = 0

                        selectionArrayList.forEach { filePath ->
                            try {
                                val sourceFile = File(filePath)
                                val destinationFolderPath = getFolderPathByBucketId(folder.bucketId)
                                    ?: run {
                                        Log.e("FileOperation", "Couldn't get destination folder path")
                                        return@forEach
                                    }

                                val destinationFolder = File(destinationFolderPath).apply {
                                    if (!exists() && !mkdirs()) {
                                        Log.e("FileOperation", "Failed to create destination folder")
                                        return@forEach
                                    }
                                }

                                val destinationFile = File(destinationFolder, sourceFile.name)

                                val result = when (fromWhere) {
                                    "Copy" -> copyFileWithScopedStorage(sourceFile, destinationFile)
                                    "Move" -> moveFileWithScopedStorage(sourceFile, destinationFile)
                                    else -> false
                                }

                                if (result) {
                                    successCount++
                                    updateMediaStore(sourceFile, destinationFile, fromWhere == "Move")
                                }
                            } catch (e: Exception) {
                                Log.e("FileOperation", "Error during file operation", e)
                            }
                        }

                        withContext(Dispatchers.Main) {
                            progressDialog.dismiss()
                            showOperationResult(successCount, selectionArrayList.size)
                            if (successCount > 0) {
                                setResult(RESULT_OK)
                                finish()
                            }
                        }
                    }
                } else {
                    rvItems.visible()
                    rvFiles.gone()
                    mediaList = MediaServices.getMediaByBucketId(this@AllPhotosActivity, folder.bucketId)

                    mediaList.forEach {
                        selectedList.forEach { selected ->
                            if (it.bucketId == selected.bucketId) it.isSelect = true
                        }
                    }

                    rvItems.adapter = RvInnerItemsAdapter(this@AllPhotosActivity, mediaList) {
                        it.isSelect = !it.isSelect
                        rvSelectedItem.visible()
                        tvNoSelect.gone()
                        if (it.isSelect) {
                            selectedList.add(it)
                            rvSelectedItem.scrollToPosition(selectedList.size - 1)
                        } else selectedList.remove(it)

                        if (selectedList.isEmpty()) cdBottom.gone() else cdBottom.visible()
                        tvSelectedItems.text = "${selectedList.size} Selected"
                        tvFolderName.text = "${selectedList.size}/500"
                        rvItems.adapter?.notifyDataSetChanged()
                        selectAdapter?.notifyDataSetChanged()
                    }
                }
            }
        }
    }

    private fun copyFileWithScopedStorage(sourceFile: File, destinationFile: File): Boolean {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // Use MediaStore API for Android 10+
                val mimeType = getMimeType(sourceFile.path)
                val collection = when {
                    mimeType?.startsWith("video/") == true -> MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                    else -> MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                }

                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, sourceFile.name)
                    put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
                    put(MediaStore.MediaColumns.RELATIVE_PATH,
                        "${Environment.DIRECTORY_PICTURES}/${File(destinationFile.parent).name}")
                    put(MediaStore.MediaColumns.IS_PENDING, 1)
                }

                val uri = contentResolver.insert(collection, contentValues) ?: return false

                contentResolver.openOutputStream(uri)?.use { outputStream ->
                    sourceFile.inputStream().use { inputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }

                contentValues.clear()
                contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
                contentResolver.update(uri, contentValues, null, null)
                true
            } else {
                // Legacy file copy for Android 9 and below
                sourceFile.inputStream().use { input ->
                    destinationFile.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
                true
            }
        } catch (e: Exception) {
            Log.e("FileOperation", "Copy failed", e)
            false
        }
    }

    private fun moveFileWithScopedStorage(sourceFile: File, destinationFile: File): Boolean {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // First copy using scoped storage
                if (copyFileWithScopedStorage(sourceFile, destinationFile)) {
                    // Then delete original
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        val collection = when {
                            getMimeType(sourceFile.path)?.startsWith("video/") == true ->
                                MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                            else -> MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                        }
                        contentResolver.delete(
                            ContentUris.withAppendedId(collection, getMediaId(sourceFile)),
                            null,
                            null
                        ) > 0
                    } else {
                        sourceFile.delete()
                    }
                } else false
            } else {
                // Legacy move operation for Android 9 and below
                sourceFile.renameTo(destinationFile) || run {
                    if (copyFileWithScopedStorage(sourceFile, destinationFile)) {
                        sourceFile.delete()
                    } else false
                }
            }
        } catch (e: Exception) {
            Log.e("FileOperation", "Move failed", e)
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

        return contentResolver.query(uri, projection, selection, selectionArgs, null)?.use { cursor ->
            if (cursor.moveToFirst()) cursor.getLong(0) else -1L
        } ?: -1L
    }

    private fun updateMediaStore(sourceFile: File, destinationFile: File, isMove: Boolean) {
        MediaScannerConnection.scanFile(
            this,
            arrayOf(destinationFile.absolutePath),
            null,
            null
        )
        if (isMove) {
            MediaScannerConnection.scanFile(
                this,
                arrayOf(sourceFile.absolutePath),
                null,
                null
            )
        }
    }

    private fun showOperationResult(successCount: Int, totalFiles: Int) {
        val message = when {
            successCount == totalFiles ->
                "Successfully ${fromWhere.lowercase()}d all $successCount files"
            successCount > 0 ->
                "Successfully ${fromWhere.lowercase()}d $successCount of $totalFiles files"
            else ->
                "Failed to ${fromWhere.lowercase()} files"
        }
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
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

    @SuppressLint("Range")
    fun getFolderPathByBucketId(bucketId: String): String? {
        val projection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            arrayOf(
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.RELATIVE_PATH
            )
        } else {
            arrayOf(
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Images.Media.DATA
            )
        }

        val selection = "${MediaStore.Images.Media.BUCKET_ID} = ?"
        val selectionArgs = arrayOf(bucketId)

        return contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            null
        )?.use { cursor ->
            if (cursor.moveToFirst()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.RELATIVE_PATH))
                        ?.let {
                            Environment.getExternalStorageDirectory().path + "/" + it
                        }?.removeSuffix("/")
                        ?: cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
                            ?.substringBeforeLast("/")
                } else {
                    cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
                        ?.substringBeforeLast("/")
                }
            } else null
        }
    }
}