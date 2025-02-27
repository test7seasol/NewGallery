package com.gallery.photos.editpic.Activity

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
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

class AllPhotosActivity : AppCompatActivity() {
    lateinit var bind: ActivityAllimagesmediaBinding
    private var fromWhere = ""

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

        val folderList = MediaServices.getAllMediaFolders(this)

        list.addAll(folderList)

        bind.apply {

            rvItems.gone()
            rvFiles.visible()

            rvSelectedItem.adapter = selectAdapter

            tvNoSelect.visibility = if (fromWhere == "CreateNew") View.VISIBLE else View.GONE
            tvFolderName.text = if (fromWhere == "CreateNew") "0/500" else "Select Files"
            cdBottom.visibility = if (fromWhere == "CreateNew") View.VISIBLE else View.GONE
            rvSelectedItem.visibility =
                if (fromWhere == "CreateNew") View.INVISIBLE else View.INVISIBLE
            tvFolderName.visibility = if (fromWhere == "CreateNew") View.VISIBLE else View.GONE
            tvCreate.text = if (fromWhere == "CreateNew") "Done" else "Create"

            icBack.onClick {
                onBackPressedDispatcher.onBackPressed()
            }

            tvImport.onClick {
                setResult(RESULT_OK, Intent().putExtra("selectedlist", selectedList.toGson()))
                finish()
            }

            tvCreate.onClick {
                if (fromWhere == "CreateNew") {
                    if (selectedList.isNotEmpty()) {
                        setResult(
                            RESULT_OK,
                            Intent().putExtra("selectedlist", selectedList.toGson())
                        )
                        finish()
                    } else {
                        "Please select a files".tos(this@AllPhotosActivity)
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

            val progressDialog = ProgressDialog(this@AllPhotosActivity).apply {
                setMessage(getString(R.string.processing_files))
                setCancelable(false)
            }

            rvFiles.adapter = RvFolderAdapter(this@AllPhotosActivity, list) { it ->
                mediaList.clear()

                if (fromWhere == "Move" || fromWhere == "Copy") {
                    if (!hasAllFilesAccessAs(this@AllPhotosActivity)) {
                        (getString(R.string.all_files_access_required)).tos(this@AllPhotosActivity)
//                    activity.startActivityWithBundle<AllFilePermissionActivity>(Bundle().apply {
//                        putString("isFrom", "Activitys")
//                    })
                        AllFilesAccessDialog(this@AllPhotosActivity) {

                        }
                        return@RvFolderAdapter
                    }

                    progressDialog.show() // Show loader
                    CoroutineScope(Dispatchers.IO).launch {
                        var successCount = 0

                        selectionArrayList.forEach { filePath ->
                            val sourceFile = File(filePath)
                            val destinationFolder = getFolderPathByBucketId(it.bucketId)

                            if (destinationFolder != null) {
                                val destinationDir = File(destinationFolder)
                                if (!destinationDir.exists()) destinationDir.mkdirs() // Create directory if it doesn't exist

                                val destinationFile = File(destinationDir, sourceFile.name)

                                if (sourceFile.exists()) {
                                    try {
                                        // If the destination file exists, delete it to overwrite
                                        if (destinationFile.exists()) destinationFile.delete()

                                        val isSuccessful = when (fromWhere) {
                                            "Move" -> moveFile(sourceFile, destinationFile)
                                            "Copy" -> copyFile(sourceFile, destinationFile)
                                            else -> false
                                        }

                                        if (isSuccessful) successCount++
                                    } catch (e: Exception) {
                                        Log.e(
                                            "FileOperation",
                                            "Error during file ${fromWhere.toLowerCase()}: ${e.message}"
                                        )
                                    }
                                } else {
                                    Log.e(
                                        "FileOperation",
                                        "Source file does not exist: ${sourceFile.path}"
                                    )
                                }
                            } else {
                                Log.e(
                                    "FileOperation",
                                    "Destination folder not found for bucket_id: ${it.bucketId}"
                                )
                            }
                        }

                        withContext(Dispatchers.Main) {
                            progressDialog.dismiss() // Hide loader

                            if (successCount == selectionArrayList.size) {
                                "${successCount} files ${fromWhere.toLowerCase()}d successfully".tos(
                                    this@AllPhotosActivity
                                )
                            } else {
                                "Some files failed to ${fromWhere.toLowerCase()}".tos(this@AllPhotosActivity)
                            }

                            setResult(RESULT_OK)
                            finish() // Close the activity
                        }
                    }
                } else {
                    rvItems.visible()
                    rvFiles.gone()
                    mediaList =
                        MediaServices.getMediaByBucketId(this@AllPhotosActivity, it.bucketId)

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

                        if (selectedList.isEmpty()) {
                            cdBottom.gone()
                        } else {
                            cdBottom.visible()
                        }

                        tvSelectedItems.text = "${selectedList.size} Selected"
                        tvFolderName.text = "${selectedList.size}/500"
                        rvItems.adapter?.notifyDataSetChanged()
                        selectAdapter?.notifyDataSetChanged()
                    }
                }
            }
        }
    }

    fun copyFile(sourceFile: File, destinationFile: File): Boolean {
        return try {
            sourceFile.inputStream().use { input ->
                destinationFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun getFolderPathByBucketId(bucketId: String): String? {
        Log.d("BucketDebug", "Searching for bucketId: $bucketId") // Log bucketId for debugging


        val projection = arrayOf(
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.RELATIVE_PATH
        )

        val selection = "${MediaStore.Images.Media.BUCKET_ID} = ?"
        val selectionArgs = arrayOf(bucketId)

        contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, selection, selectionArgs, null
        )?.use { cursor ->
            if (cursor.moveToFirst()) {
                val bucketName =
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME))
                Log.d("BucketDebug", "Found folder: $bucketName")

                return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                    val dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                    File(cursor.getString(dataColumn)).parent // Returns actual file directory
                } else {
                    val relativePathColumn =
                        cursor.getColumnIndexOrThrow(MediaStore.Images.Media.RELATIVE_PATH)
                    val fullPath = "${Environment.getExternalStorageDirectory()}/${
                        cursor.getString(relativePathColumn)
                    }".removeSuffix("/")
                    Log.d("BucketDebug", "Resolved path: $fullPath")
                    fullPath
                }
            } else {
                Log.e("BucketDebug", "No folder found for bucketId: $bucketId")
            }
        }

//        logAllBucketIds()

        return null
    }

//    todo: below android 15 work
//    fun getFolderPathByBucketId(bucketId: String): String? {
//        val projection =
//            arrayOf(MediaStore.Images.Media.BUCKET_DISPLAY_NAME, MediaStore.Images.Media.DATA)
//        val selection = "${MediaStore.Images.Media.BUCKET_ID} = ?"
//        val selectionArgs = arrayOf(bucketId)
//
//        contentResolver.query(
//            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, selection, selectionArgs, null
//        )?.use { cursor ->
//            if (cursor.moveToFirst()) {
//                val dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
//                val filePath = cursor.getString(dataColumn)
//                return File(filePath).parent // Return the parent directory of the first matching file
//            }
//        }
//        return null
//    }

    fun moveFile(sourceFile: File, destinationFile: File): Boolean {
        return try {
            sourceFile.copyTo(destinationFile, overwrite = true)
            sourceFile.delete()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
