package com.gallery.photos.editpic.Activity

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.viewpager2.widget.ViewPager2
import com.gallery.photos.editpic.Adapter.ViewPagerAdapter
import com.gallery.photos.editpic.Dialogs.AllFilesAccessDialog
import com.gallery.photos.editpic.Dialogs.DeleteWithRememberDialog
import com.gallery.photos.editpic.Dialogs.PropertiesDialog
import com.gallery.photos.editpic.Extensions.PREF_LANGUAGE_CODE
import com.gallery.photos.editpic.Extensions.handleBackPress
import com.gallery.photos.editpic.Extensions.hasAllFilesAccessAs
import com.gallery.photos.editpic.Extensions.isVideoFile
import com.gallery.photos.editpic.Extensions.log
import com.gallery.photos.editpic.Extensions.name.getMediaDatabase
import com.gallery.photos.editpic.Extensions.onClick
import com.gallery.photos.editpic.Extensions.setLanguageCode
import com.gallery.photos.editpic.Extensions.shareFile
import com.gallery.photos.editpic.Extensions.tos
import com.gallery.photos.editpic.ImageEDITModule.edit.activities.PhotoEditorActivity
import com.gallery.photos.editpic.ImageEDITModule.edit.picker.PhotoPicker
import com.gallery.photos.editpic.Model.DeleteMediaModel
import com.gallery.photos.editpic.Model.FavouriteMediaModel
import com.gallery.photos.editpic.Model.HideMediaModel
import com.gallery.photos.editpic.Model.MediaModel
import com.gallery.photos.editpic.PopupDialog.ViewPagerPopupManager
import com.gallery.photos.editpic.R
import com.gallery.photos.editpic.RoomDB.Dao.DeleteMediaDao
import com.gallery.photos.editpic.RoomDB.Dao.FavouriteMediaDao
import com.gallery.photos.editpic.RoomDB.Dao.HideMediaDao
import com.gallery.photos.editpic.Utils.MediaStoreSingleton
import com.gallery.photos.editpic.databinding.ActivityViewPagerBinding
import com.gallery.photos.editpic.myadsworld.MyAddPrefs
import com.gallery.photos.editpic.myadsworld.MyAllAdCommonClass
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.Timer
import java.util.TimerTask

class AlbumPicturesViewPagerActivity : BaseActivity() {
    private var timer: Timer? = null
    private val handler = Handler(Looper.getMainLooper())
    private var isRunning = false

    private var viewpagerselectedPosition: Int = 1
    var deleteMediaModel: DeleteMediaModel? = null
    private lateinit var binding: ActivityViewPagerBinding
    private lateinit var viewPagerAdapter: ViewPagerAdapter
    private var imageList: ArrayList<MediaModel> = arrayListOf()
    var deleteMediaDao: DeleteMediaDao? = null
    var favouriteMediaDao: FavouriteMediaDao? = null
    var hideMediaDao: HideMediaDao? = null
    var hideMediaModel: HideMediaModel? = null
    var favouriteMediaModel: FavouriteMediaModel? = null
    var isFromSlideShow: Boolean = false
    var secoundSlideShow: Int = 1


    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLanguageCode(this, MyApplicationClass.getString(PREF_LANGUAGE_CODE)!!)
        binding = ActivityViewPagerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        applyStatusBarColor()

        imageList = MediaStoreSingleton.imageList
        viewpagerselectedPosition = MediaStoreSingleton.selectedPosition
        deleteMediaModel = DeleteMediaModel()
        hideMediaModel = HideMediaModel()
        favouriteMediaModel = FavouriteMediaModel()

        isFromSlideShow = intent?.extras?.getBoolean("slideshow", false) == true
        secoundSlideShow = intent?.extras?.getInt("secoundSlideShow", 1) ?: 1

        deleteMediaDao = getMediaDatabase(this).deleteMediaDao()
        hideMediaDao = getMediaDatabase(this).hideMediaDao()
        favouriteMediaDao = getMediaDatabase(this).favouriteMediaDao()

