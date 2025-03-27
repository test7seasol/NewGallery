package com.gallery.photos.editpic.Activity

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.transition.TransitionManager
import android.util.Log
import android.view.ScaleGestureDetector
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.gallery.photos.editpic.Adapter.PictureAdapter
import com.gallery.photos.editpic.Dialogs.AllFilesAccessDialog
import com.gallery.photos.editpic.Dialogs.CreateNewFolderDialog
import com.gallery.photos.editpic.Dialogs.DeleteWithRememberDialog
import com.gallery.photos.editpic.Dialogs.SlideShowDialog
import com.gallery.photos.editpic.Extensions.PREF_LANGUAGE_CODE
import com.gallery.photos.editpic.Extensions.gone
import com.gallery.photos.editpic.Extensions.handleBackPress
import com.gallery.photos.editpic.Extensions.hasAllFilesAccessAs
import com.gallery.photos.editpic.Extensions.invisible
import com.gallery.photos.editpic.Extensions.log
import com.gallery.photos.editpic.Extensions.name
import com.gallery.photos.editpic.Extensions.name.getMediaDatabase
import com.gallery.photos.editpic.Extensions.notifyGalleryRoot
import com.gallery.photos.editpic.Extensions.onClick
import com.gallery.photos.editpic.Extensions.setLanguageCode
import com.gallery.photos.editpic.Extensions.tos
import com.gallery.photos.editpic.Extensions.visible
import com.gallery.photos.editpic.Fragment.RecentsPictureFragment.CustomItemAnimator
import com.gallery.photos.editpic.Model.DeleteMediaModel
import com.gallery.photos.editpic.Model.FavouriteMediaModel
import com.gallery.photos.editpic.Model.MediaModel
import com.gallery.photos.editpic.PopupDialog.PicturesBottomPopup
import com.gallery.photos.editpic.PopupDialog.TopMenuPicturesCustomPopup
import com.gallery.photos.editpic.R
import com.gallery.photos.editpic.RoomDB.Dao.DeleteMediaDao
import com.gallery.photos.editpic.Utils.MediaStoreSingleton
import com.gallery.photos.editpic.Utils.SelectionAlLPhotos.selectionArrayList
import com.gallery.photos.editpic.ViewModel.PictureViewModel
import com.gallery.photos.editpic.databinding.ActivityPictureBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class PictureActivity : AppCompatActivity() {

    private var bucketId: String = ""
    private lateinit var binding: ActivityPictureBinding
    private val mediaViewModel: PictureViewModel by viewModels()
    private var pictureAdapter: PictureAdapter? = null
    private var favouriteList: ArrayList<FavouriteMediaModel> = arrayListOf()
    var deleteMediaDao: DeleteMediaDao? = null
    var deleteMediaModel: DeleteMediaModel? = null

    fun toggleTopBarVisibility(isVisible: Boolean) {
        binding.recyclerViewPictures.visibility = View.VISIBLE
        ("is Visisble: $isVisible").log()
        if (isVisible) {
            binding.picturesselectedcontainerid.visible()
            binding.moreMenu.gone()
        } else {
            binding.picturesselectedcontainerid.gone()
            binding.moreMenu.visible()
        }
    }

    var activityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data = result.data

                if (data?.extras?.getString("isFrom") != "CreateClick") return@registerForActivityResult
                // Handle the result here
                Log.d("ActivityResult", "Result received successfully")
                val selectedFiles = selectionArrayList

//                selectedFiles.forEach {
//                    it.log()
//                }

                val isFromMy = (data.extras?.getString("where")!!)

                CreateNewFolderDialog(
                    this,
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).path,
                    selectedFiles,
                    isFromWhere = isFromMy
                ) { newAlbumPath ->
                    Log.d("NewAlbum", "Created new album at $newAlbumPath")
                    notifyGalleryRoot(
                        this,
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).path
                    )
                    mediaViewModel.loadMedia(intent.getStringExtra("BUCKET_ID") ?: "")
                    (getString(R.string.album_created_successfully)).tos(this)
                }
            } else {
                Log.d("ActivityResult", "Result canceled or failed")
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLanguageCode(this, MyApplicationClass.getString(PREF_LANGUAGE_CODE)!!)
        binding = ActivityPictureBinding.inflate(layoutInflater)
        setContentView(binding.root)
        deleteMediaDao = getMediaDatabase(this).deleteMediaDao()
        deleteMediaModel = DeleteMediaModel()

        handleBackPress {
            if (pictureAdapter!!.selectedItems.isEmpty()) {
                finish()
            } else {
                pictureAdapter!!.disableSelectionMode()
            }
        }

        pictureAdapter = PictureAdapter(this) {
            ("Picture Adapter: $it").log()
            if (it) {
                binding.picturesselectedcontainerid.visible()
                binding.moreMenu.gone()
            } else {
                binding.tvAlbumName.text =
                    intent.getStringExtra("folderName") ?: getString(R.string.photos)
                binding.picturesselectedcontainerid.gone()
                binding.moreMenu.visible()
            }
        }

        bucketId = intent.getStringExtra("BUCKET_ID") ?: return
        val folderName = intent.getStringExtra("folderName") ?: return

        binding.tvAlbumName.text = folderName
        binding.ivBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        setupRecyclerView()
        setupPinchToZoomGesture()
        observeMedia()

        // Handle item click to open AlbumPicturesViewPagerActivity
        pictureAdapter!!.onItemClick = { selectedMedia ->
            openViewPagerActivity(selectedMedia)
        }

        binding.apply {

            llShare.setOnClickListener {
                val selectedFiles = pictureAdapter?.selectedItems?.distinctBy { it.mediaPath } ?: emptyList()
                if (selectedFiles.size <= 100) {
                    shareMultipleFiles(selectedFiles, this@PictureActivity)
                } else {
                    getString(R.string.max_selection_limit_is_100).tos(this@PictureActivity)
                }
            }

            llMore.onClick {
                val isSelectAll = (pictureAdapter!!.selectedItems.size != mediaListCheck.size)

                val pictureBottom = PicturesBottomPopup(this@PictureActivity, isSelectAll) {
                    when (it) {
                        "deselectall" -> {
                            pictureAdapter!!.unselectAllItems()
                            binding.picturesselectedcontainerid.gone()
                            binding.moreMenu.visible()
                            binding.tvAlbumName.text =
                                intent.getStringExtra("folderName") ?: getString(R.string.photos)
                        }

                        "selectallid" -> {
                            binding.picturesselectedcontainerid.visible()
                            binding.moreMenu.gone()
                            pictureAdapter!!.selectAllItems()
                        }

                        "movetoid" -> {
                            if (!hasAllFilesAccessAs(this@PictureActivity)) {
                                (getString(R.string.all_files_access_required)).tos(this@PictureActivity)
                                AllFilesAccessDialog(this@PictureActivity) {

                                }
//                                startActivityWithBundle<AllFilePermissionActivity>(Bundle().apply {
//                                    putString("isFrom", "Activitys")
//                                })
                                return@PicturesBottomPopup
                            }

                            selectionArrayList.clear()
                            selectionArrayList =
                                pictureAdapter!!.selectedItems.map { it.mediaPath } as ArrayList<String>

                            activityResultLauncher.launch(
                                Intent(
                                    this@PictureActivity, AllPhotosActivity::class.java
                                ).putExtra("from", "Move")
                            )
                            pictureAdapter!!.unselectAllItems()
                            binding.tvAlbumName.text =
                                intent.getStringExtra("folderName") ?: getString(R.string.photos)
                            binding.picturesselectedcontainerid.gone()
                            binding.moreMenu.visible()
                        }

                        "copytoid" -> {
                            if (!hasAllFilesAccessAs(this@PictureActivity)) {
                                (getString(R.string.all_files_access_required)).tos(this@PictureActivity)
                                AllFilesAccessDialog(this@PictureActivity) {

                                }
//                                startActivityWithBundle<AllFilePermissionActivity>(Bundle().apply {
//                                    putString("isFrom", "Activitys")
//                                })
                                return@PicturesBottomPopup
                            }

                            selectionArrayList.clear()
                            selectionArrayList =
                                pictureAdapter!!.selectedItems.map { it.mediaPath } as ArrayList<String>
                            activityResultLauncher.launch(
                                Intent(
                                    this@PictureActivity, AllPhotosActivity::class.java
                                ).putExtra("from", "Copy")
                            )

                            pictureAdapter!!.unselectAllItems()
                            binding.tvAlbumName.text =
                                intent.getStringExtra("folderName") ?: getString(R.string.photos)
                            binding.picturesselectedcontainerid.gone()
                            binding.moreMenu.visible()

                        }
                    }
                }
                pictureBottom.show(llMore, 0, 0)
            }

            llDelete.setOnClickListener {
                if (!hasAllFilesAccessAs(this@PictureActivity)) {
                    (getString(R.string.all_files_access_required)).tos(this@PictureActivity)
                    AllFilesAccessDialog(this@PictureActivity) {

                    }
//                    this!!.startActivityWithBundle<AllFilePermissionActivity>(Bundle().apply {
//                        putString("isFrom", "Activitys")
//                    })

                    return@setOnClickListener
                }

                val selectedFiles = pictureAdapter!!.selectedItems.distinctBy { it.mediaPath }

                if (selectedFiles.size <= 100) {
                    DeleteWithRememberDialog(this@PictureActivity, false) {
                        CoroutineScope(Dispatchers.Main).launch {
                            // Show Progress Dialog
                            val progressDialog = ProgressDialog(this@PictureActivity).apply {
                                setMessage(getString(R.string.deleting_files))
                                setCancelable(false)
                                show()
                            }

                            withContext(Dispatchers.IO) {
                                val deletionJobs = selectedFiles.map { mediaItem ->
                                    async {
                                        val deleteMediaModel = DeleteMediaModel(
                                            mediaId = mediaItem.mediaId,
                                            mediaName = mediaItem.mediaName,
                                            mediaPath = mediaItem.mediaPath,
                                            mediaMimeType = mediaItem.mediaMimeType,
                                            mediaDateAdded = mediaItem.mediaDateAdded,
                                            isVideo = mediaItem.isVideo,
                                            displayDate = mediaItem.displayDate,
                                            isSelect = mediaItem.isSelect
                                        )

                                        val isMoved = moveToRecycleBin(deleteMediaModel.mediaPath)
                                        if (isMoved) {
                                            deleteMediaModel.binPath = File(
                                                createRecycleBin(), mediaItem.mediaName
                                            ).absolutePath
                                            deleteMediaDao!!.insertMedia(deleteMediaModel)  // Insert into Room DB

                                        } else {
                                            Log.e(
                                                "FileDeletion",
                                                "Failed to move file: ${deleteMediaModel.mediaPath}"
                                            )
                                        }
                                    }
                                }
                                // Wait for all deletion tasks to complete
                                deletionJobs.awaitAll()
                            }

                            // Dismiss Progress Dialog and update UI
                            progressDialog.dismiss()
                            pictureAdapter!!.deleteSelectedItems()
                            pictureAdapter!!.unselectAllItems()
                            binding.tvAlbumName.text =  intent.getStringExtra("folderName") ?: getString(R.string.photos)
                        }
                    }
                } else {
                    ("Max selection limit is 100").tos(this@PictureActivity)
                }
            }

            moreMenu.onClick {
                val topPict = TopMenuPicturesCustomPopup(this@PictureActivity) {
                    when (it) {
                        "llStartSlide" -> {
                            SlideShowDialog(this@PictureActivity) {
                                when (it) {
                                    "lloneSec" -> {
                                        openViewPagerSlideShowActivity(0, 1)
                                    }

                                    "lltwoSec" -> {
                                        openViewPagerSlideShowActivity(0, 2)
                                    }

                                    "llthreeSec" -> {
                                        openViewPagerSlideShowActivity(0, 3)
                                    }

                                    "llfourSec" -> {
                                        openViewPagerSlideShowActivity(0, 4)
                                    }

                                    "llfiveSec" -> {
                                        openViewPagerSlideShowActivity(0, 5)
                                    }
                                }
                            }
                        }

                        "llSelectAll" -> {
                            moreMenu.invisible()
                            pictureAdapter!!.selectAllItems()
                        }
                    }
                }
                topPict.show(moreMenu, 0, 0)

            }
        }

        mediaViewModel.loadMedia(bucketId)
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
            moveToRecycleBinLegacy(originalFilePath,this)
        }
    }

    fun shareMultipleFiles(filePaths: List<MediaModel>, context: Context) {
        val fileUris = ArrayList<Uri>()

        filePaths.forEach { media ->
            try {
                var uri = Uri.parse(media.mediaPath)

                val finalUri = when {
                    // If it's already a content URI (works across all versions)
                    uri.scheme == "content" -> uri

                    // Android 7-9 (API 24-28): Nougat, Oreo, Pie
                    Build.VERSION.SDK_INT <= Build.VERSION_CODES.P -> {
                        val file = File(media.mediaPath)
                        FileProvider.getUriForFile(
                            context,
                            "${context.packageName}.provider",
                            file
                        )
                    }

                    // Android 10-11 (API 29-30): Q, R
                    Build.VERSION.SDK_INT <= Build.VERSION_CODES.R -> {
                        FileProvider.getUriForFile(
                            context,
                            "${context.packageName}.provider",
                            File(media.mediaPath)
                        )
                    }

                    // Android 12-15 (API 31-35+): S, Tiramisu, UpsideDownCake, VanillaIceCream
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                        FileProvider.getUriForFile(
                            context,
                            "${context.packageName}.provider",
                            File(media.mediaPath)
                        )
                    }

                    else -> {
                        // Fallback for any unforeseen versions
                        FileProvider.getUriForFile(
                            context,
                            "${context.packageName}.provider",
                            File(media.mediaPath)
                        )
                    }
                }

                if (finalUri != null) {
                    context.grantUriPermission(
                        context.packageName,
                        finalUri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                    fileUris.add(finalUri)
                    Log.d("ShareFiles", "✅ URI added: $finalUri")
                } else {
                    Log.e("ShareFiles", "⚠️ Null URI for: ${media.mediaPath}")
                }
            } catch (e: Exception) {
                Log.e("ShareFiles", "❌ Error processing URI: ${media.mediaPath}", e)
            }
        }

        // Function to get MIME type from file path if not provided
        fun getMimeTypeFromPath(path: String): String {
            val extension = MimeTypeMap.getFileExtensionFromUrl(path)?.lowercase()
            return when (extension) {
                "mp4" -> "video/mp4" // Explicitly handle MP4 files
                else -> MimeTypeMap.getSingleton()
                    .getMimeTypeFromExtension(extension) ?: "video/*"
            }
        }

        if (fileUris.isNotEmpty()) {
            // Map MIME types, prioritizing MP4 detection
            val mimeTypes = filePaths.map { media ->
                media.mediaMimeType ?: getMimeTypeFromPath(media.mediaPath)
            }

            val shareIntent = Intent(Intent.ACTION_SEND_MULTIPLE).apply {
                // Default to video/mp4 since we're focusing on MP4 files
                type = "video/mp4"

                // Validate that all files are videos, preferably MP4
                val allVideos = mimeTypes.all { it.startsWith("video/") }
                val allMp4 = mimeTypes.all { it == "video/mp4" }

                when {
                    allMp4 -> type = "video/mp4" // All are MP4 files
                    allVideos -> type = "video/*" // All are videos but not all MP4
                    else -> {
                        type = "*/*" // Mixed content
                        Log.w("ShareFiles", "Some files may not be videos: $mimeTypes")
                    }
                }

                putParcelableArrayListExtra(Intent.EXTRA_STREAM, fileUris)

                flags = when {
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.N -> {
                        Intent.FLAG_GRANT_READ_URI_PERMISSION or
                                Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                    }

                    else -> {
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    }
                }

                // Add extra metadata for video sharing
                putExtra(Intent.EXTRA_TITLE, "Shared MP4 Videos")
            }

            try {
                val chooserIntent = Intent.createChooser(shareIntent, "Share MP4 Videos")
                if (shareIntent.resolveActivity(context.packageManager) != null) {
                    context.startActivity(chooserIntent)
                } else {
                    Log.e("ShareFiles", "❌ No activity found to handle share intent")
                    if (context is Activity) {
                        Toast.makeText(
                            context,
                            "No app available to share videos",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: Exception) {
                Log.e("ShareFiles", "❌ Error starting share intent", e)
            }
        } else {
            Log.e("ShareFiles", "❌ No valid files to share")
            if (context is Activity) {
                Toast.makeText(context, "No videos available to share", Toast.LENGTH_SHORT).show()
            }
        }
    }


    // ✅ Android 11+ (Scoped Storage) - Uses ContentResolver
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
            recycleBin,
            getFileNameFromUri(uri) ?: "deleted_file_${System.currentTimeMillis()}"
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

    // Helper function to get MediaStore URI from file path
    private fun getMediaStoreUriFromPath(filePath: String): Uri? {
        val projection = arrayOf(MediaStore.Images.Media._ID)
        val selection = "${MediaStore.Images.Media.DATA} = ?"
        val selectionArgs = arrayOf(filePath)

        contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            null
        )?.use { cursor ->
            if (cursor.moveToFirst()) {
                val id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID))
                return ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
            }
        }
        return null
    }

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

    override fun onResume() {
        super.onResume()
        mediaViewModel.loadMedia(intent.getStringExtra("BUCKET_ID") ?: "")
    }

    lateinit var gridLayoutManager: GridLayoutManager
    private lateinit var scaleGestureDetector: ScaleGestureDetector
    private var currentSpanCount = 3  // Default span count
    private val minSpanCount = 2
    private val maxSpanCount = 6

    private fun setupRecyclerView() {

        gridLayoutManager = GridLayoutManager(this@PictureActivity, currentSpanCount) // Initialize it here

        binding.recyclerViewPictures.apply {
            layoutManager = gridLayoutManager // Assign it to RecyclerView
            adapter = pictureAdapter
        }
        binding.recyclerViewPictures.itemAnimator = CustomItemAnimator()
        binding.recyclerViewPictures.scheduleLayoutAnimation()
    }
    private fun setupPinchToZoomGesture() {
        scaleGestureDetector = ScaleGestureDetector(
            this,
            object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
                override fun onScale(detector: ScaleGestureDetector): Boolean {
                    val scaleFactor = detector.scaleFactor

                    // Define a threshold to prevent frequent small changes
                    val zoomThreshold = 1.1f

                    if (scaleFactor > zoomThreshold && currentSpanCount > minSpanCount) {
                        currentSpanCount--
                    } else if (scaleFactor < (1 / zoomThreshold) && currentSpanCount < maxSpanCount) {
                        currentSpanCount++
                    } else {
                        return false
                    }

                    // Smoothly update the span count with animation
                    TransitionManager.beginDelayedTransition(binding.recyclerViewPictures)
                    gridLayoutManager.spanCount = currentSpanCount
                    binding.recyclerViewPictures.adapter?.notifyItemRangeChanged(
                        0, binding.recyclerViewPictures.adapter?.itemCount ?: 0
                    )
                    return true
                }
            })

        binding.recyclerViewPictures.setOnTouchListener { _, event ->
            scaleGestureDetector.onTouchEvent(event)
            false
        }
    }
    @SuppressLint("NotifyDataSetChanged")
    private fun observeMedia() {
        lifecycleScope.launch {
            getMediaDatabase(this@PictureActivity).favouriteMediaDao().getAllMediaLive()
                .observe(this@PictureActivity) {
                    favouriteList.clear()
                    favouriteList.addAll(it)
                    pictureAdapter!!.notifyDataSetChanged()
                }
        }

        mediaViewModel.mediaLiveData.observe(this) { mediaList ->
            mediaListCheck.clear()
            ("Picture Act Observer: ${mediaList.size}").log()

            if (mediaList.isEmpty()) finish()

            mediaList.forEach { media ->
                media.isFav = favouriteList.find { it.mediaId == media.mediaId }?.isFav == true
            }

            mediaListCheck.addAll(mediaList)

            pictureAdapter!!.submitList(mediaList)
        }
    }

    var mediaListCheck: ArrayList<MediaModel> = arrayListOf()

    private fun openViewPagerActivity(selectedMedia: MediaModel) {
        MediaStoreSingleton.imageList = ArrayList(pictureAdapter!!.currentList)
        MediaStoreSingleton.selectedPosition = pictureAdapter!!.currentList.indexOf(selectedMedia)

        val intent = Intent(this, AlbumPicturesViewPagerActivity::class.java)
        startActivity(intent)
    }
    private fun openViewPagerSlideShowActivity(
        position: Int, secound: Int
    ) {
        MediaStoreSingleton.imageList = ArrayList(pictureAdapter!!.currentList)
        MediaStoreSingleton.selectedPosition = position
        val intent = Intent(this, AlbumPicturesViewPagerActivity::class.java)
        intent.putExtra("slideshow", true)
        intent.putExtra("secoundSlideShow", secound)
        startActivity(intent)
    }
}