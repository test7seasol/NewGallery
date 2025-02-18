package com.gallery.photos.editpic.Activity

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.gallery.photos.editpic.Adapter.DeleteAdapter
import com.gallery.photos.editpic.Extensions.gone
import com.gallery.photos.editpic.Extensions.name.getMediaDatabase
import com.gallery.photos.editpic.Extensions.onClick
import com.gallery.photos.editpic.Extensions.tos
import com.gallery.photos.editpic.Extensions.visible
import com.gallery.photos.editpic.Model.DeleteMediaModel
import com.gallery.photos.editpic.PopupDialog.TopMenuRecycleBinCustomPopup
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
    private lateinit var progressDialog: ProgressDialog
    private lateinit var deleteMediaDao: DeleteMediaDao
    private var deleteList: ArrayList<DeleteMediaModel> = arrayListOf()
    lateinit var bind: ActivityRecycleBinBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityRecycleBinBinding.inflate(layoutInflater)
        setContentView(bind.root)


        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Please wait...")
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
                    bind.rvDeleted.adapter?.notifyDataSetChanged()
                } else {
                    bind.rvDeleted.gone()
                    bind.tvDataNotFound.visible()
                }
            }
            Log.d("LiveData", "Media list updated: ${mediaList.size} items")
        }

        bind.rvDeleted.adapter = DeleteAdapter(this@RecycleBinAct, deleteList)

        bind.apply {
            ivBack.onClick { finish() }

            menuthreeid.onClick {
                val topcustomtopcustompopup = TopMenuRecycleBinCustomPopup(this@RecycleBinAct) {
                    when (it) {
                        "recyclebinid" -> {
                            if (deleteList.isNotEmpty())
                                restoreAllFilesFromRecycleBin(deleteList)
                            else
                                ("Do not have any recent files!").tos(this@RecycleBinAct)
                        }
                    }
                }
                topcustomtopcustompopup.show(menuthreeid, 0, 0)
            }
        }
    }

    fun restoreAllFilesFromRecycleBin(deleteList: List<DeleteMediaModel>) {
        progressDialog.show()  // Show loader before starting the restore process

        CoroutineScope(Dispatchers.Main).launch {
            try {
                withContext(Dispatchers.IO) {
                    // Launch individual coroutines for each file restore and wait for all of them to complete
                    deleteList.map { item ->
                        async {
                            restoreFileFromRecycleBin(
                                item.binPath,
                                item.mediaPath,
                                item.mediaDateAdded
                            )
                        }
                    }.awaitAll()  // Wait for all restore operations to finish

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