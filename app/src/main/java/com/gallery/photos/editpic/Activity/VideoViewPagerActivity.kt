package com.gallery.photos.editpic.Activity

import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import android.view.ViewGroup
import android.widget.Toast
import android.widget.VideoView
import androidx.core.net.toUri
import com.gallery.photos.editpic.Adapter.VideoDisplayAdapter
import com.gallery.photos.editpic.Adapter.VideoPreviousNext
import com.gallery.photos.editpic.Dialogs.AllFilesAccessDialog
import com.gallery.photos.editpic.Dialogs.DeleteWithRememberDialog
import com.gallery.photos.editpic.Dialogs.PropertiesDialog
import com.gallery.photos.editpic.Extensions.PREF_LANGUAGE_CODE
import com.gallery.photos.editpic.Extensions.formatDate
import com.gallery.photos.editpic.Extensions.hasAllFilesAccessAs
import com.gallery.photos.editpic.Extensions.log
import com.gallery.photos.editpic.Extensions.name.getMediaDatabase
import com.gallery.photos.editpic.Extensions.onClick
import com.gallery.photos.editpic.Extensions.setLanguageCode
import com.gallery.photos.editpic.Extensions.shareFile
import com.gallery.photos.editpic.Extensions.tos
import com.gallery.photos.editpic.Model.DeleteMediaModel
import com.gallery.photos.editpic.Model.FavouriteMediaModel
import com.gallery.photos.editpic.Model.HideMediaModel
import com.gallery.photos.editpic.Model.MediaModel
import com.gallery.photos.editpic.Model.VideoModel
import com.gallery.photos.editpic.PopupDialog.ViewPagerPopupManager
import com.gallery.photos.editpic.R
import com.gallery.photos.editpic.RoomDB.Dao.DeleteMediaDao
import com.gallery.photos.editpic.RoomDB.Dao.FavouriteMediaDao
import com.gallery.photos.editpic.RoomDB.Dao.HideMediaDao
import com.gallery.photos.editpic.Utils.DeleteMediaStoreSingleton.deleteselectedPosition
import com.gallery.photos.editpic.Utils.VideoMediaStoreSingleton
import com.gallery.photos.editpic.Utils.VideoMediaStoreSingleton.videoimageList
import com.gallery.photos.editpic.Views.CustomViewPager
import com.gallery.photos.editpic.databinding.ActivityVideoviewPagerBinding
import com.gallery.photos.editpic.myadsworld.MyAddPrefs
import com.gallery.photos.editpic.myadsworld.MyAllAdCommonClass
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class VideoViewPagerActivity : BaseActivity() {

    private var viewpagerselectedPosition: Int = 1
    var videoMediaModel: VideoModel? = null
    private lateinit var binding: ActivityVideoviewPagerBinding
    private var imageListVideo: ArrayList<VideoModel> = arrayListOf()
    var deleteMediaDao: DeleteMediaDao? = null
    var deleteMediaModel: DeleteMediaModel? = null
    var hideMediaModel: HideMediaModel? = null
    var hideMediaDao: HideMediaDao? = null
    var favouriteMediaDao: FavouriteMediaDao? = null
    private lateinit var videoDisplayAdapter: VideoDisplayAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLanguageCode(this, MyApplicationClass.getString(PREF_LANGUAGE_CODE)!!)
        binding = ActivityVideoviewPagerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        applyStatusBarColor()
        hideMediaDao = getMediaDatabase(this).hideMediaDao()
        deleteMediaDao = getMediaDatabase(this).deleteMediaDao()
        favouriteMediaDao = getMediaDatabase(this).favouriteMediaDao()
        imageListVideo = VideoMediaStoreSingleton.videoimageList
        viewpagerselectedPosition = VideoMediaStoreSingleton.videoselectedPosition
        videoMediaModel = VideoModel()
        deleteMediaModel = DeleteMediaModel()
        hideMediaModel = HideMediaModel()


        hideBottomNavigationBar(R.color.black)


        MyAllAdCommonClass.showAdmobBanner(
            this@VideoViewPagerActivity,
            binding.bannerContainer,
            binding.shimmerContainerBanner,
            false,
            MyAddPrefs(this@VideoViewPagerActivity).admBannerId
        )

        try {

        imageListVideo[viewpagerselectedPosition].apply {
            deleteMediaModel!!.mediaId = videoId
            deleteMediaModel!!.mediaName = videoName
            deleteMediaModel!!.mediaPath = videoPath
            deleteMediaModel!!.mediaMimeType = "mp4"
            deleteMediaModel!!.mediaDateAdded = 0
            deleteMediaModel!!.isVideo = true
            deleteMediaModel!!.displayDate = ""
            deleteMediaModel!!.isSelect = isSelect
        }
            updateImageTitle(viewpagerselectedPosition)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        ("Delete onPageSelected: $viewpagerselectedPosition").log()

        setadpter(imageListVideo, viewpagerselectedPosition)

        binding.ivBack.setOnClickListener {
            finish()
        }

        binding.apply {

            bottomActions.bottomProperties.onClick {
                try {
                PropertiesDialog(
                    this@VideoViewPagerActivity, MediaModel(
                        mediaId = imageListVideo[viewpagerselectedPosition]!!.videoId,
                        mediaName = imageListVideo[viewpagerselectedPosition]!!.videoName,
                        mediaPath = imageListVideo[viewpagerselectedPosition]!!.videoPath,
                        mediaMimeType = "mp4",
                        mediaDateAdded = imageListVideo[viewpagerselectedPosition]!!.videoDateAdded,
                        isVideo = true,
                        displayDate = formatDate(imageListVideo[viewpagerselectedPosition]!!.videoDateAdded),
                        isSelect = false,
                        isFav = false
                    )
                ) {}
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            binding.bottomActions.bottomFavorite.setOnClickListener {
                val position = viewpagerselectedPosition
                val currentMedia = videoimageList[position]
                currentMedia.isFav = !currentMedia.isFav  // Toggle the favorite status

                CoroutineScope(Dispatchers.IO).launch {
                    val isFav = favouriteMediaDao?.isMediaFavorite(currentMedia.videoId) ?: false

                    if (isFav) {
                        // If it is already a favorite, remove it from the database
                        favouriteMediaDao?.getMediaById(currentMedia.videoId)
                            ?.let { favouriteMediaDao?.deleteMedia(it) }
                    } else {
                        // If it's not a favorite, add it to the database
                        favouriteMediaDao?.insertMedia(
                            FavouriteMediaModel(
                                mediaId = currentMedia.videoId,
                                mediaName = currentMedia.videoName,
                                mediaPath = currentMedia.videoPath,
                                mediaMimeType = currentMedia.videoPath,
                                mediaSize = currentMedia.videoSize,
                                mediaDateAdded = currentMedia.videoDateAdded,
                                isVideo = true,
                                displayDate = formatDate(currentMedia.videoDateAdded),
                                isFav = true
                            )
                        )
                    }

                    runOnUiThread {
                        updateImageTitle(position)  // Update the icon and UI
                    }
                }
            }

            bottomActions.bottomShare.onClick {
                shareFile(
                    this@VideoViewPagerActivity, imageListVideo[viewpagerselectedPosition].videoPath
                )
            }

            ivMore.onClick {
                val topcustomtopcustompopup = ViewPagerPopupManager(this@VideoViewPagerActivity) {
                    when (it) {
                        "hiddentoid" -> {
                            if (!hasAllFilesAccessAs(this@VideoViewPagerActivity)) {
                                (getString(R.string.all_files_access_required)).tos(this@VideoViewPagerActivity)
                                AllFilesAccessDialog(this@VideoViewPagerActivity){

                                }
//                                startActivityWithBundle<AllFilePermissionActivity>(Bundle().apply {
//                                    putString("isFrom", "Activitys")
//                                })
                                return@ViewPagerPopupManager
                            }

                            renameAndHidePhoto(deleteMediaModel!!.mediaPath)
                            videoimageList.removeAt(viewpagerselectedPosition)
                            binding.viewPager.currentItem = viewpagerselectedPosition
                            if (videoimageList.isEmpty()) finish()
                            else setadpter(videoimageList, viewpagerselectedPosition)
                        }
                    }
                }
                topcustomtopcustompopup.show(ivMore, 0, 0)
            }
            bottomActions.bottomDelete.onClick {
                if (!hasAllFilesAccessAs(this@VideoViewPagerActivity)) {
                    (getString(R.string.all_files_access_required)).tos(this@VideoViewPagerActivity)
                    AllFilesAccessDialog(this@VideoViewPagerActivity){

                    }
//                    startActivityWithBundle<AllFilePermissionActivity>(Bundle().apply {
//                        putString("isFrom", "Activitys")
//                    })
                    return@onClick
                }

                DeleteWithRememberDialog(this@VideoViewPagerActivity) {
                    val currentMedia = imageListVideo[viewpagerselectedPosition]

                    // Delete from favorites in background
                    CoroutineScope(Dispatchers.IO).launch {
                        favouriteMediaDao?.getMediaById(currentMedia.videoId)?.let {
                            favouriteMediaDao?.deleteMedia(it)
                        }
                    }

                    // Move to recycle bin
                    val isMoved = moveToRecycleBin(deleteMediaModel!!.mediaPath)
                    if (isMoved) {
                        deleteMediaModel!!.binPath = File(
                            createRecycleBin(), deleteMediaModel!!.mediaName
                        ).absolutePath

                        // Insert into Room DB
                        CoroutineScope(Dispatchers.IO).launch {
                            deleteMediaDao!!.insertMedia(deleteMediaModel!!)
                        }

                        // Remove from current list
                        imageListVideo.removeAt(viewpagerselectedPosition)

                        runOnUiThread {
                            videoDisplayAdapter.notifyDataSetChanged()

                            if (imageListVideo.isNotEmpty()) {
                                // Calculate new position - if we deleted the last item, go to previous
                                val newPosition = if (viewpagerselectedPosition >= imageListVideo.size) {
                                    imageListVideo.size - 1
                                } else {
                                    viewpagerselectedPosition
                                }

                                // Update ViewPager position
                                binding.viewPager.setCurrentItem(newPosition, false)
                                updateImageTitle(newPosition)
                            } else {
                                finish() // Close activity if no more items
                            }
                        }
                    } else {
                        Timber.tag("FileDeletion").e("Failed to move file: ${deleteMediaModel!!.mediaPath}")
                        runOnUiThread {
                            Toast.makeText(this@VideoViewPagerActivity, "Failed to delete file", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
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

        imageListVideo[viewpagerselectedPosition].videoName = newFileName
//        updateImageTitle(viewpagerselectedPosition)
        binding.tvtitile.text = newFileName

        imageListVideo[viewpagerselectedPosition].apply {
            hideMediaModel!!.mediaId = videoId
            hideMediaModel!!.mediaName = newFileName
            hideMediaModel!!.mediaPath = hiddenFile.path
            hideMediaModel!!.mediaMimeType = "mp4"
            hideMediaModel!!.mediaDateAdded = videoDateAdded
            hideMediaModel!!.isVideo = true
            hideMediaModel!!.displayDate = formatDate(videoDateAdded)
            hideMediaModel!!.isSelect = isSelect
        }


        CoroutineScope(Dispatchers.IO).launch {
            favouriteMediaDao!!.getMediaById(hideMediaModel!!.mediaId)
                ?.let { favouriteMediaDao!!.deleteMedia(it) }
            hideMediaModel?.let { hideMediaDao!!.insertMedia(it) }
        }

        return if (originalFile.renameTo(hiddenFile)) {
            Log.d("RenameAndHide", "File renamed and hidden: ${hiddenFile.absolutePath}")
            notifySystemGallery(hiddenFile.absolutePath)  // Notify the system to refresh the media store
            true
        } else {
            Log.e("RenameAndHide", "Failed to rename and hide the file.")
            false
        }
    }

    fun notifySystemGallery(filePath: String) {
        val file = File(filePath)
        val uri = Uri.fromFile(file)
        val intent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE).apply {
            data = uri
        }
        sendBroadcast(intent)
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

    fun moveToRecycleBinScopedStorage(originalFilePath: String): Boolean {
        val context = applicationContext
        val contentResolver = context.contentResolver

        // 1. Get proper URI for the source file
        val sourceUri = when {
            originalFilePath.startsWith("content://") -> Uri.parse(originalFilePath)
            else -> getMediaStoreUriFromPath(originalFilePath) ?: run {
                Log.e("MoveToRecycleBin", "Cannot find MediaStore URI for path: $originalFilePath")
                return false
            }
        }

        // 2. Get file information with proper extension handling
        val (fileName, mimeType) = getFileInfo(context, sourceUri, originalFilePath)

        // 3. Prepare recycle bin directory
        val recycleBin = File(context.getExternalFilesDir(null), ".gallery_recycleBin").apply {
            if (!exists()) mkdirs()
        }
        val destinationFile = File(recycleBin, fileName)

        return try {
            // 4. Copy the file
            contentResolver.openInputStream(sourceUri)?.use { inputStream ->
                FileOutputStream(destinationFile).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            } ?: return false.also {
                Log.e("MoveToRecycleBin", "Failed to open input stream")
            }

            // 5. Delete original only if copy succeeded
            if (destinationFile.exists()) {
                val deleteSuccess = when {
                    sourceUri.scheme == "content" -> contentResolver.delete(
                        sourceUri,
                        null,
                        null
                    ) > 0

                    sourceUri.scheme == "file" -> File(sourceUri.path!!).delete()
                    else -> false
                }

                if (deleteSuccess) {
                    // Notify adapter about item removal
                    // Notify MediaStore about changes
                    MediaScannerConnection.scanFile(
                        context,
                        arrayOf(destinationFile.absolutePath),
                        arrayOf(mimeType), null
                    )
                } else {
                    Log.w("MoveToRecycleBin", "Original file deletion failed, but copy succeeded")
                    return false
                }
            }

            true
        } catch (e: Exception) {
            Log.e("MoveToRecycleBin", "Error: ${e.message}")
            false
        }
    }

    // Enhanced file information extractor with extension support
    private fun getFileInfo(
        context: Context,
        uri: Uri,
        originalPath: String
    ): Pair<String, String> {
        // Get original filename or generate one
        val originalName = getFileNameFromUri(context, uri) ?: originalPath.substringAfterLast('/')

        // Determine extension from filename or path
        val fileExt = when {
            originalName.contains('.') -> originalName.substringAfterLast('.')
            originalPath.contains('.') -> originalPath.substringAfterLast('.')
            else -> "dat"
        }.lowercase()

        // Generate proper filename
        val fileName = if (originalName.contains('.')) {
            originalName
        } else {
            "deleted_${System.currentTimeMillis()}.$fileExt"
        }

        // Determine MIME type
        val mimeType = when (fileExt) {
            "jpg", "jpeg" -> "image/jpeg"
            "png" -> "image/png"
            "gif" -> "image/gif"
            "heic", "heif" -> "image/heif"
            "mp4", "m4v" -> "video/mp4"
            "mov" -> "video/quicktime"
            "ts" -> "video/mp2t"
            "webp" -> "image/webp"
            "bmp" -> "image/bmp"
            "svg" -> "image/svg+xml"
            else -> contentResolver.getType(uri) ?: "application/octet-stream"
        }

        return Pair(fileName, mimeType)
    }

    // Enhanced MediaStore URI finder for all media types
    private fun getMediaStoreUriFromPath(filePath: String): Uri? {
        val file = File(filePath)
        val mimeType = contentResolver.getType(file.toUri()) ?: getMimeTypeFromExtension(file.extension)

        // Determine the appropriate MediaStore collection based on MIME type or file extension
        val collection = when {
            mimeType?.startsWith("image/") == true -> MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            mimeType?.startsWith("video/") == true -> MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            file.extension.lowercase() in listOf("mp4", "mkv", "avi", "mov") -> MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            else -> MediaStore.Files.getContentUri("external") // Fallback for other file types
        }

        val projection = arrayOf(MediaStore.MediaColumns._ID)
        val selection = "${MediaStore.MediaColumns.DATA} = ?"
        val selectionArgs = arrayOf(filePath)

        return contentResolver.query(collection, projection, selection, selectionArgs, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID))
                ContentUris.withAppendedId(collection, id)
            } else {
                Log.w("MediaStoreQuery", "No entry found in $collection for $filePath")
                null
            }
        }
    }

    private fun getMimeTypeFromExtension(extension: String): String? {
        return when (extension.lowercase()) {
            "jpg", "jpeg", "png", "gif" -> "image/$extension"
            "mp4", "mkv", "avi", "mov" -> "video/$extension"
            else -> null
        }
    }

    private fun findInMediaStore(
        context: Context,
        contentUri: Uri,
        fileName: String,
        relativePath: String
    ): Uri? {
        val projection = arrayOf(MediaStore.MediaColumns._ID)
        val selection =
            "${MediaStore.MediaColumns.RELATIVE_PATH} = ? AND ${MediaStore.MediaColumns.DISPLAY_NAME} = ?"
        val selectionArgs = arrayOf(relativePath, fileName)

        return context.contentResolver.query(
            contentUri,
            projection,
            selection,
            selectionArgs,
            null
        )?.use { cursor ->
            if (cursor.moveToFirst()) {
                val id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID))
                ContentUris.withAppendedId(contentUri, id)
            } else null
        }
    }

    private fun findInMediaStoreByData(context: Context, filePath: String): Uri? {
        val file = File(filePath)
        val fileName = file.name

        // Supported media collections to search
        val mediaCollections = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            listOf(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                MediaStore.Downloads.EXTERNAL_CONTENT_URI,
                MediaStore.Files.getContentUri("external")
            )
        } else {
            TODO("VERSION.SDK_INT < Q")
        }

        for (contentUri in mediaCollections) {
            val projection = arrayOf(MediaStore.MediaColumns._ID)
            val selection =
                "${MediaStore.MediaColumns.DATA} = ? OR ${MediaStore.MediaColumns.DISPLAY_NAME} = ?"
            val selectionArgs = arrayOf(filePath, fileName)

            context.contentResolver.query(
                contentUri,
                projection,
                selection,
                selectionArgs,
                null
            )?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val id =
                        cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID))
                    return ContentUris.withAppendedId(contentUri, id)
                }
            }
        }

        return null
    }

    // File name extraction
    private fun getFileNameFromUri(context: Context, uri: Uri): String? {
        return when (uri.scheme) {
            "content" -> {
                context.contentResolver.query(
                    uri,
                    arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null
                )?.use { cursor ->
                    if (cursor.moveToFirst()) {
                        cursor.getString(cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME))
                    } else null
                }
            }

            "file" -> uri.lastPathSegment
            else -> uri.lastPathSegment
        }
    }

    // Helper function to get MediaStore URI from file path
    // Improved filename extraction
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

    // Helper function to get file info from a content URI
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

    /*
        private fun setupViewPager(imageList: ArrayList<VideoModel>, currentPosition: Int) {
            viewPagerAdapter = ViedoViewPagerAdapter(this, imageList)
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
                    viewpagerselectedPosition = position

                    imageList[position].apply {
                        deleteMediaModel!!.mediaId = videoId
                        deleteMediaModel!!.mediaName = videoName
                        deleteMediaModel!!.mediaPath = videoPath
                        deleteMediaModel!!.mediaMimeType = "mp4"
                        deleteMediaModel!!.mediaDateAdded = 0
                        deleteMediaModel!!.isVideo = true
                        deleteMediaModel!!.displayDate = ""
                        deleteMediaModel!!.isSelect = isSelect

                        hideMediaModel!!.mediaId = videoId
                        hideMediaModel!!.mediaName = videoName
                        hideMediaModel!!.mediaPath = videoPath
                        hideMediaModel!!.mediaMimeType = "mp4"
                        hideMediaModel!!.mediaDateAdded = 0
                        hideMediaModel!!.isVideo = true
                        hideMediaModel!!.displayDate = ""
                        hideMediaModel!!.isSelect = isSelect

                    }
                    deleteselectedPosition = position
                    videoMediaModel = imageList[position]
                    updateImageTitle(position)
                    viewPagerAdapter.onDestroyVideoView()
                }
            })
        }
    */


    private fun updateImageTitle(position: Int) {
        val fileName = imageListVideo[position]  // Extract file name from path
        binding.tvtitile.text = fileName.videoName
        CoroutineScope(Dispatchers.IO).launch {
            favouriteMediaDao?.let { dao ->
                val isFav = dao.isMediaFavorite(fileName.videoId)
                runOnUiThread {
                    binding.bottomActions.bottomFavorite.setImageResource(if (isFav) R.drawable.fillfavourite else R.drawable.unfillfavourite)
                }
            }
        }
    }

    fun setadpter(imageList: ArrayList<VideoModel>, currentPosition: Int) {

        videoDisplayAdapter = VideoDisplayAdapter(this, imageList, object : VideoPreviousNext {
            override fun onPrevious(currentPos: Int) {

            }

            override fun onNext(currentPos: Int) {

            }
        })

        binding.viewPager.adapter = videoDisplayAdapter
        binding.viewPager.setCurrentItem(currentPosition, true)


        binding.viewPager.setOnPageChangeListener(object : CustomViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int,
            ) {

            }

            override fun onPageSelected(position: Int) {
                // Pause all other videos
                for (i in 0 until binding.viewPager.childCount) {
                    val otherView = binding.viewPager.findViewWithTag<ViewGroup>("video_$i")
                    val otherVideoView = otherView?.findViewById<VideoView>(R.id.video_view)
                    otherVideoView?.pause()
                }

                // Start the new video
                val currentView = binding.viewPager.findViewWithTag<ViewGroup>("video_$position")
                val videoView = currentView?.findViewById<VideoView>(R.id.video_view)
                videoView?.let {
                    it.start()
                }
                updateImageTitle(position)

                viewpagerselectedPosition = position

                imageList[position].apply {
                    deleteMediaModel!!.mediaId = videoId
                    deleteMediaModel!!.mediaName = videoName
                    deleteMediaModel!!.mediaPath = videoPath
                    deleteMediaModel!!.mediaMimeType = "mp4"
                    deleteMediaModel!!.mediaDateAdded = 0
                    deleteMediaModel!!.isVideo = true
                    deleteMediaModel!!.displayDate = ""
                    deleteMediaModel!!.isSelect = isSelect

                    hideMediaModel!!.mediaId = videoId
                    hideMediaModel!!.mediaName = videoName
                    hideMediaModel!!.mediaPath = videoPath
                    hideMediaModel!!.mediaMimeType = "mp4"
                    hideMediaModel!!.mediaDateAdded = 0
                    hideMediaModel!!.isVideo = true
                    hideMediaModel!!.displayDate = ""
                    hideMediaModel!!.isSelect = isSelect

                }
                deleteselectedPosition = position
                videoMediaModel = imageList[position]
                updateImageTitle(position)
            }

            override fun onPageScrollStateChanged(state: Int) {
            }
        })
    }
    private fun applyStatusBarColor() {
        window.statusBarColor =
            resources.getColor(android.R.color.black, theme) // Set black status bar
        window.decorView.systemUiVisibility = 0 // Ensures white text/icons
        window.navigationBarColor = resources.getColor(android.R.color.black, theme)
    }

}