package com.gallery.photos.editpic.Activity

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
import androidx.viewpager2.widget.ViewPager2
import com.gallery.photos.editpic.Adapter.FavouriteViewPagerAdapter
import com.gallery.photos.editpic.Dialogs.AllFilesAccessDialog
import com.gallery.photos.editpic.Dialogs.DeleteWithRememberDialog
import com.gallery.photos.editpic.Dialogs.PropertiesDialog
import com.gallery.photos.editpic.Extensions.PREF_LANGUAGE_CODE
import com.gallery.photos.editpic.Extensions.formatDate
import com.gallery.photos.editpic.Extensions.getMimeTypeFromPath
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
import com.gallery.photos.editpic.PopupDialog.ViewPagerHidePopupManager
import com.gallery.photos.editpic.R
import com.gallery.photos.editpic.RoomDB.Dao.DeleteMediaDao
import com.gallery.photos.editpic.RoomDB.Dao.FavouriteMediaDao
import com.gallery.photos.editpic.RoomDB.Dao.HideMediaDao
import com.gallery.photos.editpic.Utils.DeleteMediaStoreSingleton.deleteselectedPosition
import com.gallery.photos.editpic.Utils.FavouriteMediaStoreSingleton
import com.gallery.photos.editpic.Utils.FavouriteMediaStoreSingleton.favouriteimageList
import com.gallery.photos.editpic.databinding.ActivityFavouriteviewPagerBinding
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

class FavouriteViewPagerActivity : BaseActivity() {

    private var viewpagerselectedPosition: Int = 1
    var deleteMediaModel: DeleteMediaModel? = null
    private lateinit var binding: ActivityFavouriteviewPagerBinding
    private lateinit var viewPagerAdapter: FavouriteViewPagerAdapter
    private var imageListFavourite: ArrayList<FavouriteMediaModel> = arrayListOf()
    var deleteMediaDao: DeleteMediaDao? = null
    var favouriteMediaDao: FavouriteMediaDao? = null
    var hideMediaDao: HideMediaDao? = null
    var hideMediaModel: HideMediaModel? = null
    var isFromSlideShow: Boolean = false
    var secoundSlideShow: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLanguageCode(this, MyApplicationClass.getString(PREF_LANGUAGE_CODE)!!)
        binding = ActivityFavouriteviewPagerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        applyStatusBarColor()
        imageListFavourite = FavouriteMediaStoreSingleton.favouriteimageList
        viewpagerselectedPosition = FavouriteMediaStoreSingleton.favouriteselectedPosition
        hideMediaDao = getMediaDatabase(this).hideMediaDao()

        hideMediaModel = HideMediaModel()
        deleteMediaModel = DeleteMediaModel()

        favouriteMediaDao = getMediaDatabase(this).favouriteMediaDao()
        deleteMediaDao = getMediaDatabase(this).deleteMediaDao()

        isFromSlideShow = intent?.extras?.getBoolean("slideshow", false) == true
        secoundSlideShow = intent?.extras?.getInt("secoundSlideShow", 1) ?: 1

        ("Favourite onPageSelected: $viewpagerselectedPosition").log()

        setupViewPager(imageListFavourite, viewpagerselectedPosition)

        hideBottomNavigationBar(R.color.black)


        MyAllAdCommonClass.showAdmobBanner(
            this@FavouriteViewPagerActivity,
            binding.bannerContainer,
            binding.shimmerContainerBanner,
            false,
            MyAddPrefs(this@FavouriteViewPagerActivity).admBannerId
        )

        binding.ivBack.setOnClickListener {
            finish()
        }

        binding.bottomActions.bottomEdit.onClick {
            val mediaUriString = imageListFavourite[viewpagerselectedPosition].mediaPath
            val mediaUri = fixMalformedUri(mediaUriString)

            val filePath = if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S) {
                resolveFilePath(this@FavouriteViewPagerActivity, mediaUri)
            } else {
                mediaUriString
            }

            Log.d("FATZ", "Loading image from mediaUri: $mediaUri || filePath: $filePath")



