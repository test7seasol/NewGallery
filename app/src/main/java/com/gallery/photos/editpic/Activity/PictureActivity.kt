package com.gallery.photos.editpic.Activity

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.ContentUris
import android.content.Intent
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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
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
import com.gallery.photos.editpic.Extensions.name.getMediaDatabase
import com.gallery.photos.editpic.Extensions.notifyGalleryRoot
import com.gallery.photos.editpic.Extensions.onClick
import com.gallery.photos.editpic.Extensions.setLanguageCode
import com.gallery.photos.editpic.Extensions.shareMultipleFiles
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


            llDelete.onClick {
                if (!hasAllFilesAccessAs(this@PictureActivity)) {
                    (getString(R.string.all_files_access_required)).tos(this@PictureActivity)
                    AllFilesAccessDialog(this@PictureActivity) {

                    }
//                    startActivityWithBundle<AllFilePermissionActivity>(Bundle().apply {
//                        putString("isFrom", "Activitys")
//                    })
                    return@onClick
                }

                val selectedFiles = pictureAdapter!!.selectedItems.distinctBy { it.mediaPath }
                if (selectedFiles.size <= 100) {
                    DeleteWithRememberDialog(this@PictureActivity, false) {
                        CoroutineScope(Dispatchers.Main).launch {
                            // Show Progress Dialog
                            val progressDialog = ProgressDialog(this@PictureActivity)
                            progressDialog.setMessage(getString(R.string.deleting_files))
                            progressDialog.setCancelable(false)
                            progressDialog.show()

                            withContext(Dispatchers.IO) {
                                val deletionJobs = pictureAdapter!!.selectedItems.map {
                                    async {
                                        deleteMediaModel!!.apply {
                                            mediaId = it.mediaId
                                            mediaName = it.mediaName
                                            mediaPath = it.mediaPath
                                            mediaMimeType = it.mediaMimeType
                                            mediaDateAdded = it.mediaDateAdded
                                            isVideo = it.isVideo
                                            displayDate = it.displayDate
                                            isSelect = it.isSelect
                                        }
                                        moveToRecycleBin(deleteMediaModel!!.mediaPath)
                                    }
                                }

                                // Wait for all deletion tasks to complete
                                deletionJobs.awaitAll()
                            }

                            // Dismiss Progress Dialog and update UI
                            progressDialog.dismiss()
                            pictureAdapter!!.deleteSelectedItems()
                            pictureAdapter!!.unselectAllItems()
                            binding.tvAlbumName.text =
                                intent.getStringExtra("folderName") ?: getString(R.string.photos)
                        }
                    }
                } else {
                    (getString(R.string.max_selection_limit_is_100)).tos(this@PictureActivity)
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
            moveToRecycleBinLegacy(originalFilePath)
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
    fun moveToRecycleBinLegacy(originalFilePath: String): Boolean {
        val originalFile = File(originalFilePath)
        if (!originalFile.exists()) {
            Log.e("MoveToRecycleBin", "File does not exist: $originalFilePath")
            return false
        }

        val recycleBin = createRecycleBin()
        val recycledFile = File(recycleBin, originalFile.name)

        return try {
            originalFile.copyTo(recycledFile, overwrite = true)  // Copy to recycle bin
            Log.d("MoveToRecycleBin", "File copied to recycle bin: ${recycledFile.absolutePath}")

            if (originalFile.delete()) {
                Log.d("MoveToRecycleBin", "Original file deleted")
            } else {
                Log.e("MoveToRecycleBin", "Failed to delete original file")
            }

            true
        } catch (e: IOException) {
            Log.e("MoveToRecycleBin", "IOException: ${e.message}")
            e.printStackTrace()
            false
        }
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