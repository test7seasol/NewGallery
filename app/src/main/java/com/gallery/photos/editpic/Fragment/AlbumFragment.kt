package com.gallery.photos.editpic.Fragment

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.app.ProgressDialog
import android.content.BroadcastReceiver
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.GridLayoutManager
import com.gallery.photos.editpic.Activity.AllPhotosActivity
import com.gallery.photos.editpic.Adapter.FolderAdapter
import com.gallery.photos.editpic.Dialogs.AllFilesAccessDialog
import com.gallery.photos.editpic.Dialogs.CreateNewFolderDialog
import com.gallery.photos.editpic.Dialogs.DeleteWithRememberDialog
import com.gallery.photos.editpic.Dialogs.SMCopyMoveBottomSheetDialog
import com.gallery.photos.editpic.Extensions.formatDate
import com.gallery.photos.editpic.Extensions.fromJSON
import com.gallery.photos.editpic.Extensions.gone
import com.gallery.photos.editpic.Extensions.hasAllFilesAccessAs
import com.gallery.photos.editpic.Extensions.invisible
import com.gallery.photos.editpic.Extensions.log
import com.gallery.photos.editpic.Extensions.name.getMediaDatabase
import com.gallery.photos.editpic.Extensions.onClick
import com.gallery.photos.editpic.Extensions.tos
import com.gallery.photos.editpic.Extensions.visible
import com.gallery.photos.editpic.Model.DeleteMediaModel
import com.gallery.photos.editpic.Model.FolderModel
import com.gallery.photos.editpic.Model.MediaModelItem
import com.gallery.photos.editpic.PopupDialog.AlbumsBottomPopup
import com.gallery.photos.editpic.PopupDialog.TopMenuAlbumsCustomPopup
import com.gallery.photos.editpic.R
import com.gallery.photos.editpic.RoomDB.Dao.DeleteMediaDao
import com.gallery.photos.editpic.ViewModel.AlbumViewModel
import com.gallery.photos.editpic.databinding.FragmentAlbumBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.net.URLConnection

class AlbumFragment : Fragment() {

    private lateinit var movedialog: SMCopyMoveBottomSheetDialog
    private var folderName: String = ""
    private var _binding: FragmentAlbumBinding? = null
    private val binding get() = _binding!!

