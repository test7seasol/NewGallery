package com.gallery.photos.editpic.Activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.gallery.photos.editpic.Adapter.ViewPagerAdapter
import com.gallery.photos.editpic.Dialogs.DeleteWithRememberDialog
import com.gallery.photos.editpic.Dialogs.PropertiesDialog
import com.gallery.photos.editpic.EditModule.EditImageActivity
import com.gallery.photos.editpic.Extensions.handleBackPress
import com.gallery.photos.editpic.Extensions.isVideoFile
import com.gallery.photos.editpic.Extensions.log
import com.gallery.photos.editpic.Extensions.name.getMediaDatabase
import com.gallery.photos.editpic.Extensions.onClick
import com.gallery.photos.editpic.Extensions.shareFile
import com.gallery.photos.editpic.Extensions.tos
import com.gallery.photos.editpic.Model.DeleteMediaModel
import com.gallery.photos.editpic.Model.FavouriteMediaModel
import com.gallery.photos.editpic.Model.HideMediaModel
import com.gallery.photos.editpic.Model.MediaModel
import com.gallery.photos.editpic.PopupDialog.ViewPagerPopupManager
import com.gallery.photos.editpic.R
import com.gallery.photos.editpic.RoomDB.Dao.DeleteMediaDao
import com.gallery.photos.editpic.RoomDB.Dao.FavouriteMediaDao
import com.gallery.photos.editpic.RoomDB.Dao.HideMediaDao
import com.gallery.photos.editpic.Utils.MediaStoreSingleton
import com.gallery.photos.editpic.databinding.ActivityViewPagerBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import java.util.Timer
import java.util.TimerTask

class ViewPagerActivity : AppCompatActivity() {
    private var timer: Timer? = null
    private val handler = Handler(Looper.getMainLooper())
    private var isRunning = false

    private var viewpagerselectedPosition: Int = 1
    var deleteMediaModel: DeleteMediaModel? = null
    private lateinit var binding: ActivityViewPagerBinding
    private lateinit var viewPagerAdapter: ViewPagerAdapter
    private var imageList: ArrayList<MediaModel> = arrayListOf()
    var deleteMediaDao: DeleteMediaDao? = null
    var favouriteMediaDao: FavouriteMediaDao? = null
    var hideMediaDao: HideMediaDao? = null
    var hideMediaModel: HideMediaModel? = null
    var favouriteMediaModel: FavouriteMediaModel? = null
    var isFromSlideShow: Boolean = false
    var secoundSlideShow: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewPagerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        applyStatusBarColor()

        imageList = MediaStoreSingleton.imageList
        viewpagerselectedPosition = MediaStoreSingleton.selectedPosition
        deleteMediaModel = DeleteMediaModel()
        hideMediaModel = HideMediaModel()
        favouriteMediaModel = FavouriteMediaModel()

        isFromSlideShow = intent?.extras?.getBoolean("slideshow", false) == true
        secoundSlideShow = intent?.extras?.getInt("secoundSlideShow", 1) ?: 1

        deleteMediaDao = getMediaDatabase(this).deleteMediaDao()
        hideMediaDao = getMediaDatabase(this).hideMediaDao()
        favouriteMediaDao = getMediaDatabase(this).favouriteMediaDao()



        imageList[viewpagerselectedPosition].apply {
            deleteMediaModel!!.mediaId = mediaId
            deleteMediaModel!!.mediaName = mediaName
            deleteMediaModel!!.mediaPath = mediaPath
            deleteMediaModel!!.mediaMimeType = mediaMimeType
            deleteMediaModel!!.mediaDateAdded = mediaDateAdded
            deleteMediaModel!!.isVideo = isVideo
            deleteMediaModel!!.displayDate = displayDate
            deleteMediaModel!!.isSelect = isSelect

            favouriteMediaModel!!.mediaId = mediaId
            favouriteMediaModel!!.mediaName = mediaName
            favouriteMediaModel!!.mediaPath = mediaPath
            favouriteMediaModel!!.mediaMimeType = mediaMimeType
            favouriteMediaModel!!.mediaDateAdded = mediaDateAdded
            favouriteMediaModel!!.isVideo = isVideo
            favouriteMediaModel!!.displayDate = displayDate
            favouriteMediaModel!!.isSelect = isSelect
            favouriteMediaModel!!.isFav = isFav
        }