            val intent = Intent(this@FavouriteViewPagerActivity, PhotoEditorActivity::class.java).apply {
                putExtra(PhotoPicker.KEY_SELECTED_PHOTOS, filePath)
            }
            startActivity(intent)
        }


        binding.apply {
            ivMore.onClick {
                val topcustomtopcustompopup =
                    ViewPagerHidePopupManager(this@FavouriteViewPagerActivity) {
                        when (it) {
                            "hiddentoid" -> {

                                if (!hasAllFilesAccessAs(this@FavouriteViewPagerActivity)) {
                                    (getString(R.string.all_files_access_required)).tos(this@FavouriteViewPagerActivity)
                                    AllFilesAccessDialog(this@FavouriteViewPagerActivity) {

                                    }
//                    startActivityWithBundle<AllFilePermissionActivity>(Bundle().apply {
//                        putString("isFrom", "Activitys")
//                    })
                                    return@ViewPagerHidePopupManager
                                }

                                renameFileToHide(
                                    this@FavouriteViewPagerActivity,
                                    deleteMediaModel!!.mediaPath
                                )
                                imageListFavourite.removeAt(viewpagerselectedPosition)
                                binding.viewPager.currentItem = viewpagerselectedPosition

                                if (imageListFavourite.isEmpty()) {
                                    finish()
                                } else setupViewPager(imageListFavourite, viewpagerselectedPosition)
                            }
                        }
                    }
                topcustomtopcustompopup.show(ivMore, 0, 0)
            }

            binding.bottomActions.bottomProperties.onClick {
                PropertiesDialog(
                    this@FavouriteViewPagerActivity, MediaModel(
                        mediaId = imageListFavourite[viewpagerselectedPosition]!!.mediaId,
                        mediaName = imageListFavourite[viewpagerselectedPosition]!!.mediaName,
                        mediaPath = imageListFavourite[viewpagerselectedPosition].mediaPath,
                        mediaMimeType = getMimeTypeFromPath(imageListFavourite[viewpagerselectedPosition].mediaPath).toString(),
                        mediaDateAdded = imageListFavourite[viewpagerselectedPosition]!!.mediaDateAdded,
                        isVideo = isVideoFile(imageListFavourite[viewpagerselectedPosition].mediaPath),
                        displayDate = formatDate(imageListFavourite[viewpagerselectedPosition]!!.mediaDateAdded),
                        isSelect = false,
                        isFav = imageListFavourite[viewpagerselectedPosition].isFav
                    )
                ) {}
            }

            binding.bottomActions.bottomFavorite.setOnClickListener {
                val position = viewpagerselectedPosition
                val currentMedia = imageListFavourite[position]
                currentMedia.isFav = !currentMedia.isFav  // Toggle the favorite status

                CoroutineScope(Dispatchers.IO).launch {
                    val isFav = favouriteMediaDao?.isMediaFavorite(currentMedia.mediaId) ?: false

                    if (isFav) {
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

            bottomActions.bottomShare.onClick {
                shareFile(
                    this@FavouriteViewPagerActivity,
                    imageListFavourite[viewpagerselectedPosition].mediaPath
                )
            }

            bottomActions.bottomDelete.onClick {
                if (!hasAllFilesAccessAs(this@FavouriteViewPagerActivity)) {
                    (getString(R.string.all_files_access_required)).tos(this@FavouriteViewPagerActivity)
                    AllFilesAccessDialog(this@FavouriteViewPagerActivity){

                    }
//                    startActivityWithBundle<AllFilePermissionActivity>(Bundle().apply {
//                        putString("isFrom", "Activitys")
//                    })
                    return@onClick
                }

                DeleteWithRememberDialog(this@FavouriteViewPagerActivity) {
                    run {
                        val currentMedia = imageListFavourite[viewpagerselectedPosition]
                        CoroutineScope(Dispatchers.IO).launch {
                            favouriteMediaDao?.getMediaById(currentMedia.mediaId)
                                ?.let { favouriteMediaDao?.deleteMedia(it) }
                        }
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
                                if (imageListFavourite.size + 1 >= viewpagerselectedPosition) {

                                imageListFavourite.removeAt(viewpagerselectedPosition)

                                viewPagerAdapter.notifyDataSetChanged()

                                if (imageListFavourite.isNotEmpty()) {
//                                (imageListFavourite.toList())
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
        }

        if (isFromSlideShow) {
            isOneTimeVisibleTools = !isOneTimeVisibleTools
            binding.bottomActions.root.visibility = View.INVISIBLE
            binding.rltop.visibility = View.INVISIBLE
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN // Hide status bar
            ("Slide start $secoundSlideShow sec").log()
            startTimerTask()
        }
    }

    fun resolveFilePath(context: Context, uri: Uri): String? {
        return when {
            "file".equals(uri.scheme, ignoreCase = true) -> {
                // Direct file path
                uri.path
            }

            "content".equals(uri.scheme, ignoreCase = true) -> {
                // Try resolving content URI to file path
                getFilePathFromUri(context, uri) ?: copyFileToCache(context, uri)?.absolutePath
            }

            else -> null
        }
    }

    fun copyFileToCache(context: Context, uri: Uri): File? {
        val file = File(context.cacheDir, "temp_${System.currentTimeMillis()}.jpg")
        return try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                FileOutputStream(file).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
            file
        } catch (e: Exception) {
            Log.e("FileCopy", "Error copying file to cache", e)
            null
        }
    }

    var isOneTimeVisibleTools = false

    fun fixMalformedUri(uriString: String): Uri {
        val decoded = Uri.decode(uriString) // Decodes %3A to :
        if (decoded.startsWith("file:///content:")) {
            val corrected = decoded.replace("file:///", "")
            return Uri.parse(corrected) // Should become content://media/external/file/1000025614
        }
        return Uri.parse(uriString) // Fallback to original
    }

    fun getFilePathFromUri(context: Context, uri: Uri): String? {
        when (uri.scheme) {
            "content" -> {
                // Query MediaStore for the file path
                val projection = arrayOf(MediaStore.Images.Media.DATA)
                context.contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
                    if (cursor.moveToFirst()) {
                        val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                        return cursor.getString(columnIndex)
                    }
                }
            }

            "file" -> {
                // Directly use the file path from the Uri
                return uri.path
            }
        }

        // Fallback: Copy to a temp file if direct path isn’t available
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val tempFile = File.createTempFile("temp_image", ".jpg", context.cacheDir)
            inputStream?.use { input ->
                tempFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            tempFile.absolutePath
        } catch (e: Exception) {
            Log.e("FilePath", "Failed to get path from Uri: $uri", e)
            null
        }
    }

    private fun startTimerTask() {
        timer = Timer()
        timer?.schedule(object : TimerTask() {
            override fun run() {
                handler.post {
                    val position = binding.viewPager.currentItem
                    if (position < favouriteimageList.size - 1) {
                        binding.viewPager.currentItem = (position + 1)
                        updateImageTitle(position + 1)
                    } else {
                        (getString(R.string.slide_end)).tos(this@FavouriteViewPagerActivity)
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


    private var timer: Timer? = null
    private val handler = Handler(Looper.getMainLooper())

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

    fun renameAndHidePhoto(originalFilePath: String): Boolean {
        val originalFile = File(originalFilePath)
        if (!originalFile.exists()) {
            Log.e("RenameAndHide", "File not found: $originalFilePath")
            return false
        }

        val parentDir = originalFile.parentFile
        val newFileName = ".${originalFile.name}"  // Prefix the filename with a dot
        val hiddenFile = File(parentDir, newFileName)

        favouriteimageList[viewpagerselectedPosition].mediaName = newFileName
//        updateImageTitle(viewpagerselectedPosition)
        binding.tvtitile.text = newFileName

        favouriteimageList[viewpagerselectedPosition].apply {
            hideMediaModel!!.mediaId = mediaId
            hideMediaModel!!.mediaName = newFileName
            hideMediaModel!!.mediaPath = hiddenFile.path
            hideMediaModel!!.mediaMimeType = mediaMimeType
            hideMediaModel!!.mediaDateAdded = mediaDateAdded
            hideMediaModel!!.isVideo = isVideo
            hideMediaModel!!.displayDate = displayDate
            hideMediaModel!!.isSelect = isSelect
        }

        CoroutineScope(Dispatchers.IO).launch {
            hideMediaModel?.let { hideMediaDao!!.insertMedia(it) }
            imageListFavourite[viewpagerselectedPosition].let { favouriteMediaDao!!.deleteMedia(it) }
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

    // ✅ Android 11+ (Scoped Storage) - Uses ContentResolver
    fun moveToRecycleBinScopedStorage(originalFilePath: String): Boolean {
        Log.e("MoveToRecycleBin", "Cannotath: " + originalFilePath)
        val context = applicationContext
        val contentResolver = context.contentResolver

        // 1. Get proper URI for the source file
        val sourceUri = when {
            originalFilePath.startsWith("content://") -> Uri.parse(originalFilePath)
            else -> getMediaStoreUriFromPath(context, originalFilePath) ?: run {
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
                        sourceUri, null, null
                    ) > 0

                    sourceUri.scheme == "file" -> File(sourceUri.path!!).delete()
                    else -> false
                }

                if (deleteSuccess) {
                    // Notify adapter about item removal
                    // Notify MediaStore about changes
                    MediaScannerConnection.scanFile(
                        context, arrayOf(destinationFile.absolutePath), arrayOf(mimeType), null
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
        context: Context, uri: Uri, originalPath: String
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


    private fun getFileNameFromUri(context: Context, uri: Uri): String? {

        return when (uri.scheme) {
            "content" -> {
                context.contentResolver.query(
                    uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null
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


    private fun findInMediaStore(
        context: Context, contentUri: Uri, fileName: String, relativePath: String
    ): Uri? {
        val projection = arrayOf(MediaStore.MediaColumns._ID)
        val selection =
            "${MediaStore.MediaColumns.RELATIVE_PATH} = ? AND ${MediaStore.MediaColumns.DISPLAY_NAME} = ?"
        val selectionArgs = arrayOf(relativePath, fileName)

        return context.contentResolver.query(
            contentUri, projection, selection, selectionArgs, null
        )?.use { cursor ->
            if (cursor.moveToFirst()) {
                val id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID))
                ContentUris.withAppendedId(contentUri, id)
            } else null
        }
    }

    private fun getMediaStoreUriFromPath(context: Context, filePath: String): Uri? {
        val file = File(filePath)
        val fileName = file.name
        val relativePath = filePath.substringAfterLast("/DCIM/").substringBeforeLast("/")

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

        // Try each media collection
        for (contentUri in mediaCollections) {
            findInMediaStore(context, contentUri, fileName, "DCIM/$relativePath/")?.let {
                return it
            }
        }

        // Fallback to search by absolute path
        return findInMediaStoreByData(context, filePath)
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
                contentUri, projection, selection, selectionArgs, null
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
                    context, arrayOf(recycledFile.absolutePath), arrayOf(mimeType), null
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

    private fun setupViewPager(favimageList: ArrayList<FavouriteMediaModel>, currentPosition: Int) {
        viewPagerAdapter = FavouriteViewPagerAdapter(this, favimageList) {
            isOneTimeVisibleTools = !isOneTimeVisibleTools
            if (isOneTimeVisibleTools) {
                binding.bottomActions.root.visibility = View.INVISIBLE
                binding.rltop.visibility = View.INVISIBLE
                window.decorView.systemUiVisibility =
                    View.SYSTEM_UI_FLAG_FULLSCREEN // Hide status bar
                hideNavigationBar(this)
            } else {
                if (isFromSlideShow) {
                    sliderstop()
                }
                binding.bottomActions.root.visibility = View.VISIBLE
                binding.rltop.visibility = View.VISIBLE
                window.decorView.systemUiVisibility =
                    View.SYSTEM_UI_FLAG_VISIBLE // Show status bar & navbar
                hideNavigationBar(this)
            }
        }
        binding.viewPager.adapter = viewPagerAdapter
        binding.viewPager.setCurrentItem(currentPosition, false)

        binding.viewPager.offscreenPageLimit = 1
        // Set initial image title
//        updateImageTitle(currentPosition)
        updateImageTitle(viewpagerselectedPosition)

        // Change title when page is scrolled
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                ("Delete onPageSelected: $position").log()

                try {
                    if (favimageList.isEmpty() || position >= favimageList.size) {
                        Log.e(
                            "ViewPager",
                            "Invalid position: $position, List size: ${favimageList.size}"
                        )
                        return // Prevent crash if list is empty or index is out of bounds
                    }

                viewpagerselectedPosition = position
                deleteselectedPosition = position

                favimageList[position].apply {
                    deleteMediaModel?.mediaId = mediaId
                    deleteMediaModel?.mediaName = mediaName
                    deleteMediaModel?.mediaPath = mediaPath
                    deleteMediaModel?.mediaMimeType = mediaMimeType
                    deleteMediaModel?.mediaDateAdded = mediaDateAdded
                    deleteMediaModel?.isVideo = isVideo
                    deleteMediaModel?.displayDate = displayDate
                    deleteMediaModel?.isSelect = isSelect
                }
                updateImageTitle(position)

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        })
    }

    private fun updateImageTitle(position: Int) {
        val fileName = imageListFavourite[position]  // Extract file name from path
        binding.tvtitile.text = fileName.mediaName
        binding.bottomActions.bottomFavorite.setImageResource(if (fileName.isFav) R.drawable.fillfavourite else R.drawable.unfillfavourite)
        binding.bottomActions.bottomEdit.visibility =
            if (isVideoFile(fileName.mediaPath)) View.GONE else View.VISIBLE
    }
    private fun applyStatusBarColor() {
        window.statusBarColor =
            resources.getColor(android.R.color.black, theme) // Set black status bar
        window.decorView.systemUiVisibility = 0 // Ensures white text/icons
        window.navigationBarColor = resources.getColor(android.R.color.black, theme)
    }
}