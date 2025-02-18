package com.gallery.photos.editpic.Activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.gallery.photos.editpic.Adapter.FavouriteViewPagerAdapter
import com.gallery.photos.editpic.Dialogs.DeleteWithRememberDialog
import com.gallery.photos.editpic.Extensions.isVideoFile
import com.gallery.photos.editpic.Extensions.log
import com.gallery.photos.editpic.Extensions.name.getMediaDatabase
import com.gallery.photos.editpic.Extensions.onClick
import com.gallery.photos.editpic.Extensions.shareFile
import com.gallery.photos.editpic.Model.DeleteMediaModel
import com.gallery.photos.editpic.Model.FavouriteMediaModel
import com.gallery.photos.editpic.Model.HideMediaModel
import com.gallery.photos.editpic.PopupDialog.ViewPagerHidePopupManager
import com.gallery.photos.editpic.R
import com.gallery.photos.editpic.RoomDB.Dao.DeleteMediaDao
import com.gallery.photos.editpic.RoomDB.Dao.FavouriteMediaDao
import com.gallery.photos.editpic.RoomDB.Dao.HideMediaDao
import com.gallery.photos.editpic.Utils.DeleteMediaStoreSingleton.deleteselectedPosition
import com.gallery.photos.editpic.Utils.FavouriteMediaStoreSingleton
import com.gallery.photos.editpic.Utils.FavouriteMediaStoreSingleton.favouriteimageList
import com.gallery.photos.editpic.Utils.MediaStoreSingleton.imageList
import com.gallery.photos.editpic.databinding.ActivityFavouriteviewPagerBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException

class FavouriteViewPagerActivity : AppCompatActivity() {

    private var viewpagerselectedPosition: Int = 1
    var deleteMediaModel: DeleteMediaModel? = null
    private lateinit var binding: ActivityFavouriteviewPagerBinding
    private lateinit var viewPagerAdapter: FavouriteViewPagerAdapter
    private var imageListFavourite: ArrayList<FavouriteMediaModel> = arrayListOf()
    var deleteMediaDao: DeleteMediaDao? = null
    var favouriteMediaDao: FavouriteMediaDao? = null
    var hideMediaDao: HideMediaDao? = null
    var hideMediaModel: HideMediaModel? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavouriteviewPagerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        imageListFavourite = FavouriteMediaStoreSingleton.favouriteimageList
        viewpagerselectedPosition = FavouriteMediaStoreSingleton.favouriteselectedPosition
        hideMediaDao = getMediaDatabase(this).hideMediaDao()

        hideMediaModel = HideMediaModel()
        deleteMediaModel = DeleteMediaModel()

        favouriteMediaDao = getMediaDatabase(this).favouriteMediaDao()
        deleteMediaDao = getMediaDatabase(this).deleteMediaDao()

        ("Favourite onPageSelected: $viewpagerselectedPosition").log()

        setupViewPager(imageListFavourite, viewpagerselectedPosition)

        binding.ivBack.setOnClickListener {
            finish()
        }


        binding.apply {
            ivMore.onClick {
                val topcustomtopcustompopup =
                    ViewPagerHidePopupManager(this@FavouriteViewPagerActivity) {
                        when (it) {
                            "hiddentoid" -> {
                                renameAndHidePhoto(deleteMediaModel!!.mediaPath)
                                favouriteimageList.removeAt(viewpagerselectedPosition)
                                binding.viewPager.currentItem = viewpagerselectedPosition

                                if (favouriteimageList.isEmpty()) {
                                    finish()
                                } else setupViewPager(favouriteimageList, viewpagerselectedPosition)
                            }
                        }
                    }
                topcustomtopcustompopup.show(ivMore, 0, 0)
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
                DeleteWithRememberDialog(this@FavouriteViewPagerActivity) {
                    run {
                        val currentMedia = imageListFavourite[viewpagerselectedPosition]
                        CoroutineScope(Dispatchers.IO).launch {
                            favouriteMediaDao?.getMediaById(currentMedia.mediaId)
                                ?.let { favouriteMediaDao?.deleteMedia(it) }
                        }
                        moveToRecycleBin(deleteMediaModel!!.mediaPath)
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
                runOnUiThread {
//                    binding.viewPager.currentItem = viewpagerselectedPosition + 1
                    favouriteimageList.removeAt(viewpagerselectedPosition)
                    runOnUiThread {
//                        DeleteMediaStoreSingleton.deleteimageList.removeAt(viewpagerselectedPosition)
                        if (favouriteimageList.isEmpty()) {
                            finish()
                        } else setupViewPager(imageListFavourite, viewpagerselectedPosition)
                    }
                }
                Log.d("MoveToRecycleBin", "Media record inserted into Room database.")
            }
            true
        } catch (e: IOException) {
            Log.e("MoveToRecycleBin", "IOException occurred: ${e.message}")
            e.printStackTrace()
            false
        }
    }

    private fun setupViewPager(imageList: ArrayList<FavouriteMediaModel>, currentPosition: Int) {
        viewPagerAdapter = FavouriteViewPagerAdapter(this, imageList)
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
                viewpagerselectedPosition = position
                deleteselectedPosition = position
                imageList[position].apply {
                    deleteMediaModel!!.mediaId = mediaId
                    deleteMediaModel!!.mediaName = mediaName
                    deleteMediaModel!!.mediaPath = mediaPath
                    deleteMediaModel!!.mediaMimeType = mediaMimeType
                    deleteMediaModel!!.mediaDateAdded = mediaDateAdded
                    deleteMediaModel!!.isVideo = isVideo
                    deleteMediaModel!!.displayDate = displayDate
                    deleteMediaModel!!.isSelect = isSelect
                }
                updateImageTitle(position)
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
}