        setupViewPager(imageList, viewpagerselectedPosition)

        binding.ivBack.setOnClickListener {
            if (isFromSlideShow) {
                sliderstop()
                finish()
            } else {
                finish()
            }
        }

        binding.apply {
            binding.bottomActions.bottomShare.onClick {
                shareFile(this@ViewPagerActivity, imageList[viewpagerselectedPosition].mediaPath)
            }
            binding.bottomActions.bottomFavorite.setOnClickListener {
                val position = viewpagerselectedPosition
                val currentMedia = imageList[position]
                currentMedia.isFav = !currentMedia.isFav  // Toggle the favorite status

                CoroutineScope(Dispatchers.IO).launch {
                    val isFav = favouriteMediaDao?.isMediaFavorite(currentMedia.mediaId) ?: false

                    if (isFav) {
                        // If it is already a favorite, remove it from the database
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

            bottomActions.bottomProperties.onClick {
                PropertiesDialog(this@ViewPagerActivity, imageList[viewpagerselectedPosition]) {}
            }

            ivOrientation.onClick {

            }

            ivMore.onClick {
                val topcustomtopcustompopup = ViewPagerPopupManager(this@ViewPagerActivity) {
                    when (it) {
                        "tvSlideShow" -> {

                        }
                        "hiddentoid" -> {
                            renameAndHidePhoto(deleteMediaModel!!.mediaPath)
                            imageList.removeAt(viewpagerselectedPosition)
                            binding.viewPager.currentItem = viewpagerselectedPosition

                            if (imageList.isEmpty()) {
                                finish()
                            } else setupViewPager(imageList, viewpagerselectedPosition)
                        }
                    }
                }
                topcustomtopcustompopup.show(ivMore, 0, 0)
            }

            bottomActions.bottomDelete.onClick {
                DeleteWithRememberDialog(this@ViewPagerActivity) {
                    run {
                        moveToRecycleBin(deleteMediaModel!!.mediaPath)
                    }
                }
            }

            bottomActions.bottomEdit.onClick {
                val intent = Intent(this@ViewPagerActivity, EditImageActivity::class.java)
                intent.putExtra("IMAGE_PATH", imageList[viewpagerselectedPosition].mediaPath)
                startActivity(intent)
            }
        }


        //todo: onBackPress
        handleBackPress {
            if (isFromSlideShow) {
                sliderstop()
                finish()
            } else {
                finish()
            }
        }


        if (isFromSlideShow) {
            isOneTimeVisibleTools = !isOneTimeVisibleTools
            binding.bottomActions.root.visibility = View.INVISIBLE
            binding.rltop.visibility = View.INVISIBLE
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN // Hide status bar
            ("Slide start $secoundSlideShow sec")
            startTimerTask()
        }

    }

    private fun startTimerTask() {
        timer = Timer()
        timer?.schedule(object : TimerTask() {
            override fun run() {
                handler.post {
                    val position = binding.viewPager.currentItem
                    if (position < imageList.size - 1) {
                        binding.viewPager.currentItem = (position + 1)
                        updateImageTitle(position + 1)
                    } else {
                        ("Slide end").tos(this@ViewPagerActivity)
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

    private fun applyStatusBarColor() {
        window.statusBarColor =
            resources.getColor(android.R.color.black, theme) // Set black status bar
        window.decorView.systemUiVisibility = 0 // Ensures white text/icons
        window.navigationBarColor = resources.getColor(android.R.color.black, theme)
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

        imageList[viewpagerselectedPosition].mediaName = newFileName
//        updateImageTitle(viewpagerselectedPosition)
        binding.tvtitile.text = newFileName

        imageList[viewpagerselectedPosition].apply {
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
            favouriteMediaDao!!.getMediaById(hideMediaModel!!.mediaId)
                ?.let { favouriteMediaDao!!.deleteMedia(it) }
            hideMediaModel?.let { hideMediaDao!!.insertMedia(it) }
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
                deleteMediaModel!!.mediaDateAdded =
                    imageList[viewpagerselectedPosition].mediaDateAdded
//                videoMediaModel!!.randomMediaId = randomMediaId

                deleteMediaDao!!.insertMedia(deleteMediaModel!!)  // Save path for restoration
//                imageList.removeAt(viewpagerselectedPosition)
//                MediaStoreSingleton.imageList.removeAt(viewpagerselectedPosition)
                runOnUiThread {
//                    binding.viewPager.currentItem = viewpagerselectedPosition + 1
                    imageList.removeAt(viewpagerselectedPosition)

                    if (imageList.isEmpty()) {
                        finish()
                    } else setupViewPager(imageList, viewpagerselectedPosition)

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

    var isOneTimeVisibleTools = false

    private fun setupViewPager(imageList: List<MediaModel>, currentPosition: Int) {
        viewPagerAdapter = ViewPagerAdapter(this, imageList) {
            ("onClick").log()
            isOneTimeVisibleTools = !isOneTimeVisibleTools
            if (isOneTimeVisibleTools) {
                binding.bottomActions.root.visibility = View.INVISIBLE
                binding.rltop.visibility = View.INVISIBLE
                window.decorView.systemUiVisibility =
                    View.SYSTEM_UI_FLAG_FULLSCREEN // Hide status bar
            } else {
                if (isFromSlideShow) {
                    sliderstop()
                }
                binding.bottomActions.root.visibility = View.VISIBLE
                binding.rltop.visibility = View.VISIBLE
                window.decorView.systemUiVisibility =
                    View.SYSTEM_UI_FLAG_VISIBLE // Show status bar & navbar
            }
        }

        binding.viewPager.adapter = viewPagerAdapter
        binding.viewPager.setCurrentItem(currentPosition, false)

        binding.viewPager.offscreenPageLimit = 1
        // Set initial image title
        updateImageTitle(currentPosition)

        // Change title when page is scrolled
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                viewpagerselectedPosition = position
                imageList[position].apply {
                    deleteMediaModel!!.mediaId = mediaId
                    deleteMediaModel!!.mediaName = mediaName
                    deleteMediaModel!!.mediaPath = mediaPath
                    deleteMediaModel!!.mediaMimeType = mediaMimeType
                    deleteMediaModel!!.mediaDateAdded = mediaDateAdded
                    deleteMediaModel!!.isVideo = isVideo
                    deleteMediaModel!!.displayDate = displayDate
                    deleteMediaModel!!.isSelect = isSelect

                    favouriteMediaModel!!.mediaId = mediaId
                    favouriteMediaModel!!.mediaName = mediaName
                    favouriteMediaModel!!.mediaPath = mediaPath
                    favouriteMediaModel!!.mediaMimeType = mediaMimeType
                    favouriteMediaModel!!.mediaDateAdded = mediaDateAdded
                    favouriteMediaModel!!.isVideo = isVideo
                    favouriteMediaModel!!.displayDate = displayDate
                    favouriteMediaModel!!.isSelect = isSelect
                    favouriteMediaModel!!.isFav = isFav
                }
                updateImageTitle(position)
            }
        })
    }

    private fun updateImageTitle(position: Int) {
        val fileName = imageList[position]
        binding.tvtitile.text = fileName.mediaName

        binding.bottomActions.bottomEdit.visibility =
            if (isVideoFile(imageList[position].mediaPath)) View.GONE else View.VISIBLE

        CoroutineScope(Dispatchers.IO).launch {
            favouriteMediaDao?.let { dao ->
                val isFav = dao.isMediaFavorite(fileName.mediaId)
                runOnUiThread {
                    binding.bottomActions.bottomFavorite.setImageResource(if (isFav) R.drawable.fillfavourite else R.drawable.unfillfavourite)
                }
            }
        }
    }
}