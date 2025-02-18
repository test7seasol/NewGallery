package com.gallery.photos.editpic.Activity

import CreateNewFolderDialog
import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.gallery.photos.editpic.Adapter.FavouriteAdapter
import com.gallery.photos.editpic.Dialogs.DeleteWithRememberDialog
import com.gallery.photos.editpic.Extensions.gone
import com.gallery.photos.editpic.Extensions.handleBackPress
import com.gallery.photos.editpic.Extensions.log
import com.gallery.photos.editpic.Extensions.name.getMediaDatabase
import com.gallery.photos.editpic.Extensions.notifyGalleryRoot
import com.gallery.photos.editpic.Extensions.onClick
import com.gallery.photos.editpic.Extensions.shareMultipleFilesFavourite
import com.gallery.photos.editpic.Extensions.tos
import com.gallery.photos.editpic.Extensions.visible
import com.gallery.photos.editpic.Model.DeleteMediaModel
import com.gallery.photos.editpic.Model.FavouriteMediaModel
import com.gallery.photos.editpic.PopupDialog.FavouriteBottomPopup
import com.gallery.photos.editpic.PopupDialog.TopMenuFavouriteCustomPopup
import com.gallery.photos.editpic.RoomDB.Dao.DeleteMediaDao
import com.gallery.photos.editpic.RoomDB.Dao.FavouriteMediaDao
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

                    ("Album created successfully").tos(this)
                }
            } else {
                Log.d("ActivityResult", "Result canceled or failed")
            }
        }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityFavoriteBinding.inflate(layoutInflater)
        setContentView(bind.root)

        handleBackPress {
            if (bind.tvTitleFavourite.text != "Favourite") {
                favadapter!!.unselectAllItems()
                bind.tvTitleFavourite.text = "Favourite"
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

        favadapter = FavouriteAdapter(this@FavoriteAct, favouriteList) { onLongItemClick ->
            if (onLongItemClick) {
                bind.selectedcontainerFavouriteid.visible()
//                binding.ivSearch.gone()
                bind.menuFav.gone()

            } else {
                bind.tvTitleFavourite.text = "Favourite"
//                binding.ivSearch.visible()
                bind.selectedcontainerFavouriteid.gone()
                bind.menuFav.visible()
            }
        }
        bind.rvFavourite.adapter = favadapter

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
                            tvTitleFavourite.text = "Favourite"
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
                            tvTitleFavourite.text = "Favourite"
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
                            val progressDialog = ProgressDialog(this@FavoriteAct)
                            progressDialog.setMessage("Deleting files...")
                            progressDialog.setCancelable(false)
                            progressDialog.show()

                            withContext(Dispatchers.IO) {
                                val deletionJobs = favadapter!!.selectedItems.map {
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
                                        favouriteMediaModel!!.apply {
                                            mediaId = it.mediaId
                                            mediaName = it.mediaName
                                            mediaPath = it.mediaPath
                                            mediaMimeType = it.mediaMimeType
                                            mediaDateAdded = it.mediaDateAdded
                                            isVideo = it.isVideo
                                            displayDate = it.displayDate
                                            isSelect = it.isSelect
                                        }
                                        moveToRecycleBin(
                                            deleteMediaModel!!.mediaPath
                                        )
                                    }
                                }

                                // Wait for all deletion tasks to complete
                                deletionJobs.awaitAll()
                            }

                            // Dismiss Progress Dialog and update UI
                            progressDialog.dismiss()

                            favadapter!!.selectedItems.forEach {
                                favouriteMediaDao.deleteMedia(it)
                            }

                            favadapter!!.deleteSelectedItems()
                            favadapter!!.unselectAllItems()
                            tvTitleFavourite.text = "Favourite"
                        }
                    }
                } else {
                    ("Max selection limit is 100").tos(this@FavoriteAct)
                }
            }

            ivBack.onClick { onBackPressedDispatcher.onBackPressed() }

            menuFav.onClick {
                val topcustomtopcustompopup = TopMenuFavouriteCustomPopup(this@FavoriteAct) {
                    when (it) {
                        "llStartSlide" -> {

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

    private fun unFavSelectedList() {
        if (favadapter!!.selectedItems.size <= 100) {
            val progressDialog = ProgressDialog(this@FavoriteAct).apply {
                setMessage("Removing favorites...")
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
                bind.tvTitleFavourite.text = "Favourite"

                Toast.makeText(
                    this@FavoriteAct, "Favorites removed successfully", Toast.LENGTH_SHORT
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

    fun moveToRecycleBin(
        originalFilePath: String
    ): Boolean {
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
}