    private val mediaViewModel: AlbumViewModel by viewModels()
    private var folderAdapter: FolderAdapter? = null
    var deleteMediaDao: DeleteMediaDao? = null
    var deleteMediaModel: DeleteMediaModel? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAlbumBinding.inflate(inflater, container, false)
        return binding.root
    }

    var progressDialog: ProgressDialog? = null

    var activityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data = result.data
                val selectedlist = data?.extras?.getString("selectedlist")
                val selectList = selectedlist!!.fromJSON<ArrayList<MediaModelItem>>()

                if (File(folderName).mkdirs()) {
                    ("Folder Created DONE").log()
                } else {
                    ("Folder Not Created DONE").log()
                }
                // Check if folder already exists
                val folderExists = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    // For Android 10+, check using MediaStore
                    checkFolderExistsMediaStore(folderName)
                } else {
                    // For older versions, check using File API
                    checkFolderExistsLegacy(folderName)
                }

                if (folderExists) {
                    Toast.makeText(
                        requireContext(),
                        "Folder '$folderName' already exists",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@registerForActivityResult
                }

                movedialog = SMCopyMoveBottomSheetDialog(
                    requireActivity(), selectList.size.toString(), folderName
                ) {
                    if (!hasAllFilesAccessAs(requireActivity())) {
                        (getString(R.string.all_files_access_required)).tos(requireActivity())
                        AllFilesAccessDialog(requireActivity()) {}
                        return@SMCopyMoveBottomSheetDialog
                    }

                    when (it) {
                        "tvCopy" -> {
                            copyFiles(selectList, "Copy", folderName)
                            movedialog.onDismissDialog()
                        }
                        "tvMove" -> {
                            copyFiles(selectList, "Move", folderName)
                            movedialog.onDismissDialog()
                        }
                    }
                }
                Log.d("ActivityResult", "Result received successfully: " + selectList.size)
            } else {
                Log.d("ActivityResult", "Result canceled or failed")
            }
        }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun checkFolderExistsMediaStore(folderName: String): Boolean {
        val collection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        val projection = arrayOf(MediaStore.Images.Media.RELATIVE_PATH)
        val selection = "${MediaStore.Images.Media.RELATIVE_PATH} = ?"
        val selectionArgs = arrayOf("Pictures/$folderName/")

        return requireActivity().contentResolver.query(
            collection,
            projection,
            selection,
            selectionArgs,
            null
        )?.use { cursor ->
            cursor.count > 0
        } ?: false
    }

    private fun checkFolderExistsLegacy(folderName: String): Boolean {
        val picturesDir =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        val folder = File(picturesDir, folderName)
        return folder.exists() && folder.isDirectory
    }


    fun copyFiles(list: ArrayList<MediaModelItem>, fromWhere: String, folderName: String) {
        progressDialog?.show()

        CoroutineScope(Dispatchers.IO).launch {
            var successCount = 0
            val totalFiles = list.size

            list.forEachIndexed { index, mediaItem ->
                try {
                    val result = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        copyFileAndroid10Plus(requireContext(), mediaItem, folderName, fromWhere)
                    } else {
                        copyFileLegacy(requireContext(), mediaItem, folderName, fromWhere)
                    }

                    if (result) successCount++

                    withContext(Dispatchers.Main) {
                        progressDialog?.setMessage("Processing ${index + 1}/$totalFiles")
                    }
                } catch (e: Exception) {
                    Log.e("FileCopy", "Error copying ${mediaItem.path}", e)
                }
            }

            withContext(Dispatchers.Main) {
                progressDialog?.dismiss()
                val message = when (fromWhere) {
                    "Copy" -> "Copied $successCount/$totalFiles files to $folderName"
                    "Move" -> "Moved $successCount/$totalFiles files to $folderName"
                    else -> "Processed $successCount/$totalFiles files"
                }
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                mediaViewModel.refreshFolders()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private suspend fun copyFileAndroid10Plus(
        context: Context,
        mediaItem: MediaModelItem,
        folderName: String,
        operation: String
    ): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val sourceFile =
                    File(mediaItem.path).takeIf { it.exists() } ?: return@withContext false
                val mimeType = getMimeType(sourceFile.path)

                val collection = when {
                    mimeType?.startsWith("video/") == true -> MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                    else -> MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                }

                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, sourceFile.name)
                    put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
                    put(
                        MediaStore.MediaColumns.RELATIVE_PATH,
                        "${Environment.DIRECTORY_PICTURES}/$folderName"
                    )
                    put(MediaStore.MediaColumns.IS_PENDING, 1)
                }

                val uri = context.contentResolver.insert(collection, contentValues)
                    ?: return@withContext false

                context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                    sourceFile.inputStream().use { inputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }

                contentValues.clear()
                contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
                context.contentResolver.update(uri, contentValues, null, null)

                if (operation == "Move") {
                    // For move operation, we need to delete the original file
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        // Android 11+ needs special handling
                        try {
                            // First try direct deletion (might work with MANAGE_EXTERNAL_STORAGE)
                            val deleted = context.contentResolver.delete(
                                ContentUris.withAppendedId(collection, mediaItem.bucketId),
                                null,
                                null
                            ) > 0

                            if (!deleted) {
                                // Fallback to MediaStore delete request
                                val pendingIntent = MediaStore.createDeleteRequest(
                                    context.contentResolver,
                                    listOf(
                                        ContentUris.withAppendedId(
                                            collection,
                                            mediaItem.bucketId
                                        )
                                    )
                                )
                                requireActivity().startIntentSenderForResult(
                                    pendingIntent.intentSender,
                                    REQUEST_CODE_DELETE,
                                    null,
                                    0,
                                    0,
                                    0,
                                    null
                                )
                                // Note: Actual deletion happens after user confirms
                                return@withContext true
                            }
                        } catch (e: SecurityException) {
                            Log.e("FileMove", "Permission denied, trying alternative approach", e)
                            // Fallback to legacy method if permission denied
                            if (sourceFile.delete()) {
                                MediaScannerConnection.scanFile(
                                    context,
                                    arrayOf(sourceFile.path),
                                    null,
                                    null
                                )
                                return@withContext true
                            }
                            return@withContext false
                        }
                    } else {
                        // Android 10 (Q)
                        if (sourceFile.delete()) {
                            MediaScannerConnection.scanFile(
                                context,
                                arrayOf(sourceFile.path),
                                null,
                                null
                            )
                            return@withContext true
                        }
                        return@withContext false
                    }
                }

                return@withContext true
            } catch (e: Exception) {
                Log.e("FileCopy", "Android 10+ $operation failed", e)
                false
            }
        }
    }

    private fun copyFileLegacy(
        context: Context,
        mediaItem: MediaModelItem,
        folderName: String,
        operation: String
    ): Boolean {
        return try {
            val sourceFile = File(mediaItem.path).takeIf { it.exists() } ?: return false
            val picturesDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val destFolder = File(picturesDir, folderName).apply { mkdirs() }

            if (!destFolder.exists()) return false

            val destFile = File(destFolder, sourceFile.name)

            if (operation == "Copy") {
                sourceFile.copyTo(destFile, overwrite = true)
            } else { // Move
                if (!sourceFile.renameTo(destFile)) {
                    // If rename fails, try copy + delete
                    sourceFile.copyTo(destFile, overwrite = true)
                    if (!sourceFile.delete()) {
                        Log.e("FileMove", "Failed to delete source file after copy")
                        return false
                    }
                }
            }

            // Notify media scanner for both copy and move operations
            MediaScannerConnection.scanFile(
                context,
                arrayOf(destFile.absolutePath),
                arrayOf(getMimeType(sourceFile.path)),
                null
            )

            // For move operation, also notify about source file deletion
            if (operation == "Move") {
                MediaScannerConnection.scanFile(
                    context,
                    arrayOf(sourceFile.absolutePath),
                    null,
                    null
                )
            }

            true
        } catch (e: Exception) {
            Log.e("FileCopy", "Legacy $operation failed", e)
            false
        }
    }

    companion object {
        private const val REQUEST_CODE_DELETE = 1001  // For handling delete request result
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
            else -> URLConnection.guessContentTypeFromName(path) ?: "*/*"
        }
    }

    // Fallback method for Android 7-9

    fun logAllBucketIds() {
        val projection = arrayOf(
            MediaStore.Images.Media.BUCKET_ID, MediaStore.Images.Media.BUCKET_DISPLAY_NAME
        )

        val cursor = requireActivity().contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null, null, null
        )

        cursor?.use {
            while (it.moveToNext()) {
                val bucketId =
                    it.getString(it.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_ID))
                val bucketName =
                    it.getString(it.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME))
                Log.d("BucketDebug", "BucketId: $bucketId -> $bucketName")
            }
        }
    }

    fun getFolderPathByBucketId(bucketId: String): String? {
        val projection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Android 10+ (Q) can use relative_path
            arrayOf(
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.RELATIVE_PATH
            )
        } else {
            // Android 7-9 (Nougat-Oreo-Pie) fallback
            arrayOf(
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Images.Media.DATA
            )
        }

        val selection = "${MediaStore.Images.Media.BUCKET_ID} = ? AND " +
                "(is_drm = 0 OR is_drm IS NULL)"
        val selectionArgs = arrayOf(bucketId)

        return requireActivity().contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            null
        )?.use { cursor ->
            if (cursor.moveToFirst()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    // Try to use relative_path first
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.RELATIVE_PATH))
                        ?.let { Environment.getExternalStorageDirectory().path + "/" + it }
                        ?: cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA))
                            ?.substringBeforeLast("/")
                } else {
                    // Android 7-9 fallback - use DATA column only
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA))
                        ?.substringBeforeLast("/")
                }
            } else {
                null
            }
        }
    }
