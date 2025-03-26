package com.gallery.photos.editpic.Activity

import android.app.ProgressDialog
import android.content.ContentValues
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.viewpager2.widget.ViewPager2
import com.gallery.photos.editpic.Adapter.DeleteViewPagerAdapter
import com.gallery.photos.editpic.Dialogs.AllFilesAccessDialog
import com.gallery.photos.editpic.Dialogs.DeleteWithRememberDialog
import com.gallery.photos.editpic.Extensions.PREF_LANGUAGE_CODE
import com.gallery.photos.editpic.Extensions.hasAllFilesAccessAs
import com.gallery.photos.editpic.Extensions.log
import com.gallery.photos.editpic.Extensions.name.getMediaDatabase
import com.gallery.photos.editpic.Extensions.onClick
import com.gallery.photos.editpic.Extensions.setLanguageCode
import com.gallery.photos.editpic.Extensions.tos
import com.gallery.photos.editpic.Model.DeleteMediaModel
import com.gallery.photos.editpic.R
import com.gallery.photos.editpic.RoomDB.Dao.DeleteMediaDao
import com.gallery.photos.editpic.Utils.DeleteMediaStoreSingleton
import com.gallery.photos.editpic.Utils.DeleteMediaStoreSingleton.deleteselectedPosition
import com.gallery.photos.editpic.databinding.ActivityDeleteviewPagerBinding
import com.gallery.photos.editpic.myadsworld.MyAddPrefs
import com.gallery.photos.editpic.myadsworld.MyAllAdCommonClass
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DeleteViewPagerActivity : BaseActivity() {
    private var viewpagerselectedPosition: Int = 1
    var deleteMediaModel: DeleteMediaModel? = null
    private lateinit var binding: ActivityDeleteviewPagerBinding
    private lateinit var viewPagerAdapter: DeleteViewPagerAdapter
    private var imageListDelete: ArrayList<DeleteMediaModel> = arrayListOf()
    var deleteMediaDao: DeleteMediaDao? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLanguageCode(this, MyApplicationClass.getString(PREF_LANGUAGE_CODE)!!)
        binding = ActivityDeleteviewPagerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        applyStatusBarColor()

        imageListDelete = DeleteMediaStoreSingleton.deleteimageList
        viewpagerselectedPosition = DeleteMediaStoreSingleton.deleteselectedPosition

//        ("Delete onPageSelected: $viewpagerselectedPosition").log()

        if (imageListDelete.isEmpty()) {
            Log.e("DeleteViewPagerActivity", "No images available for deletion.")
            finish()  // Exit the activity to prevent the crash
            return
        }

        // Ensure valid index
        viewpagerselectedPosition = viewpagerselectedPosition.coerceIn(0, imageListDelete.size - 1)
        deleteMediaModel = imageListDelete[viewpagerselectedPosition]

        setupViewPager(imageListDelete, viewpagerselectedPosition)

        hideBottomNavigationBar(R.color.black)

        MyAllAdCommonClass.showAdmobBanner(
            this@DeleteViewPagerActivity,
            binding.bannerContainer,
            binding.shimmerContainerBanner,
            false,
            MyAddPrefs(this@DeleteViewPagerActivity).admBannerId
        )

        binding.ivBack.setOnClickListener {
            finish()
        }

        deleteMediaDao = getMediaDatabase(this).deleteMediaDao()

        binding.apply {

            bottomActions.fileDelete.onClick {
                if (!hasAllFilesAccessAs(this@DeleteViewPagerActivity)) {
                    (getString(R.string.all_files_access_required)).tos(this@DeleteViewPagerActivity)
                    AllFilesAccessDialog(this@DeleteViewPagerActivity){

                    }
//                    startActivityWithBundle<AllFilePermissionActivity>(Bundle().apply {
//                        putString("isFrom", "Activitys")
//                    })
                    return@onClick
                }
                DeleteWithRememberDialog(this@DeleteViewPagerActivity, isFromBin = true) {
                    run {
                        deleteMediaModel?.let { it1 -> permanentlyDeleteFile(it1) }
                    }
                }
            }
            bottomActions.fileRestore.onClick {
                if (!hasAllFilesAccessAs(this@DeleteViewPagerActivity)) {
                    (getString(R.string.all_files_access_required)).tos(this@DeleteViewPagerActivity)
                    AllFilesAccessDialog(this@DeleteViewPagerActivity){

                    }
                    return@onClick
                }
                val deletedlist: ArrayList<DeleteMediaModel> = arrayListOf()
                deletedlist.clear()
                deletedlist.add(deleteMediaModel!!)
                restoreSelectedFilesFromRecycleBin(deletedlist)
            }
        }
    }

    fun restoreSelectedFilesFromRecycleBin(selectedList: List<DeleteMediaModel>) {
        if (selectedList.isEmpty()) {
            Log.e("RestoreSelectedFiles", "No files selected for restoration.")
            return
        }

        CoroutineScope(Dispatchers.Main).launch {
            val progressDialog = ProgressDialog(this@DeleteViewPagerActivity).apply {
                setMessage("Restoring files...")
                setCancelable(false)
                show()
            }

            try {
                // Restore files in background
                val restoreResults = withContext(Dispatchers.IO) {
                    selectedList.map { item ->
                        async {
                            restoreSelectFileFromRecycleBin(item)
                        }
                    }.awaitAll()
                }

                // Remove restored items from our current list
                val restoredIds = selectedList.map { it.mediaId }
                imageListDelete.removeAll { it.mediaId in restoredIds }

                withContext(Dispatchers.Main) {
                    progressDialog.dismiss()

                    if (imageListDelete.isEmpty()) {
                        // No items left - finish activity
                        finish()
                    } else {
                        // Update ViewPager position
                        val newPosition = if (viewpagerselectedPosition >= imageListDelete.size) {
                            // If current position is now out of bounds, go to last item
                            imageListDelete.size - 1
                        } else {
                            // Otherwise keep current position
                            viewpagerselectedPosition
                        }

                        setupViewPager(imageListDelete, newPosition)
                        viewpagerselectedPosition = newPosition

                        // Show success message
                        ("1 files restored").tos(this@DeleteViewPagerActivity)
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    progressDialog.dismiss()
                    Log.e("RestoreSelectedFiles", "Error restoring files: ${e.message}")
                    getString(R.string.failed_to_restore_selected_files).tos(this@DeleteViewPagerActivity)
                }
            }
        }
    }

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

    private fun getMimeType(file: File): String? {
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(file.extension.lowercase())
    }

    private fun applyStatusBarColor() {
        window.statusBarColor =
            resources.getColor(android.R.color.black, theme) // Set black status bar
        window.decorView.systemUiVisibility = 0 // Ensures white text/icons
        window.navigationBarColor = resources.getColor(android.R.color.black, theme)
    }

    // Function to format the date
    fun formatDate(timestamp: Long): String {
        val date = Date(timestamp)
        val format = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        return format.format(date)
    }

    fun permanentlyDeleteFile(mediaItem: DeleteMediaModel) {
        Log.d("PermanentlyDelete", "Bin Path: ${mediaItem.binPath}")
        CoroutineScope(Dispatchers.IO).launch {
            val binFile = File(mediaItem.binPath)
            if (binFile.exists()) {
                if (binFile.delete()) {
                    Log.d("PermanentlyDelete", "File deleted: ${binFile.absolutePath}")
                    deleteMediaDao!!.deleteMedia(mediaItem)  // Remove from Room database

                    runOnUiThread {
                        ("File permanently deleted").tos(this@DeleteViewPagerActivity)
                        imageListDelete.removeAt(viewpagerselectedPosition)
//                        DeleteMediaStoreSingleton.deleteimageList.removeAt(viewpagerselectedPosition)
                        if (imageListDelete.isEmpty()) {
                            finish()
                        } else setupViewPager(imageListDelete, viewpagerselectedPosition)
                    }
                } else {
                    Log.e("PermanentlyDelete", "Failed to delete file: ${binFile.absolutePath}")
                }
            } else {
                Log.e("PermanentlyDelete", "File not found: ${binFile.absolutePath}")
            }
        }
    }

    private fun setupViewPager(imageList: ArrayList<DeleteMediaModel>, currentPosition: Int) {
        try {

        if (imageList.isEmpty()) {
            finish() // Exit activity if no images left
            return
        }

        viewPagerAdapter = DeleteViewPagerAdapter(this, imageList)
        binding.viewPager.adapter = viewPagerAdapter
        binding.viewPager.setCurrentItem(currentPosition, false)

        binding.viewPager.offscreenPageLimit = 1
        // Set initial image title
//        updateImageTitle(currentPosition)

        // Change title when page is scrolled
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                ("Delete onPageSelected: $position").log()
                try {
                    if (imageList.isNotEmpty()) {
                        viewpagerselectedPosition = position
                        deleteselectedPosition = position
                        deleteMediaModel = imageList[position]
                        updateImageTitle(position)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        })

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun updateImageTitle(position: Int) {
        if (position >= imageListDelete.size) return // Prevent crash
        val fileName = File(imageListDelete[position].binPath).name
        binding.tvtitile.text = fileName
    }

}