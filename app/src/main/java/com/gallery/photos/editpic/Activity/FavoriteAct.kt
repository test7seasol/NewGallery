package com.gallery.photos.editpic.Activity

import CreateNewFolderDialog
import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.ScaleGestureDetector
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.GridLayoutManager
import com.gallery.photos.editpic.Adapter.FavouriteAdapter
import com.gallery.photos.editpic.Dialogs.DeleteWithRememberDialog
import com.gallery.photos.editpic.Dialogs.SlideShowDialog
import com.gallery.photos.editpic.Extensions.PREF_LANGUAGE_CODE
import com.gallery.photos.editpic.Extensions.gone
import com.gallery.photos.editpic.Extensions.handleBackPress
import com.gallery.photos.editpic.Extensions.log
import com.gallery.photos.editpic.Extensions.name.getMediaDatabase
import com.gallery.photos.editpic.Extensions.notifyGalleryRoot
import com.gallery.photos.editpic.Extensions.onClick
import com.gallery.photos.editpic.Extensions.setLanguageCode
import com.gallery.photos.editpic.Extensions.shareMultipleFilesFavourite
import com.gallery.photos.editpic.Extensions.tos
import com.gallery.photos.editpic.Extensions.visible
import com.gallery.photos.editpic.Model.DeleteMediaModel
import com.gallery.photos.editpic.Model.FavouriteMediaModel
import com.gallery.photos.editpic.PopupDialog.FavouriteBottomPopup
import com.gallery.photos.editpic.PopupDialog.TopMenuFavouriteCustomPopup
import com.gallery.photos.editpic.R
import com.gallery.photos.editpic.RoomDB.Dao.DeleteMediaDao
import com.gallery.photos.editpic.RoomDB.Dao.FavouriteMediaDao
import com.gallery.photos.editpic.Utils.FavouriteMediaStoreSingleton
import com.gallery.photos.editpic.Utils.SelectionAlLPhotos.selectionArrayList
import com.gallery.photos.editpic.databinding.ActivityFavoriteBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException

class FavoriteAct : AppCompatActivity() {
    var favadapter: FavouriteAdapter? = null
    lateinit var bind: ActivityFavoriteBinding
    private lateinit var favouriteMediaDao: FavouriteMediaDao
    private var favouriteList: ArrayList<FavouriteMediaModel> = arrayListOf()
    var deleteMediaModel: DeleteMediaModel? = null
    var favouriteMediaModel: FavouriteMediaModel? = null
    var deleteMediaDao: DeleteMediaDao? = null

    lateinit var gridLayoutManager: GridLayoutManager
    private lateinit var scaleGestureDetector: ScaleGestureDetector
    private var currentSpanCount = 4  // Default span count
    private val minSpanCount = 2
    private val maxSpanCount = 6