//    fun getFolderPathByBucketId(bucketId: String): String? {
//        Log.d("BucketDebug", "Searching for bucketId: $bucketId") // Log bucketId for debugging
//
//        val projection = arrayOf(
//            MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
//            MediaStore.Images.Media.DATA,
//            MediaStore.Images.Media.RELATIVE_PATH
//        )
//
//        val selection = "${MediaStore.Images.Media.BUCKET_ID} = ?"
//        val selectionArgs = arrayOf(bucketId)
//
//        requireActivity().contentResolver.query(
//            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, selection, selectionArgs, null
//        )?.use { cursor ->
//            if (cursor.moveToFirst()) {
//                val bucketName =
//                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME))
//                Log.d("BucketDebug", "Found folder: $bucketName")
//
//                return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
//                    val dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
//                    File(cursor.getString(dataColumn)).parent // Returns actual file directory
//                } else {
//                    val relativePathColumn =
//                        cursor.getColumnIndexOrThrow(MediaStore.Images.Media.RELATIVE_PATH)
//                    val fullPath = "${Environment.getExternalStorageDirectory()}/${
//                        cursor.getString(relativePathColumn)
//                    }".removeSuffix("/")
//                    Log.d("BucketDebug", "Resolved path: $fullPath")
//                    fullPath
//                }
//            } else {
//                Log.e("BucketDebug", "No folder found for bucketId: $bucketId")
//            }
//        }
//
////        logAllBucketIds()
//
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


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progressDialog = ProgressDialog(requireActivity()).apply {
            setMessage(getString(R.string.processing_files))
            setCancelable(false)
        }
        deleteMediaModel = DeleteMediaModel()
        deleteMediaDao = getMediaDatabase(requireActivity()).deleteMediaDao()

        setupRecyclerView()
        observeFolders()
    }

    fun refreshFolder() {
        Log.d("NewAlbum", "Resfrehs Folder")
        mediaViewModel.refreshFolders()  // Refresh folders when user returns
    }

    override fun onResume() {
        super.onResume()
        ("onResume Refres hFolder").log()
        mediaViewModel.refreshFolders()  // Refresh folders when user returns
        val intentFilter = IntentFilter("com.example.FOLDER_CREATED")
        LocalBroadcastManager.getInstance(requireContext())
            .registerReceiver(folderCreatedReceiver, intentFilter)
    }

    private val folderCreatedReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            mediaViewModel.refreshFolders()
            setupRecyclerView()

        }
    }

    override fun onPause() {
        super.onPause()
        LocalBroadcastManager.getInstance(requireContext())
            .unregisterReceiver(folderCreatedReceiver)
    }

    @SuppressLint("SetTextI18n")
    private fun setupRecyclerView() {

        binding.recyclerViewAlbums.apply {
            layoutManager = GridLayoutManager(requireContext(), 3)
            folderAdapter = FolderAdapter(requireActivity(), onLongItemClick = {
                if (it) {
                    binding.selectedcontaineralbumsid.visible()
                    requireActivity().findViewById<RelativeLayout>(R.id.mainTopTabsContainer).gone()
                    binding.searchiconid.invisible()
                    binding.createFolder.gone()
                    binding.menuthreeid.gone()
                } else {
                    binding.selectedcontaineralbumsid.gone()
                    requireActivity().findViewById<RelativeLayout>(R.id.mainTopTabsContainer)
                        .visible()
//                    binding.searchiconid.visible()
                    binding.createFolder.visible()
                    binding.menuthreeid.visible()
                    binding.tvAlbumeTitle.text = getString(R.string.albums)
                }
            })
            adapter = folderAdapter
        }

        binding.apply {
            llMore.onClick {
                val isSelectAll = (folderAdapter?.selectedItems?.size != albumelist.size)

                val selectBottomview = AlbumsBottomPopup(requireActivity(), isSelectAll) {
                    when (it) {
                        "deselectall" -> {
                            folderAdapter!!.unselectAllItems()
                            binding.selectedcontaineralbumsid.gone()
                            binding.createFolder.visible()
                            binding.searchiconid.invisible()
                            binding.menuthreeid.visible()

                            if (folderAdapter!!.selectedItems.isEmpty()) {
                                binding.createFolder.gone()
                                binding.menuthreeid.gone()
                                binding.selectedcontaineralbumsid.visible()
                            }
                        }
                        "selectallid" -> {
                            folderAdapter!!.selectAllItems()
                            binding.selectedcontaineralbumsid.visible()
                            binding.createFolder.gone()
                            binding.searchiconid.invisible()
                            binding.menuthreeid.gone()

                            if (folderAdapter!!.selectedItems.isEmpty()) {
                                binding.createFolder.visible()
                                binding.menuthreeid.visible()
                                binding.selectedcontaineralbumsid.gone()
                            }
                        }
                    }
                }
                selectBottomview.show(llMore, 0, 0)
            }
            menuthreeid.onClick {

                val topAlbumDialog = TopMenuAlbumsCustomPopup(requireActivity()) {
                    folderAdapter!!.selectAllItems()
                    binding.selectedcontaineralbumsid.visible()
                    binding.createFolder.gone()
                    binding.searchiconid.invisible()
                    binding.menuthreeid.gone()
                }
                topAlbumDialog.show(menuthreeid, 0, 0)
            }

            llDelete.onClick {

                if (!hasAllFilesAccessAs(requireActivity())) {
                    (getString(R.string.all_files_access_required)).tos(requireActivity())
                    AllFilesAccessDialog(requireActivity()) {

                    }
//                    startActivityWithBundle<AllFilePermissionActivity>(Bundle().apply {
//                        putString("isFrom", "Activitys")
//                    })
                    return@onClick
                }

                DeleteWithRememberDialog(requireActivity(), false) {
                    progressDialog?.show()
                    lifecycleScope.launch {
                        withContext(Dispatchers.IO) {
                            folderAdapter!!.selectedItems.forEach {
                                moveMediaByBucketId(it.bucketId)
                            }
                        }
                        binding.selectedcontaineralbumsid.gone()
                        requireActivity().findViewById<RelativeLayout>(R.id.mainTopTabsContainer)
                            .visible()
//                        binding.searchiconid.visible()
                        binding.createFolder.visible()
                        binding.menuthreeid.visible()
                        binding.tvAlbumeTitle.text = getString(R.string.albums)
                        folderAdapter!!.selectedItems.clear()
                        folderAdapter!!.notifyDataSetChanged()
                        progressDialog?.dismiss()
                    }
                }
            }

            createFolder.onClick {

            CreateNewFolderDialog(requireActivity(),
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).path,
                isFromWhere = "CreateFolder"
                ) {
                    requireActivity().findViewById<RelativeLayout>(R.id.mainTopTabsContainer)
                        .visible()
                    requireActivity().findViewById<RelativeLayout>(R.id.footer).visible()

                    folderName = it
                    activityResultLauncher.launch(
                        Intent(
                            requireActivity(), AllPhotosActivity::class.java
                        ).putExtra("from", "CreateNew")
                    )
                }
            }
        }
    }

    fun moveMediaByBucketId(bucketId: String) {
        val imageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val videoUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI

        // Common projection for all Android versions
        val projection = arrayOf(
            MediaStore.MediaColumns._ID,
            MediaStore.MediaColumns.DISPLAY_NAME,
            MediaStore.MediaColumns.DATA,
            MediaStore.MediaColumns.MIME_TYPE,
            MediaStore.MediaColumns.SIZE,
            MediaStore.MediaColumns.DATE_ADDED,
            MediaStore.MediaColumns.BUCKET_ID
        )

        // Selection criteria - works on all versions
        val selection = "${MediaStore.MediaColumns.BUCKET_ID} = ?"
        val selectionArgs = arrayOf(bucketId)

        val mediaList = mutableListOf<DeleteMediaModel>()

        // Helper function to process cursor results
        fun processCursor(cursor: Cursor, isVideo: Boolean) {
            val idIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID)
            val nameIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME)
            val pathIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
            val mimeTypeIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.MIME_TYPE)
            val sizeIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.SIZE)
            val dateAddedIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_ADDED)

            while (cursor.moveToNext()) {
                try {
                    mediaList.add(
                        DeleteMediaModel(
                            mediaId = cursor.getLong(idIndex),
                            mediaName = cursor.getString(nameIndex),
                            mediaPath = cursor.getString(pathIndex) ?: continue, // Skip if no path
                            mediaMimeType = cursor.getString(mimeTypeIndex),
                            mediaSize = cursor.getLong(sizeIndex),
                            mediaDateAdded = cursor.getLong(dateAddedIndex),
                            isVideo = isVideo,
                            displayDate = formatDate(cursor.getLong(dateAddedIndex))
                        )
                    )
                } catch (e: Exception) {
                    Log.e("MediaProcessing", "Error processing media item", e)
                }
            }
        }

        // Process images
        requireActivity().contentResolver.query(
            imageUri, projection, selection, selectionArgs, null
        )?.use { cursor ->
            processCursor(cursor, false)
        }

        // Process videos
        requireActivity().contentResolver.query(
            videoUri, projection, selection, selectionArgs, null
        )?.use { cursor ->
            processCursor(cursor, true)
        }

        // Move files to Recycle Bin in background thread
        CoroutineScope(Dispatchers.IO).launch {
            mediaList.forEach { media ->
                try {
                    if (moveToRecycleBin(media)) {
                        // After moving to recycle bin, delete from MediaStore
                        deleteFromMediaStore(media)
                    }
                } catch (e: Exception) {
                    Log.e("MediaMove", "Error moving media ${media.mediaPath}", e)
                }
            }

            // Refresh UI
            withContext(Dispatchers.Main) {
                refreshAlbums()
            }
        }
    }

    // Helper function to delete from MediaStore
