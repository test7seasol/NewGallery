package com.gallery.photos.editpic.Fragment

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.app.ProgressDialog
import android.content.BroadcastReceiver
import android.content.ContentUris
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
import androidx.activity.result.contract.ActivityResultContracts
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


                movedialog = SMCopyMoveBottomSheetDialog(
                    requireActivity(), selectList.size.toString(), folderName
                ) {
                    if (!hasAllFilesAccessAs(requireActivity())) {
                        (getString(R.string.all_files_access_required)).tos(requireActivity())
                        AllFilesAccessDialog(requireActivity()) {

                        }
//                    startActivityWithBundle<AllFilePermissionActivity>(Bundle().apply {
//                        putString("isFrom", "Activitys")
//                    })
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
                // Handle the result here
                Log.d("ActivityResult", "Result received successfully: " + selectList.size)

            } else {
                Log.d("ActivityResult", "Result canceled or failed")
            }
        }


    fun copyFiles(list: ArrayList<MediaModelItem>, fromWhere: String, folderName: String) {
        progressDialog?.show()

        CoroutineScope(Dispatchers.IO).launch {
            var successCount = 0

            list.forEach { mediaItem ->
                try {
                    val sourceFile = File(mediaItem.path).takeIf { it.exists() } ?: run {
                        Log.e("FileOperation", "Source file not found: ${mediaItem.path}")
                        return@forEach
                    }

                    // Version-specific path handling
                    val basePath = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        getFolderPathByBucketId(mediaItem.bucketId.toString())
                    } else {
                        // Android 7-9 fallback - use more reliable method
                        getLegacyFolderPath(mediaItem.bucketId.toString())
                    } ?: (Environment.getExternalStorageDirectory().path + "/MyGalleryApp")

                    val destinationFolder = File(basePath, folderName).apply {
                        if (!exists() && !mkdirs()) {
                            Log.e("FileOperation", "Failed to create folder: $absolutePath")
                            return@forEach
                        }
                    }

                    // Rest of your copy/move logic...

                } catch (e: Exception) {
                    Log.e("FileOperation", "Error processing ${mediaItem.path}", e)
                }
            }

            withContext(Dispatchers.Main) {
                progressDialog?.dismiss()
                // Update UI...
            }
        }
    }

    // Fallback method for Android 7-9
    private fun getLegacyFolderPath(bucketId: String): String? {
        val projection = arrayOf(
            MediaStore.Images.Media.DATA
        )

        return requireActivity().contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            "${MediaStore.Images.Media.BUCKET_ID} = ?",
            arrayOf(bucketId),
            null
        )?.use { cursor ->
            if (cursor.moveToFirst()) {
                cursor.getString(0)?.substringBeforeLast("/")
            } else {
                null
            }
        }
    }

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
                val selectBottomview = AlbumsBottomPopup(requireActivity()) {
                    when (it) {
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

            CreateNewFolderDialog(
                    requireActivity(), initialPath = Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DCIM
                    ).path, isFromWhere = "CreateFolder"
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
            binding.recyclerViewAlbums.scrollToPosition(0)
            folderAdapter!!.submitList(folders)
        }
    }

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