    fun toggleTopBarVisibility(isVisible: Boolean) {
        bind.rvFavourite.visibility = View.VISIBLE
        ("is Fasvoutrite Visisble: $isVisible").log()
        if (isVisible) {
            bind.selectedcontainerFavouriteid.visible()
        } else {
            bind.selectedcontainerFavouriteid.gone()
//            binding.ivSearch.visible()
            bind.menuFav.visible()
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

                CreateNewFolderDialog(
                    this,
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).path,
                    selectedFiles,
                    isFromWhere = (data.extras?.getString("where")!!)
                ) { newAlbumPath ->
                    Log.d("NewAlbum", "Created new album at $newAlbumPath")
                    notifyGalleryRoot(
                        this,
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).path
                    )
                    val intent = Intent("com.example.FOLDER_CREATED")
                    LocalBroadcastManager.getInstance(this).sendBroadcast(intent)

                    (getString(R.string.album_created_successfully)).tos(this)
                }
            } else {
                Log.d("ActivityResult", "Result canceled or failed")
            }
        }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLanguageCode(this, MyApplicationClass.getString(PREF_LANGUAGE_CODE)!!)
        bind = ActivityFavoriteBinding.inflate(layoutInflater)
        setContentView(bind.root)

        handleBackPress {
            if (bind.tvTitleFavourite.text != getString(R.string.favorites)) {
                favadapter!!.unselectAllItems()
                bind.tvTitleFavourite.text = getString(R.string.favorites)
            } else {
                finish()
            }
        }

        deleteMediaModel = DeleteMediaModel()
        favouriteMediaModel = FavouriteMediaModel()
        deleteMediaDao = getMediaDatabase(this).deleteMediaDao()

        favouriteMediaDao = getMediaDatabase(this@FavoriteAct).favouriteMediaDao()

        favouriteMediaDao.getAllMediaLive().observe(this) { mediaList ->
            // Update your RecyclerView adapter or UI

            favouriteList.clear()

            mediaList.forEach {
                it.isSelect = false
            }

            favouriteList.addAll(
                ArrayList(
                    mediaList
                )
            )

            runOnUiThread {
                if (favouriteList.isNotEmpty()) {
                    bind.rvFavourite.visible()
                    bind.menuFav.visible()
                    bind.tvDataNotFound.gone()
                    bind.rvFavourite.adapter?.notifyDataSetChanged()
                } else {
                    bind.rvFavourite.gone()
                    bind.tvDataNotFound.visible()
                    bind.menuFav.gone()
                }
            }
            Log.d("LiveData", "Media list updated: ${mediaList.size} items")
        }

        setupPinchToZoomGesture()

        favadapter = FavouriteAdapter(this@FavoriteAct, favouriteList) { onLongItemClick ->
            if (onLongItemClick) {
                bind.selectedcontainerFavouriteid.visible()
//                binding.ivSearch.gone()
                bind.menuFav.gone()
            } else {
                bind.tvTitleFavourite.text = getString(R.string.favorites)
//                binding.ivSearch.visible()
                bind.selectedcontainerFavouriteid.gone()
                bind.menuFav.visible()
            }
        }

      /*  val layoutManager = GridLayoutManager(this, 3)
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                // Make headers span all 4 columns, while media items take 1 column
                return if (favadapter!!.getItemViewType(position) == 0) 3 else 3
            }
        }*/

        gridLayoutManager = GridLayoutManager(this, currentSpanCount)
        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (favadapter!!.getItemViewType(position) == 0) currentSpanCount else 3
            }
        }

        bind.rvFavourite.apply {
//            this.layoutManager = gridLayoutManager
            adapter = favadapter
        }


        bind.apply {
            llMore.onClick {
                val pictureBottom = FavouriteBottomPopup(this@FavoriteAct, true) {
                    when (it) {

                        /*    "deselectall" -> {
                                favadapter!!.unselectAllItems()
                                selectedcontainerFavouriteid.gone()
                                searchiconid.visible()
                                menuFav.visible()
                                tvTitleFavourite.text = "Favourite"
                            }*/

                        "llUnFav" -> {
                            unFavSelectedList()
                        }

                        "selectallid" -> {
                            selectedcontainerFavouriteid.visible()
                            favadapter!!.selectAllItems()
                            searchiconid.gone()
                            menuFav.gone()
                        }

                        "movetoid" -> {
                            selectionArrayList.clear()
                            selectionArrayList =
                                favadapter!!.selectedItems.map { it.mediaPath } as ArrayList<String>

                            activityResultLauncher.launch(
                                Intent(
                                    this@FavoriteAct, AllPhotosActivity::class.java
                                ).putExtra("from", "Move")
                            )

                            favadapter!!.unselectAllItems()
                            selectedcontainerFavouriteid.gone()
                            searchiconid.visible()
                            menuFav.visible()
                            tvTitleFavourite.text = getString(R.string.favorites)
                        }

                        "copytoid" -> {
                            selectionArrayList.clear()
                            selectionArrayList =
                                favadapter!!.selectedItems.map { it.mediaPath } as ArrayList<String>

                            activityResultLauncher.launch(
                                Intent(
                                    this@FavoriteAct, AllPhotosActivity::class.java
                                ).putExtra("from", "Copy")
                            )

                            favadapter!!.unselectAllItems()
                            selectedcontainerFavouriteid.gone()
                            searchiconid.visible()
                            menuFav.visible()
                            tvTitleFavourite.text = getString(R.string.favorites)
                        }
                    }
                }
                pictureBottom.show(llMore, 0, 0)
            }

            llUnFav.setOnClickListener {
                unFavSelectedList()
            }


            llShare.onClick {
                val selectedFiles = favadapter!!.selectedItems.distinctBy { it.mediaPath }
                if (selectedFiles.size <= 100) {
                    shareMultipleFilesFavourite(selectedFiles, this@FavoriteAct)
                } else {
                    ("Max selection limit is 100").tos(this@FavoriteAct)
                }
            }

            llDelete.onClick {
                val selectedFiles = favadapter!!.selectedItems.distinctBy { it.mediaPath }

                if (selectedFiles.size <= 100) {
                    DeleteWithRememberDialog(this@FavoriteAct, false) {
                        CoroutineScope(Dispatchers.Main).launch {
                            // Show Progress Dialog
                            val progressDialog = ProgressDialog(this@FavoriteAct).apply {
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
                                            deleteMediaDao!!.insertMedia(deleteMediaModel)  // Insert into Recycle Bin

                                            // ✅ Delete from favorites database
                                            favouriteMediaDao.getMediaById(mediaItem.mediaId)
                                                ?.let { it1 -> favouriteMediaDao!!.deleteMedia(it1) }
                                            Log.d(
                                                "FileDeletion",
                                                "Removed from favorites: ${mediaItem.mediaPath}"
                                            )
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
                            favadapter!!.deleteSelectedItems()
                            favadapter!!.unselectAllItems()
                            tvTitleFavourite.text = getString(R.string.favourite)
                        }
                    }
                } else {
                    (getString(R.string.max_selection_limit_is_100)).tos(this@FavoriteAct)
                }
            }

            ivBack.onClick { onBackPressedDispatcher.onBackPressed() }

            menuFav.onClick {
                val topcustomtopcustompopup = TopMenuFavouriteCustomPopup(this@FavoriteAct) {
                    when (it) {
                        "llStartSlide" -> {
                            SlideShowDialog(this@FavoriteAct) {
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
                            favadapter!!.selectAllItems()
                            menuFav.gone()
                        }
                    }
                }
                topcustomtopcustompopup.show(menuFav, 0, 0)
            }
        }
    }

    private fun setupPinchToZoomGesture() {
        scaleGestureDetector = ScaleGestureDetector(
            this,
            object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
                override fun onScale(detector: ScaleGestureDetector): Boolean {
                    val scaleFactor = detector.scaleFactor

                    if (scaleFactor > 1 && currentSpanCount > minSpanCount) {
                        currentSpanCount--
                    } else if (scaleFactor < 1 && currentSpanCount < maxSpanCount) {
                        currentSpanCount++
                    }

                    gridLayoutManager.spanCount = currentSpanCount
                    bind.rvFavourite.adapter?.notifyDataSetChanged()
                    return true
                }
            })

        bind.rvFavourite.setOnTouchListener { _, event ->
            scaleGestureDetector.onTouchEvent(event)
            false
        }
    }

    private fun unFavSelectedList() {
        if (favadapter!!.selectedItems.size <= 100) {
            val progressDialog = ProgressDialog(this@FavoriteAct).apply {
                setMessage(getString(R.string.removing_favorites))
                setCancelable(false)
                show()
            }

            CoroutineScope(Dispatchers.Main).launch {
                withContext(Dispatchers.IO) {
                    favadapter?.selectedItems?.forEach {
                        favouriteMediaDao.deleteMedia(it)
                    }
                }

                progressDialog.dismiss()

                favadapter!!.deleteSelectedItems()
                favadapter!!.unselectAllItems()
                bind.tvTitleFavourite.text = getString(R.string.favourite)

                Toast.makeText(
                    this@FavoriteAct,
                    getString(R.string.favorites_removed_successfully),
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            ("Max selection limit is 100").tos(this@FavoriteAct)
        }
    }

    fun createRecycleBin(): File {
        val recycleBin = File(this.getExternalFilesDir(null), ".gallery_recycleBin")
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
                true
            } else {
                Log.e(
                    "MoveToRecycleBin",
                    "Failed to delete original file: ${originalFile.absolutePath}"
                )
                false
            }
        } catch (e: IOException) {
            Log.e("MoveToRecycleBin", "IOException occurred: ${e.message}", e)
            false
        }
    }


    private fun openViewPagerSlideShowActivity(position: Int, sec: Int) {
        FavouriteMediaStoreSingleton.favouriteimageList = favouriteList
        FavouriteMediaStoreSingleton.favouriteselectedPosition = position

        val intent = Intent(this, FavouriteViewPagerActivity::class.java)
        intent.putExtra("slideshow", true)
        intent.putExtra("secoundSlideShow", sec)
        startActivity(intent)
    }
}