// Replace the deleteFromMediaStore function with this corrected version
    private fun deleteFromMediaStore(media: DeleteMediaModel) {
        val contentUri = if (media.isVideo) {
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        } else {
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }

        try {
            // First delete from MediaStore
            val rowsDeleted = requireActivity().contentResolver.delete(
                ContentUris.withAppendedId(contentUri, media.mediaId),
                null,
                null
            )

            if (rowsDeleted > 0) {
                Log.d("MediaDelete", "Successfully deleted from MediaStore: ${media.mediaPath}")

                // Then refresh MediaStore (different approaches for different API levels)
                refreshMediaStore(media.mediaPath)
            } else {
                Log.e("MediaDelete", "Failed to delete from MediaStore: ${media.mediaPath}")
            }
        } catch (e: SecurityException) {
            Log.e("MediaDelete", "Permission denied for MediaStore delete", e)
            // Handle Android 10+ scoped storage permission issues
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                try {
                    // Alternative approach for Android 10+
                    refreshMediaStore(media.mediaPath)
                } catch (e: Exception) {
                    Log.e("MediaDelete", "Alternative delete failed", e)
                }
            }
        }
    }

    // New helper function to refresh MediaStore
    private fun refreshMediaStore(filePath: String) {
        val mediaFile = File(filePath)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            // For Android 9 and below - use broadcast
            val intent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
            intent.data = Uri.fromFile(mediaFile)
            requireActivity().sendBroadcast(intent)
        } else {
            // For Android 10 and above - use MediaScannerConnection
            MediaScannerConnection.scanFile(
                requireContext(),
                arrayOf(filePath),
                null
            ) { path, uri ->
                Log.d("MediaScan", "Scanned $path -> $uri")
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

    fun moveToRecycleBin(media: DeleteMediaModel): Boolean {
        val originalFile = File(media.mediaPath)
        if (!originalFile.exists()) {
            Log.e("MoveToRecycleBin", "File does not exist: ${media.mediaPath}")
            return false
        }

        val recycleBin = createRecycleBin()
        val recycledFile = File(recycleBin, originalFile.name)

        return try {
            Log.d("MoveToRecycleBin", "Moving file to recycle bin: ${originalFile.absolutePath}")

            originalFile.copyTo(recycledFile, overwrite = true)
            Log.d("MoveToRecycleBin", "File copied to recycle bin: ${recycledFile.absolutePath}")

            if (originalFile.delete()) {
                Log.d("MoveToRecycleBin", "Original file deleted: ${originalFile.absolutePath}")
            } else {
                Log.e(
                    "MoveToRecycleBin",
                    "Failed to delete original file: ${originalFile.absolutePath}"
                )
            }

            // Insert into Room Database
            CoroutineScope(Dispatchers.IO).launch {
                val deleteMediaModel = media.copy(binPath = recycledFile.absolutePath)
                deleteMediaDao!!.insertMedia(deleteMediaModel)
                Log.d(
                    "MoveToRecycleBin", "Inserted into Room database: ${recycledFile.absolutePath}"
                )
            }

            true
        } catch (e: IOException) {
            Log.e("MoveToRecycleBin", "IOException occurred: ${e.message}")
            e.printStackTrace()
            false
        }
    }

    // Function to refresh the album list
    fun refreshAlbums() {
        Log.d("DeleteMedia", "Refreshing album list...")
        mediaViewModel.refreshFolders()  // Refresh folders when user returns
    }


    fun observeFolders() {
        mediaViewModel.folderLiveData.observe(viewLifecycleOwner) { folders ->
            albumelist.clear()
            albumelist.addAll(folders)
            binding.recyclerViewAlbums.scrollToPosition(0)
            folderAdapter!!.submitList(folders)
        }
    }

    var albumelist: ArrayList<FolderModel> = arrayListOf()

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Avoid memory leaks
    }

    fun toggleTopBarVisibility(isVisible: Boolean) {
        binding.rlTop.visibility = View.VISIBLE
        if (isVisible) {
//            binding.searchiconid.invisible()
            binding.createFolder.invisible()
            binding.menuthreeid.invisible()
        } else {
//            binding.searchiconid.visible()
            binding.createFolder.visible()
            binding.menuthreeid.visible()
            binding.selectedcontaineralbumsid.gone()
        }
    }
}
