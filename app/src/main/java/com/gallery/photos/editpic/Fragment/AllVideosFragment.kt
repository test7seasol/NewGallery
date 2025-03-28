package com.gallery.photos.editpic.Fragment

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.app.ProgressDialog
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.gallery.photos.editpic.Activity.AllPhotosActivity
import com.gallery.photos.editpic.Activity.VideoViewPagerActivity
import com.gallery.photos.editpic.Adapter.VideoAdapter
import com.gallery.photos.editpic.Dialogs.AllFilesAccessDialog
import com.gallery.photos.editpic.Dialogs.CreateNewFolderDialog
import com.gallery.photos.editpic.Dialogs.DeleteWithRememberDialog
import com.gallery.photos.editpic.Extensions.formatDate
import com.gallery.photos.editpic.Extensions.gone
import com.gallery.photos.editpic.Extensions.hasAllFilesAccessAs
import com.gallery.photos.editpic.Extensions.isVideoFile
import com.gallery.photos.editpic.Extensions.log
import com.gallery.photos.editpic.Extensions.name.getMediaDatabase
import com.gallery.photos.editpic.Extensions.notifyGalleryRoot
import com.gallery.photos.editpic.Extensions.onClick
import com.gallery.photos.editpic.Extensions.tos
import com.gallery.photos.editpic.Extensions.visible
import com.gallery.photos.editpic.Model.DeleteMediaModel
import com.gallery.photos.editpic.Model.FavouriteMediaModel
import com.gallery.photos.editpic.Model.VideoModel
import com.gallery.photos.editpic.PopupDialog.PicturesBottomPopup
import com.gallery.photos.editpic.PopupDialog.TopMenuVideosCustomPopup
import com.gallery.photos.editpic.R
import com.gallery.photos.editpic.RoomDB.Dao.DeleteMediaDao
import com.gallery.photos.editpic.RoomDB.Dao.FavouriteMediaDao
import com.gallery.photos.editpic.Utils.SelectionAlLPhotos.selectionArrayList
import com.gallery.photos.editpic.Utils.SelectionModeListener
import com.gallery.photos.editpic.Utils.VideoMediaStoreSingleton
import com.gallery.photos.editpic.ViewModel.VideoViewModel
import com.gallery.photos.editpic.ViewModel.VideoViewModelFactory
import com.gallery.photos.editpic.databinding.FragmentVideosBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class AllVideosFragment : Fragment() {

    private var _binding: FragmentVideosBinding? = null
    private val binding get() = _binding!!

    private val viewModel: VideoViewModel by viewModels { VideoViewModelFactory(requireActivity()) }
    private lateinit var videoAdapter: VideoAdapter
    private var favouriteList: ArrayList<FavouriteMediaModel> = arrayListOf()
    var deleteMediaModel: DeleteMediaModel? = null
    var deleteMediaDao: DeleteMediaDao? = null
    private lateinit var favouriteMediaDao: FavouriteMediaDao

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVideosBinding.inflate(inflater, container, false)
        return binding.root
    }

    var activityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data = result.data

                if (data?.extras?.getString("isFrom") != "CreateClick") return@registerForActivityResult
                // Handle the result here
                Log.d("ActivityResult", "Result received successfully")
                val selectedFiles = selectionArrayList

                if (selectedFiles.isEmpty()) {
                    Log.e("CreateNewFolder", "No files selected for moving")
                    return@registerForActivityResult
                }

                try {

                    CreateNewFolderDialog(
                        requireActivity(),
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).path,
                        selectedFiles,
                        isFromWhere = (data.extras?.getString("where")!!)
                    ) { newAlbumPath ->

                        Log.d("NewAlbum", "Created new album at $newAlbumPath")
                        notifyGalleryRoot(
                            requireActivity(),
                            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).path
                        )
                        val fragmentB =
                            parentFragmentManager.findFragmentByTag("FragmentBTag") as? AlbumFragment
                        fragmentB?.refreshFolder()

                        ("Album created successfully").tos(requireActivity())
                    }
                } catch (e: Exception) {
                    Log.e("CreateNewFolder", "Error creating folder dialog", e)
                    "Failed to create folder".tos(requireActivity())
                }
            } else {
                Log.d("ActivityResult", "Result canceled or failed")
            }
        }

    fun toggleTopBarVisibility(isVisible: Boolean) {
        binding.recyclerViewVideos.visibility = View.VISIBLE
        ("is Visisble: $isVisible").log()
        if (isVisible) {
            binding.selectedcontainerVideo.visible()
        } else {
            binding.selectedcontainerVideo.gone()
//            binding.ivSearch.visible()
            binding.menuDot.visible()
        }
    }

    private fun checkPermissions() {
        val permission = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> {
                // Android 15 (API 34) and above
                Manifest.permission.READ_MEDIA_VIDEO
            }

            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                // Android 13+ (API 33)
                Manifest.permission.READ_MEDIA_VIDEO
            }

            else -> {
                // Android 12 and below
                Manifest.permission.READ_EXTERNAL_STORAGE
            }
        }

        if (ContextCompat.checkSelfPermission(
                requireActivity(), permission
            ) == PackageManager.PERMISSION_GRANTED
        ) {
//            getAllVideos(requireActivity())
        } else {
            requestPermissionLauncher.launch(permission)
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
//            getAllVideos(requireActivity())
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkPermissions()
        setupRecyclerView()
        favouriteMediaDao = getMediaDatabase(requireActivity()).favouriteMediaDao()
        observeViewModel()
        setUpListioner()
        viewModel.loadAllVideos()
    }

    private fun setUpListioner() {
        deleteMediaModel = DeleteMediaModel()
        deleteMediaDao = getMediaDatabase(requireActivity()).deleteMediaDao()

        binding.apply {
            ivBack.onClick {
                if (videoAdapter.selectedItems.isEmpty()) {
                    requireActivity().finish()
                } else {
                    videoAdapter.disableSelectionMode()
                }
            }

            ivSearch.onClick { }

            menuDot.onClick {
                val videoMenu = TopMenuVideosCustomPopup(requireActivity()) {
                    when (it) {
                        "llStartSlide" -> {

                        }

                        "llSelectAll" -> {
                            videoAdapter.selectAllItems()
                            binding.selectedcontainerVideo.visible()
//                            binding.ivSearch.gone()
                            binding.menuDot.gone()
                        }
                    }
                }
                videoMenu.show(menuDot, 0, 0)
            }
            llShare.onClick {
                val selectedFiles = videoAdapter.selectedItems.distinctBy { it.videoPath }
                if (selectedFiles.size <= 100) {
                    shareMultipleVideoFiles(selectedFiles, requireActivity())
                } else {
                    (getString(R.string.max_selection_limit_is_100)).tos(requireActivity())
                }
            }

            llDelete.setOnClickListener {
                if (!hasAllFilesAccessAs(requireActivity())) {
                    (requireActivity().getString(R.string.all_files_access_required)).tos(
                        requireActivity()
                    )
//                    activity.startActivityWithBundle<AllFilePermissionActivity>(Bundle().apply {
//                        putString("isFrom", "Activitys")
//                    })
                    AllFilesAccessDialog(requireActivity()) {

                    }
                    return@setOnClickListener
                }

                val selectedFiles = videoAdapter.selectedItems.distinctBy { it.videoPath }

                if (selectedFiles.size <= 100) {
                    DeleteWithRememberDialog(requireActivity(), false) {
                        CoroutineScope(Dispatchers.Main).launch {
                            // Show Progress Dialog
                            val progressDialog = ProgressDialog(requireActivity()).apply {
                                setMessage(getString(R.string.deleting_files))
                                setCancelable(false)
                                show()
                            }

                            withContext(Dispatchers.IO) {
                                val deletionJobs = selectedFiles.map { mediaItem ->
                                    async {
                                        val deleteMediaModel = DeleteMediaModel(
                                            mediaId = mediaItem.videoId,
                                            mediaName = mediaItem.videoName,
                                            mediaPath = mediaItem.videoPath,
                                            mediaMimeType = "mp4",
                                            mediaDateAdded = mediaItem.videoDateAdded,
                                            isVideo = isVideoFile(mediaItem.videoPath),
                                            displayDate = formatDate(mediaItem.videoDateAdded),
                                            isSelect = mediaItem.isSelect
                                        )

                                        val isMoved = moveToRecycleBin(deleteMediaModel.mediaPath)
                                        if (isMoved) {
                                            deleteMediaModel.binPath = File(
                                                createRecycleBin(), mediaItem.videoName
                                            ).absolutePath
                                            deleteMediaDao!!.insertMedia(deleteMediaModel)  // Insert into Recycle Bin

                                            favouriteMediaDao.getMediaById(mediaItem.videoId)
                                                ?.let { it1 -> favouriteMediaDao.deleteMedia(it1) }

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
                            videoAdapter.deleteSelectedItems()
                            videoAdapter.unselectAllItems()
                            tvTitalVideo.text = getString(R.string.videos)
//                            viewModel.loadRecentMedia()
                        }
                    }
                } else {
                    (getString(R.string.max_selection_limit_is_100)).tos(requireActivity())
                }
            }

            llMore.onClick {
                val isSelectAll = (videoAdapter?.selectedItems?.size != mediaListCheck.size)

                val pictureBottom = PicturesBottomPopup(requireActivity(), isSelectAll) {
                    when (it) {
                        "deselectall" -> {
                            try {
                            videoAdapter.unselectAllItems()
                            binding.selectedcontainerVideo.gone()
                            requireActivity().findViewById<RelativeLayout>(R.id.mainTopTabsContainer)
                                .visible()
//                            binding.ivSearch.visible()
                            binding.menuDot.visible()
                            binding.tvTitalVideo.text = getString(R.string.videos)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }

                        "selectallid" -> {
                            binding.selectedcontainerVideo.visible()
                            videoAdapter.selectAllItems()
                        }

                        "movetoid" -> {
                            selectionArrayList.clear()
                            selectionArrayList =
                                videoAdapter.selectedItems.map { it.videoPath } as ArrayList<String>

                            activityResultLauncher.launch(
                                Intent(
                                    requireActivity(),
                                    AllPhotosActivity::class.java
                                ).putExtra("from", "Move")
                            )

                            videoAdapter.unselectAllItems()
                            binding.selectedcontainerVideo.gone()
//                            binding.ivSearch.visible()
                            binding.menuDot.visible()
                            binding.tvTitalVideo.text = getString(R.string.videos)
                        }

                        "copytoid" -> {
                            selectionArrayList.clear()
                            selectionArrayList =
                                videoAdapter.selectedItems.map { it.videoPath } as ArrayList<String>

                            activityResultLauncher.launch(
                                Intent(
                                    requireActivity(),
                                    AllPhotosActivity::class.java
                                ).putExtra("from", "Copy")
                            )

                            videoAdapter.unselectAllItems()
                            binding.selectedcontainerVideo.gone()
//                            binding.ivSearch.visible()
                            binding.menuDot.visible()
                            binding.tvTitalVideo.text = getString(R.string.videos)
                        }
                    }
                }
                pictureBottom.show(llMore, 0, 0)
            }

        }
    }

    fun shareMultipleVideoFiles(filePaths: List<VideoModel>, context: Context) {
        val fileUris = ArrayList<Uri>()

        filePaths.forEach { video ->
            try {
                var uri = Uri.parse(video.videoPath)

                val finalUri = when {
                    // If it's already a content URI (e.g., from MediaStore)
                    uri.scheme == "content" -> uri

                    // Android 7-9 (API 24-28): Nougat, Oreo, Pie
                    Build.VERSION.SDK_INT <= Build.VERSION_CODES.P -> {
                        val file = File(video.videoPath)
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
                            File(video.videoPath)
                        )
                    }

                    // Android 12-15 (API 31-35+): S, Tiramisu, UpsideDownCake, VanillaIceCream
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                        FileProvider.getUriForFile(
                            context,
                            "${context.packageName}.provider",
                            File(video.videoPath)
                        )
                    }

                    else -> {
                        // Fallback for future versions
                        FileProvider.getUriForFile(
                            context,
                            "${context.packageName}.provider",
                            File(video.videoPath)
                        )
                    }
                }

                if (finalUri != null) {
                    // Grant URI permission
                    context.grantUriPermission(
                        context.packageName,
                        finalUri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                    fileUris.add(finalUri)
                    Log.d("ShareVideos", "✅ Video URI added: $finalUri")
                } else {
                    Log.e("ShareVideos", "⚠️ Null URI for: ${video.videoPath}")
                }
            } catch (e: Exception) {
                Log.e("ShareVideos", "❌ Error processing video URI: ${video.videoPath}", e)
            }
        }

        if (fileUris.isNotEmpty()) {
            // Since this is specifically for videos, we can set a video-specific MIME type
            val shareIntent = Intent(Intent.ACTION_SEND_MULTIPLE).apply {
                type = "video/*" // Default to video type since we're sharing videos

                // Optionally validate MIME types if provided
                val mimeTypes = filePaths.map { "mp4" ?: "video/*" }
                if (mimeTypes.all { it.startsWith("video/") }) {
                    type = "video/*"
                } else {
                    Log.w("ShareVideos", "Some files may not be videos: $mimeTypes")
                }

                putParcelableArrayListExtra(Intent.EXTRA_STREAM, fileUris)

                // Handle flags based on Android version
                flags = when {
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.N -> {
                        Intent.FLAG_GRANT_READ_URI_PERMISSION or
                                Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                    }
                    else -> {
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    }
                }

                // Optional: Add extra metadata for video sharing
                putExtra(Intent.EXTRA_TITLE, "Shared Videos")
            }

            try {
                val chooserIntent = Intent.createChooser(shareIntent, "Share Videos")
                if (shareIntent.resolveActivity(context.packageManager) != null) {
                    context.startActivity(chooserIntent)
                } else {
                    Log.e("ShareVideos", "❌ No activity found to handle video share intent")
                    // Optional: Show a toast or notification to the user
                    if (context is Activity) {
                        Toast.makeText(context, "No app available to share videos", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Log.e("ShareVideos", "❌ Error starting video share intent", e)
            }
        } else {
            Log.e("ShareVideos", "❌ No valid video files to share")
            // Optional: Notify user
            if (context is Activity) {
                Toast.makeText(context, "No videos available to share", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun createRecycleBin(): File {
        val recycleBin = File(requireActivity().getExternalFilesDir(null), ".gallery_recycleBin")
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
            moveToRecycleBinLegacy(originalFilePath, requireActivity())
        }
    }


    fun moveToRecycleBinScopedStorage(originalFilePath: String): Boolean {
        val context = requireActivity().applicationContext
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
            else -> requireActivity().contentResolver.getType(uri) ?: "application/octet-stream"
        }

        return Pair(fileName, mimeType)
    }

    // Enhanced MediaStore URI finder for all media types
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
        val cursor = requireActivity().contentResolver.query(uri, null, null, null, null)
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

    override fun onResume() {
        super.onResume()
        viewModel.loadAllVideos()
    }

    private fun setupRecyclerView() {
        videoAdapter = VideoAdapter(requireActivity(),object : SelectionModeListener{
            override fun toggleTopBar(show: Boolean) {
                toggleTopBarVisibility(show)
            }

        },{
            VideoMediaStoreSingleton.videoimageList = ArrayList(videoAdapter.currentList)
            VideoMediaStoreSingleton.videoselectedPosition = videoAdapter.currentList.indexOf(it)
            val intent = Intent(requireActivity(), VideoViewPagerActivity::class.java)
            startActivity(intent)
        }, { onLongItemClick ->
            if (onLongItemClick) {
                binding.selectedcontainerVideo.visible()
//                binding.ivSearch.gone()
                binding.menuDot.gone()

            } else {
                binding.tvTitalVideo.text = getString(R.string.videos)
//                binding.ivSearch.visible()
                binding.menuDot.visible()
                binding.selectedcontainerVideo.gone()
            }
        })
        binding.recyclerViewVideos.apply {
            layoutManager = GridLayoutManager(requireContext(), 4)
            adapter = videoAdapter
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun observeViewModel() {
        lifecycleScope.launch {
            getMediaDatabase(requireActivity()).favouriteMediaDao().getAllMediaLive()
                .observe(requireActivity()) {
                    favouriteList.clear()
                    favouriteList.addAll(it)
                    favouriteList.forEach { media ->
                        media.isFav =
                            favouriteList.find { it.mediaId == media.mediaId }?.isFav == true
                    }
                    videoAdapter.notifyDataSetChanged()
                }
        }

        viewModel.videosLiveData.observe(viewLifecycleOwner) { videos ->
            mediaListCheck.clear()
            if (videos.isEmpty()) {
                binding.tvDataNotFound.visible()
                binding.recyclerViewVideos.gone()
                binding.menuDot.gone()
            } else {
                binding.tvDataNotFound.gone()
                binding.recyclerViewVideos.visible()
                binding.menuDot.visible()
            }

            videos.forEach { media ->
                media.isFav = favouriteList.find { it.mediaId == media.videoId }?.isFav == true
            }
            mediaListCheck.addAll(videos)

            videoAdapter.submitList(videos)
        }
    }
    var mediaListCheck: ArrayList<VideoModel> = arrayListOf()

}