        if (imageList.isNotEmpty() && viewpagerselectedPosition in imageList.indices) {
            val media = imageList[viewpagerselectedPosition]
            media.apply {
                deleteMediaModel!!.mediaId = mediaId
                deleteMediaModel!!.mediaName = mediaName
                deleteMediaModel!!.mediaPath = mediaPath
                deleteMediaModel!!.mediaMimeType = mediaMimeType
                deleteMediaModel!!.mediaDateAdded = mediaDateAdded
                deleteMediaModel!!.isVideo = isVideo
                deleteMediaModel!!.displayDate = displayDate
                deleteMediaModel!!.isSelect = isSelect

                favouriteMediaModel!!.mediaId = mediaId
                favouriteMediaModel!!.mediaName = mediaName
                favouriteMediaModel!!.mediaPath = mediaPath
                favouriteMediaModel!!.mediaMimeType = mediaMimeType
                favouriteMediaModel!!.mediaDateAdded = mediaDateAdded
                favouriteMediaModel!!.isVideo = isVideo
                favouriteMediaModel!!.displayDate = displayDate
                favouriteMediaModel!!.isSelect = isSelect
                favouriteMediaModel!!.isFav = isFav
            }
        } else {
            Log.e(
                "ViewPagerActivity",
                "imageList is empty or invalid position: $viewpagerselectedPosition"
            )
            Toast.makeText(this, "No images to display", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setupViewPager(imageList, viewpagerselectedPosition)

        binding.ivBack.setOnClickListener {
            if (isFromSlideShow) {
                sliderstop()
                finish()
            } else {
                finish()
            }
        }

        hideBottomNavigationBar(R.color.black)


        MyAllAdCommonClass.showAdmobBanner(
            this@AlbumPicturesViewPagerActivity,
            binding.bannerContainer,
            binding.shimmerContainerBanner,
            false,
            MyAddPrefs(this@AlbumPicturesViewPagerActivity).admBannerId
        )



        binding.apply {
            binding.bottomActions.bottomShare.onClick {
                shareFile(
                    this@AlbumPicturesViewPagerActivity,
                    imageList[viewpagerselectedPosition].mediaPath
                )
            }
            binding.bottomActions.bottomFavorite.setOnClickListener {
                val position = viewpagerselectedPosition
                val currentMedia = imageList[position]
                currentMedia.isFav = !currentMedia.isFav  // Toggle the favorite status

                CoroutineScope(Dispatchers.IO).launch {
                    val isFav = favouriteMediaDao?.isMediaFavorite(currentMedia.mediaId) ?: false

                    if (isFav) {
                        // If it is already a favorite, remove it from the database
                        favouriteMediaDao?.getMediaById(currentMedia.mediaId)
                            ?.let { favouriteMediaDao?.deleteMedia(it) }
                    } else {
                        // If it's not a favorite, add it to the database
                        favouriteMediaDao?.insertMedia(
                            FavouriteMediaModel(
                                mediaId = currentMedia.mediaId,
                                mediaName = currentMedia.mediaName,
                                mediaPath = currentMedia.mediaPath,
                                mediaMimeType = currentMedia.mediaMimeType,
                                mediaSize = currentMedia.mediaSize,
                                mediaDateAdded = currentMedia.mediaDateAdded,
                                isVideo = currentMedia.isVideo,
                                displayDate = currentMedia.displayDate,
                                isFav = true
                            )
                        )
                    }

                    runOnUiThread {
                        updateImageTitle(position)  // Update the icon and UI
                    }
                }
            }

            bottomActions.bottomProperties.onClick {
                PropertiesDialog(
                    this@AlbumPicturesViewPagerActivity, imageList[viewpagerselectedPosition]
                ) {}
            }

            ivOrientation.onClick {

            }

            ivMore.onClick {
                val topcustomtopcustompopup =
                    ViewPagerPopupManager(this@AlbumPicturesViewPagerActivity) {
                        when (it) {
                            "tvSlideShow" -> {

                            }

                            "hiddentoid" -> {

                                if (!hasAllFilesAccessAs(this@AlbumPicturesViewPagerActivity)) {
                                    (getString(R.string.all_files_access_required)).tos(this@AlbumPicturesViewPagerActivity)
                                    AllFilesAccessDialog(this@AlbumPicturesViewPagerActivity) {

                                    }
//                    startActivityWithBundle<AllFilePermissionActivity>(Bundle().apply {
//                        putString("isFrom", "Activitys")
//                    })
                                    return@ViewPagerPopupManager
                                }

                                renameFileToHide(
                                    this@AlbumPicturesViewPagerActivity,
                                    deleteMediaModel!!.mediaPath
                                )
                                imageList.removeAt(viewpagerselectedPosition)
                                binding.viewPager.currentItem = viewpagerselectedPosition

                                if (imageList.isEmpty()) {
                                    finish()
                                } else setupViewPager(imageList, viewpagerselectedPosition)
                            }
                        }
                    }
                topcustomtopcustompopup.show(ivMore, 0, 0)
            }

// Update your bottom delete onClick listener
            bottomActions.bottomDelete.onClick {
                if (!hasAllFilesAccessAs(this@AlbumPicturesViewPagerActivity)) {
                    (getString(R.string.all_files_access_required)).tos(this@AlbumPicturesViewPagerActivity)
                    AllFilesAccessDialog(this@AlbumPicturesViewPagerActivity) {}
                    return@onClick
                }

                DeleteWithRememberDialog(this@AlbumPicturesViewPagerActivity) {
                    run {
                        val isMoved = moveToRecycleBin(deleteMediaModel!!.mediaPath)
                        if (isMoved) {
                            deleteMediaModel!!.binPath = File(
                                createRecycleBin(), deleteMediaModel!!.mediaName
                            ).absolutePath

                            CoroutineScope(Dispatchers.IO).launch {
                                deleteMediaDao!!.insertMedia(deleteMediaModel!!)  // Insert into Room DB
                            }
                            viewpagerselectedPosition = binding.viewPager.currentItem

                            runOnUiThread {
                                if (imageList.size + 1 >= viewpagerselectedPosition) {
                                    imageList.removeAt(viewpagerselectedPosition)
                                    viewpagerselectedPosition = binding.viewPager.currentItem

                                    viewPagerAdapter.notifyDataSetChanged()

                                    if (imageList.isNotEmpty()) {
//                                (imageList.toList())
                                        binding.viewPager.setCurrentItem(
                                            viewpagerselectedPosition, false
                                        )
                                        updateImageTitle(viewpagerselectedPosition)
                                    } else {
                                        finish() // Close activity if no more images
                                    }
                                }
                            }
                        } else {
                            Timber.tag("FileDeletion")
                                .e("Failed to move file: ${deleteMediaModel!!.mediaPath}")
                        }
                    }
                }
            }

            bottomActions.bottomEdit.onClick {
                val intent2 =
                    Intent(applicationContext, PhotoEditorActivity::class.java as Class<*>)
                intent2.putExtra(
                    PhotoPicker.KEY_SELECTED_PHOTOS, imageList[viewpagerselectedPosition].mediaPath
                )
                startActivity(intent2)
//                val intent = Intent(this@ViewPagerActivity, EditImageActivity::class.java)
//                intent.putExtra("IMAGE_PATH", imageList[viewpagerselectedPosition].mediaPath)
//                startActivity(intent)
            }
        }

        //todo: onBackPress
        handleBackPress {
            if (isFromSlideShow) {
                sliderstop()
                finish()
            } else {
                finish()
            }
        }


        if (isFromSlideShow) {
            isOneTimeVisibleTools = !isOneTimeVisibleTools
            binding.bottomActions.root.visibility = View.INVISIBLE
            binding.rltop.visibility = View.INVISIBLE
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN // Hide status bar
            ("Slide start $secoundSlideShow sec")
            startTimerTask()
        }

    }

    private fun startTimerTask() {
        timer = Timer()
        timer?.schedule(object : TimerTask() {
            override fun run() {
                handler.post {
                    val position = binding.viewPager.currentItem
                    if (position < imageList.size - 1) {
                        binding.viewPager.currentItem = (position + 1)
                        updateImageTitle(position + 1)
                    } else {
                        ("Slide end").tos(this@AlbumPicturesViewPagerActivity)
                        sliderstop()
                    }
//                    isRunning = !isRunning
                }
            }
        }, 0, secoundSlideShow * 1000L) // 5 seconds interval
    }


    private fun sliderstop() {
        println("Stop method called")
        timer?.cancel()
        // Your stop logic
    }

    override fun onDestroy() {
        super.onDestroy()
        timer?.cancel() // Stop the timer when activity is destroyed
    }

    private fun applyStatusBarColor() {
        window.statusBarColor =
            resources.getColor(android.R.color.black, theme) // Set black status bar
        window.decorView.systemUiVisibility = 0 // Ensures white text/icons
        window.navigationBarColor = resources.getColor(android.R.color.black, theme)
    }

    fun renameFileToHide(context: Context, originalFilePath: String): Boolean {
        val originalFile = File(originalFilePath)
        if (!originalFile.exists()) {
            Log.e("RenameFile", "File not found: $originalFilePath")
            return false
        }

        return if (Build.VERSION.SDK_INT == Build.VERSION_CODES.R || Build.VERSION.SDK_INT == Build.VERSION_CODES.S) {
            val parentDir = originalFile.parentFile
            val newFileName =
                if (!originalFile.name.startsWith(".")) ".${originalFile.name}" else originalFile.name
            val hiddenFile = File(parentDir, newFileName)

            CoroutineScope(Dispatchers.IO).launch {
                favouriteMediaDao!!.getMediaById(hideMediaModel!!.mediaId)
                    ?.let { favouriteMediaDao!!.deleteMedia(it) }
                hideMediaModel?.let { hideMediaDao!!.insertMedia(it) }
            }

            renameFileUsingMediaStore(context, originalFilePath, newFileName)
        } else {
            renameAndHidePhoto(originalFilePath)/*    if (originalFile.renameTo(hiddenFile)) {
                    Log.d("RenameFile", "File renamed: ${hiddenFile.absolutePath}")
                    notifySystemGallery(context, hiddenFile.absolutePath)
                    true
                } else {
                    Log.e("RenameFile", "Failed to rename file.")
                    false
                }*/
        }
    }

    fun renameAndHidePhoto(originalFilePath: String): Boolean {
        val originalFile = File(originalFilePath)
        if (!originalFile.exists()) {
            Log.e("RenameAndHide", "File not found: $originalFilePath")
            return false
        }

        val parentDir = originalFile.parentFile
        val newFileName = ".${originalFile.name}"  // Prefix the filename with a dot
        val hiddenFile = File(parentDir, newFileName)
        if (imageList.isNotEmpty() && viewpagerselectedPosition in imageList.indices) {
            imageList[viewpagerselectedPosition].mediaName = newFileName
//        updateImageTitle(viewpagerselectedPosition)
            binding.tvtitile.text = newFileName

            imageList[viewpagerselectedPosition].apply {
                hideMediaModel!!.mediaId = mediaId
                hideMediaModel!!.mediaName = newFileName
                hideMediaModel!!.mediaPath = hiddenFile.path
                hideMediaModel!!.mediaMimeType = mediaMimeType
                hideMediaModel!!.mediaDateAdded = mediaDateAdded
                hideMediaModel!!.isVideo = isVideo
                hideMediaModel!!.displayDate = displayDate
                hideMediaModel!!.isSelect = isSelect
            }
        }

        CoroutineScope(Dispatchers.IO).launch {
            favouriteMediaDao!!.getMediaById(hideMediaModel!!.mediaId)
                ?.let { favouriteMediaDao!!.deleteMedia(it) }
            hideMediaModel?.let { hideMediaDao!!.insertMedia(it) }
        }

        return if (originalFile.renameTo(hiddenFile)) {
            Log.d("RenameAndHide", "File renamed and hidden: ${hiddenFile.absolutePath}")
            notifySystemGallery(
                this,
                hiddenFile.absolutePath
            )  // Notify the system to refresh the media store
            true
        } else {
            Log.e("RenameAndHide", "Failed to rename and hide the file.")
            false
        }
    }


    /**
     * Rename file using MediaStore API (For Android 10+)
     */
    private fun renameFileUsingMediaStore(
        context: Context, originalFilePath: String, newFileName: String
    ): Boolean {
        val contentResolver = context.contentResolver
        val collectionUri =
            if (originalFilePath.contains("/DCIM") || originalFilePath.contains("/Pictures")) {
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            } else {
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            }

        val projection = arrayOf(MediaStore.MediaColumns._ID)
        val selection = "${MediaStore.MediaColumns.DATA} = ?"
        val selectionArgs = arrayOf(originalFilePath)

        contentResolver.query(collectionUri, projection, selection, selectionArgs, null)
            ?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val idColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID)
                    val id = cursor.getLong(idColumn)
                    val contentUri = ContentUris.withAppendedId(collectionUri, id)

                    // Rename file (without moving)
                    val values = ContentValues().apply {
                        put(MediaStore.MediaColumns.DISPLAY_NAME, newFileName)
                    }

                    val updatedRows = contentResolver.update(contentUri, values, null, null)
                    return if (updatedRows > 0) {
                        Log.d("RenameFile", "File renamed using MediaStore: $newFileName")
                        true
                    } else {
                        Log.e("RenameFile", "Failed to rename file using MediaStore.")
                        false
                    }
                }
            }
        return false
    }

    /**
     * Notify the system gallery to refresh media store.
     */
    fun notifySystemGallery(context: Context, filePath: String) {
        val file = File(filePath)
        val uri = Uri.fromFile(file)
        val intent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE).apply {
            data = uri
        }
        context.sendBroadcast(intent)
        Log.d("NotifyGallery", "Broadcast sent for $filePath")
    }

    fun createRecycleBin(): File {
        val recycleBin = File(getExternalFilesDir(null), ".gallery_recycleBin")
        if (!recycleBin.exists()) {
            if (recycleBin.mkdirs()) {
                Log.d("RecycleBin", "Recycle bin created at: ${recycleBin.absolutePath}")
            } else {
                Log.e("RecycleBin", "Failed to create recycle bin at: ${recycleBin.absolutePath}")
            }
        } else {
            Log.d("RecycleBin", "Recycle bin already exists at: ${recycleBin.absolutePath}")
        }
        return recycleBin
    }

    fun moveToRecycleBin(originalFilePath: String): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {  // Android 11+ (API 30+)
            moveToRecycleBinScopedStorage(originalFilePath)
        } else {
            moveToRecycleBinLegacy(originalFilePath, this)
        }
    }

    // ✅ Android 11+ (Scoped Storage) - Uses ContentResolver
    fun moveToRecycleBinScopedStorage(originalFilePath: String): Boolean {
        // First get the MediaStore URI for the file
        val uri = getMediaStoreUriFromPath(originalFilePath) ?: run {
            Log.e("MoveToRecycleBin", "Could not get MediaStore URI for path: $originalFilePath")
            return false
        }

        val contentResolver = contentResolver
        val inputStream = try {
            contentResolver.openInputStream(uri)
        } catch (e: Exception) {
            Log.e("MoveToRecycleBin", "Failed to open InputStream: ${e.message}")
            return false
        }

        val recycleBin = createRecycleBin()
        val recycledFile = File(
            recycleBin, getFileNameFromUri(uri) ?: "deleted_file_${System.currentTimeMillis()}"
        )

        return try {
            // Copy file to recycle bin
            recycledFile.outputStream().use { output ->
                inputStream?.copyTo(output)
            }
            inputStream?.close()

            Log.d("MoveToRecycleBin", "File copied to recycle bin: ${recycledFile.absolutePath}")

            // Delete from MediaStore
            val deleteCount = contentResolver.delete(uri, null, null)
            if (deleteCount > 0) {
                Log.d("MoveToRecycleBin", "Original file deleted successfully")
            } else {
                Log.e("MoveToRecycleBin", "Failed to delete original file from MediaStore")
                // If delete failed, delete the copy we made
                recycledFile.delete()
                return false
            }

            // Insert into Room Database
            CoroutineScope(Dispatchers.IO).launch {
                deleteMediaModel?.let { model ->
                    model.binPath = recycledFile.absolutePath
                    deleteMediaDao?.insertMedia(model)
                    Log.d(
                        "MoveToRecycleBin",
                        "Inserted into Room database: ${recycledFile.absolutePath}"
                    )
                }
            }

            true
        } catch (e: IOException) {
            Log.e("MoveToRecycleBin", "IOException: ${e.message}")
            e.printStackTrace()
            // Clean up if something went wrong
            recycledFile.delete()
            false
        }
    }

    private fun getMediaStoreUriFromPath(filePath: String): Uri? {
        val projection = arrayOf(MediaStore.Images.Media._ID)
        val selection = "${MediaStore.Images.Media.DATA} = ?"
        val selectionArgs = arrayOf(filePath)

        contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, selection, selectionArgs, null
        )?.use { cursor ->
            if (cursor.moveToFirst()) {
                val id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID))
                return ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
            }
        }
        return null
    }
    // ✅ Android 10 and Below - Uses Direct File Access
    fun moveToRecycleBinLegacy(originalFilePath: String, context: Context): Boolean {
        val contentResolver = context.contentResolver

        // Determine if the input is a content URI or file path
        val isContentUri = originalFilePath.startsWith("content://")
        val sourceUri = if (isContentUri) Uri.parse(originalFilePath) else null
        val sourceFile = if (!isContentUri) File(originalFilePath) else null

        // Check if the source exists
        if (isContentUri && sourceUri != null) {
            // Verify content URI exists by querying ContentResolver
            contentResolver.query(sourceUri, null, null, null, null)?.use { cursor ->
                if (!cursor.moveToFirst()) {
                    Log.e("MoveToRecycleBin", "Content URI does not exist: $originalFilePath")
                    return false
                }
            } ?: run {
                Log.e("MoveToRecycleBin", "Failed to query content URI: $originalFilePath")
                return false
            }
        } else if (sourceFile != null && !sourceFile.exists()) {
            Log.e("MoveToRecycleBin", "File does not exist: $originalFilePath")
            return false
        }

        // Prepare recycle bin directory in app-specific storage
        val recycleBin = File(context.getExternalFilesDir(null), ".gallery_recycleBin").apply {
            if (!exists()) {
                if (!mkdirs()) {
                    Log.e("MoveToRecycleBin", "Failed to create recycle bin directory")
                    return false
                }
            }
        }

        // Get file name and MIME type
        val (fileName, mimeType) = if (isContentUri && sourceUri != null) {
            getFileInfoFromUri(context, sourceUri)
        } else {
            Pair(
                sourceFile!!.name,
                context.contentResolver.getType(Uri.fromFile(sourceFile)) ?: "*/*"
            )
        }

        val recycledFile = File(recycleBin, fileName)

        return try {
            // Copy the file to recycle bin
            val copySuccess = if (isContentUri && sourceUri != null) {
                contentResolver.openInputStream(sourceUri)?.use { inputStream ->
                    FileOutputStream(recycledFile).use { outputStream ->
                        inputStream.copyTo(outputStream)
                        true
                    }
                } ?: false.also {
                    Log.e("MoveToRecycleBin", "Failed to open input stream for $sourceUri")
                }
            } else {
                sourceFile!!.copyTo(recycledFile, overwrite = true)
                true
            }

            if (!copySuccess) {
                Log.e("MoveToRecycleBin", "Failed to copy file to recycle bin: $originalFilePath")
                return false
            }

            Log.d("MoveToRecycleBin", "File copied to recycle bin: ${recycledFile.absolutePath}")

            // Delete the original file
            val deleteSuccess = if (isContentUri && sourceUri != null) {
                contentResolver.delete(sourceUri, null, null) > 0
            } else {
                sourceFile!!.delete()
            }

            if (deleteSuccess) {
                Log.d("MoveToRecycleBin", "Original file deleted: $originalFilePath")
                // Notify MediaScanner about the new file in recycle bin
                MediaScannerConnection.scanFile(
                    context,
                    arrayOf(recycledFile.absolutePath),
                    arrayOf(mimeType),
                    null
                )
                true
            } else {
                Log.e("MoveToRecycleBin", "Failed to delete original: $originalFilePath")
                recycledFile.delete() // Clean up if deletion fails
                false
            }
        } catch (e: IOException) {
            Log.e("MoveToRecycleBin", "IOException during file operation: ${e.message}", e)
            recycledFile.delete() // Clean up on failure
            false
        } catch (e: SecurityException) {
            Log.e("MoveToRecycleBin", "SecurityException: ${e.message}", e)
            recycledFile.delete() // Clean up on failure
            false
        }
    }

    private fun getFileInfoFromUri(context: Context, uri: Uri): Pair<String, String> {
        var fileName = "unknown_file"
        var mimeType = "*/*"
        context.contentResolver.query(
            uri,
            arrayOf(MediaStore.MediaColumns.DISPLAY_NAME, MediaStore.MediaColumns.MIME_TYPE),
            null,
            null,
            null
        )?.use { cursor ->
            if (cursor.moveToFirst()) {
                val nameIndex = cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME)
                val mimeIndex = cursor.getColumnIndex(MediaStore.MediaColumns.MIME_TYPE)
                if (nameIndex >= 0) fileName = cursor.getString(nameIndex)
                if (mimeIndex >= 0) mimeType = cursor.getString(mimeIndex) ?: "*/*"
            }
        }
        return Pair(fileName, mimeType)
    }

    // ✅ Helper Function: Save to Room Database
    fun saveToDatabase(binPath: String) {
        CoroutineScope(Dispatchers.IO).launch {
            deleteMediaModel!!.binPath = binPath
            deleteMediaModel!!.mediaDateAdded = imageList[viewpagerselectedPosition].mediaDateAdded

            deleteMediaDao!!.insertMedia(deleteMediaModel!!)  // Save path for restoration

            runOnUiThread {
                imageList.removeAt(viewpagerselectedPosition)
                if (imageList.isEmpty()) {
                    finish()
                } else setupViewPager(imageList, viewpagerselectedPosition)
            }
            Log.d("MoveToRecycleBin", "Media record inserted into Room database.")
        }
    }

    // ✅ Helper Function: Get File Name from URI
    fun getFileNameFromUri(uri: Uri): String? {
        var name: String? = null
        val cursor = contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex != -1) {
                    name = it.getString(nameIndex)
                }
            }
        }
        return name
    }

    var isOneTimeVisibleTools = false

    private fun setupViewPager(imageList: List<MediaModel>, currentPosition: Int) {
        if (imageList.isEmpty()) {
            binding.tvtitile.text = "No images to display"
            binding.viewPager.visibility = View.GONE
            return
        }
        viewPagerAdapter = ViewPagerAdapter(this, imageList) {
            ("onClick").log()
            isOneTimeVisibleTools = !isOneTimeVisibleTools
            if (isOneTimeVisibleTools) {
                binding.bottomActions.root.visibility = View.INVISIBLE
                binding.rltop.visibility = View.INVISIBLE
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
                hideNavigationBar(this)
            } else {
                if (isFromSlideShow) {
                    sliderstop()
                }
                binding.bottomActions.root.visibility = View.VISIBLE
                binding.rltop.visibility = View.VISIBLE
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
                hideNavigationBar(this)
            }
        }
        binding.viewPager.adapter = viewPagerAdapter
        binding.viewPager.setCurrentItem(currentPosition, false)
        binding.viewPager.offscreenPageLimit = 1
        updateImageTitle(currentPosition)

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                try {


                    if (imageList.isNotEmpty()) {
                        viewpagerselectedPosition = position
                        imageList[position].apply {
                            deleteMediaModel!!.mediaId = mediaId
                            deleteMediaModel!!.mediaName = mediaName
                            deleteMediaModel!!.mediaPath = mediaPath
                            deleteMediaModel!!.mediaMimeType = mediaMimeType
                            deleteMediaModel!!.mediaDateAdded = mediaDateAdded
                            deleteMediaModel!!.isVideo = isVideo
                            deleteMediaModel!!.displayDate = displayDate
                            deleteMediaModel!!.isSelect = isSelect

                            favouriteMediaModel!!.mediaId = mediaId
                            favouriteMediaModel!!.mediaName = mediaName
                            favouriteMediaModel!!.mediaPath = mediaPath
                            favouriteMediaModel!!.mediaMimeType = mediaMimeType
                            favouriteMediaModel!!.mediaDateAdded = mediaDateAdded
                            favouriteMediaModel!!.isVideo = isVideo
                            favouriteMediaModel!!.displayDate = displayDate
                            favouriteMediaModel!!.isSelect = isSelect
                            favouriteMediaModel!!.isFav = isFav
                        }
                        updateImageTitle(position)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        })

    }

    private fun updateImageTitle(position: Int) {
        if (position in imageList.indices) {
            val fileName = imageList[position]
            binding.tvtitile.text = fileName.mediaName
            binding.bottomActions.bottomEdit.visibility =
                if (isVideoFile(imageList[position].mediaPath)) View.GONE else View.VISIBLE
            CoroutineScope(Dispatchers.IO).launch {
                favouriteMediaDao?.let { dao ->
                    val isFav = dao.isMediaFavorite(fileName.mediaId)
                    runOnUiThread {
                        binding.bottomActions.bottomFavorite.setImageResource(if (isFav) R.drawable.fillfavourite else R.drawable.unfillfavourite)
                    }
                }
            }
        } else {
            Log.e(
                "ViewPagerActivity",
                "Invalid position: $position for imageList size ${imageList.size}"
            )
            binding.tvtitile.text = "No image"
        }
    }
}