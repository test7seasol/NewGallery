package com.gallery.photos.editpic.Activity

import CreateNewFolderDialog
import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.gallery.photos.editpic.Adapter.PictureAdapter
import com.gallery.photos.editpic.Dialogs.DeleteWithRememberDialog
import com.gallery.photos.editpic.Dialogs.SlideShowDialog
import com.gallery.photos.editpic.Extensions.gone
import com.gallery.photos.editpic.Extensions.handleBackPress
import com.gallery.photos.editpic.Extensions.log
import com.gallery.photos.editpic.Extensions.name.getMediaDatabase
import com.gallery.photos.editpic.Extensions.notifyGalleryRoot
import com.gallery.photos.editpic.Extensions.onClick
import com.gallery.photos.editpic.Extensions.shareMultipleFiles
import com.gallery.photos.editpic.Extensions.tos
import com.gallery.photos.editpic.Extensions.visible
import com.gallery.photos.editpic.Model.DeleteMediaModel
import com.gallery.photos.editpic.Model.FavouriteMediaModel
import com.gallery.photos.editpic.Model.MediaModel
import com.gallery.photos.editpic.PopupDialog.PicturesBottomPopup
import com.gallery.photos.editpic.PopupDialog.TopMenuPicturesCustomPopup
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
        } else {
            binding.picturesselectedcontainerid.gone()
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

                var isFromMy = (data.extras?.getString("where")!!)

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
                    ("Album created successfully").tos(this)
                }
            } else {
                Log.d("ActivityResult", "Result canceled or failed")
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
            if (it) {
                binding.picturesselectedcontainerid.visible()
            } else {
                binding.tvAlbumName.text = intent.getStringExtra("folderName") ?: "Photos"
                binding.picturesselectedcontainerid.gone()
            }
        }

        bucketId = intent.getStringExtra("BUCKET_ID") ?: return
        val folderName = intent.getStringExtra("folderName") ?: return

        binding.tvAlbumName.text = folderName
        binding.ivBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        setupRecyclerView()
        observeMedia()

        // Handle item click to open ViewPagerActivity
        pictureAdapter!!.onItemClick = { selectedMedia ->
            openViewPagerActivity(selectedMedia)
        }

        binding.apply {

            llShare.onClick {
                val selectedFiles = pictureAdapter!!.selectedItems.distinctBy { it.mediaPath }
                if (selectedFiles.size <= 100) {
                    shareMultipleFiles(selectedFiles, this@PictureActivity)
                } else {
                    ("Max selection limit is 100").tos(this@PictureActivity)
                }
            }

            llMore.onClick {
                val pictureBottom = PicturesBottomPopup(this@PictureActivity, true) {
                    when (it) {
                        "deselectall" -> {
                            pictureAdapter!!.unselectAllItems()
                            binding.picturesselectedcontainerid.gone()
                            binding.tvAlbumName.text =
                                intent.getStringExtra("folderName") ?: "Photos"
                        }

                        "selectallid" -> {
                            binding.picturesselectedcontainerid.visible()
                            pictureAdapter!!.selectAllItems()
                        }

                        "movetoid" -> {
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
                                intent.getStringExtra("folderName") ?: "Photos"
                            binding.picturesselectedcontainerid.gone()
                        }

                        "copytoid" -> {
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
                                intent.getStringExtra("folderName") ?: "Photos"
                            binding.picturesselectedcontainerid.gone()

                        }
                    }
                }
                pictureBottom.show(llMore, 0, 0)
            }


            llDelete.onClick {
                val selectedFiles = pictureAdapter!!.selectedItems.distinctBy { it.mediaPath }
                if (selectedFiles.size <= 100) {
                    DeleteWithRememberDialog(this@PictureActivity, false) {
                        CoroutineScope(Dispatchers.Main).launch {
                            // Show Progress Dialog
                            val progressDialog = ProgressDialog(this@PictureActivity)
                            progressDialog.setMessage("Deleting files...")
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
                                intent.getStringExtra("folderName") ?: "Photos"
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
        val originalFile = File(originalFilePath)
        if (!originalFile.exists()) {
            Log.e("MoveToRecycleBin", "File does not exist: $originalFilePath")
            return false
        }

        val recycleBin = createRecycleBin()
        val recycledFile = File(recycleBin, originalFile.name)

        return try {
            Log.d("MoveToRecycleBin", "Moving file to recycle bin: ${originalFile.absolutePath}")

            originalFile.copyTo(recycledFile, overwrite = true)  // Copy to recycle bin
            Log.d("MoveToRecycleBin", "File copied to recycle bin: ${recycledFile.absolutePath}")

            if (originalFile.delete()) {
                Log.d("MoveToRecycleBin", "Original file deleted: ${originalFile.absolutePath}")
            } else {
                Log.e(
                    "MoveToRecycleBin",
                    "Failed to delete original file: ${originalFile.absolutePath}"
                )
            }

            CoroutineScope(Dispatchers.IO).launch {

                Log.d("MoveToRecycleBin", "Inserting media record into Room database.")
                deleteMediaModel!!.binPath = recycledFile.absolutePath
//                videoMediaModel!!.randomMediaId = randomMediaId

                deleteMediaDao!!.insertMedia(deleteMediaModel!!)  // Save path for restoration
//                imageList.removeAt(viewpagerselectedPosition)
//                MediaStoreSingleton.imageList.removeAt(viewpagerselectedPosition)
//                requireActivity().runOnUiThread {
////                    binding.viewPager.currentItem = viewpagerselectedPosition + 1
//                }
                Log.d("MoveToRecycleBin", "Media record inserted into Room database.")
            }
            true
        } catch (e: IOException) {
            Log.e("MoveToRecycleBin", "IOException occurred: ${e.message}")
            e.printStackTrace()
            false
        }
    }

    override fun onResume() {
        super.onResume()
        mediaViewModel.loadMedia(intent.getStringExtra("BUCKET_ID") ?: "")
    }

    private fun setupRecyclerView() {
        binding.recyclerViewPictures.adapter = pictureAdapter
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
            ("Picture Act Observer: ${mediaList.size}").log()

            if (mediaList.isEmpty()) finish()

            mediaList.forEach { media ->
                media.isFav = favouriteList.find { it.mediaId == media.mediaId }?.isFav == true
            }
            pictureAdapter!!.submitList(mediaList)
        }
    }

    private fun openViewPagerActivity(selectedMedia: MediaModel) {
        MediaStoreSingleton.imageList = ArrayList(pictureAdapter!!.currentList)
        MediaStoreSingleton.selectedPosition = pictureAdapter!!.currentList.indexOf(selectedMedia)

        val intent = Intent(this, ViewPagerActivity::class.java)
        startActivity(intent)
    }
    private fun openViewPagerSlideShowActivity(
        position: Int, secound: Int
    ) {
        MediaStoreSingleton.imageList = ArrayList(pictureAdapter!!.currentList)
        MediaStoreSingleton.selectedPosition = position
        val intent = Intent(this, ViewPagerActivity::class.java)
        intent.putExtra("slideshow", true)
        intent.putExtra("secoundSlideShow", secound)
        startActivity(intent)
    }
}