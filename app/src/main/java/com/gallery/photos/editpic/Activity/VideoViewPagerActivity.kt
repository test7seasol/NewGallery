package com.gallery.photos.editpic.Activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import com.gallery.photos.editpic.Adapter.VideoDisplayAdapter
import com.gallery.photos.editpic.Adapter.VideoPreviousNext
import com.gallery.photos.editpic.Dialogs.DeleteWithRememberDialog
import com.gallery.photos.editpic.Dialogs.PropertiesDialog
import com.gallery.photos.editpic.Extensions.formatDate
import com.gallery.photos.editpic.Extensions.log
import com.gallery.photos.editpic.Extensions.name.getMediaDatabase
import com.gallery.photos.editpic.Extensions.onClick
import com.gallery.photos.editpic.Extensions.shareFile
import com.gallery.photos.editpic.Model.DeleteMediaModel
import com.gallery.photos.editpic.Model.FavouriteMediaModel
import com.gallery.photos.editpic.Model.HideMediaModel
import com.gallery.photos.editpic.Model.MediaModel
import com.gallery.photos.editpic.Model.VideoModel
import com.gallery.photos.editpic.PopupDialog.ViewPagerPopupManager
import com.gallery.photos.editpic.R
import com.gallery.photos.editpic.RoomDB.Dao.DeleteMediaDao
import com.gallery.photos.editpic.RoomDB.Dao.FavouriteMediaDao
import com.gallery.photos.editpic.RoomDB.Dao.HideMediaDao
import com.gallery.photos.editpic.Utils.DeleteMediaStoreSingleton.deleteselectedPosition
import com.gallery.photos.editpic.Utils.VideoMediaStoreSingleton
import com.gallery.photos.editpic.Utils.VideoMediaStoreSingleton.videoimageList
import com.gallery.photos.editpic.Views.CustomViewPager
import com.gallery.photos.editpic.databinding.ActivityVideoviewPagerBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import java.util.UUID

class VideoViewPagerActivity : AppCompatActivity() {

    private var viewpagerselectedPosition: Int = 1
    var videoMediaModel: VideoModel? = null
    private lateinit var binding: ActivityVideoviewPagerBinding
    private var imageListVideo: ArrayList<VideoModel> = arrayListOf()
    var deleteMediaDao: DeleteMediaDao? = null
    var deleteMediaModel: DeleteMediaModel? = null
    var hideMediaModel: HideMediaModel? = null
    var hideMediaDao: HideMediaDao? = null
    var favouriteMediaDao: FavouriteMediaDao? = null
    private lateinit var videoDisplayAdapter: VideoDisplayAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoviewPagerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        hideMediaDao = getMediaDatabase(this).hideMediaDao()
        deleteMediaDao = getMediaDatabase(this).deleteMediaDao()
        favouriteMediaDao = getMediaDatabase(this).favouriteMediaDao()
        imageListVideo = VideoMediaStoreSingleton.videoimageList
        viewpagerselectedPosition = VideoMediaStoreSingleton.videoselectedPosition

        deleteMediaModel = DeleteMediaModel()
        hideMediaModel = HideMediaModel()

        imageListVideo[viewpagerselectedPosition].apply {
            deleteMediaModel!!.mediaId = videoId
            deleteMediaModel!!.mediaName = videoName
            deleteMediaModel!!.mediaPath = videoPath
            deleteMediaModel!!.mediaMimeType = "mp4"
            deleteMediaModel!!.mediaDateAdded = 0
            deleteMediaModel!!.isVideo = true
            deleteMediaModel!!.displayDate = ""
            deleteMediaModel!!.isSelect = isSelect
        }

        updateImageTitle(viewpagerselectedPosition)

        ("Delete onPageSelected: $viewpagerselectedPosition").log()

        setadpter(imageListVideo, viewpagerselectedPosition)

        binding.ivBack.setOnClickListener {
            finish()
        }

        binding.apply {

            bottomActions.bottomProperties.onClick {
                PropertiesDialog(
                    this@VideoViewPagerActivity, MediaModel(
                        mediaId = videoMediaModel!!.videoId,
                        mediaName = videoMediaModel!!.videoName,
                        mediaPath = videoMediaModel!!.videoPath,
                        mediaMimeType = "mp4",
                        mediaDateAdded = videoMediaModel!!.videoDateAdded,
                        isVideo = true,
                        displayDate = formatDate(videoMediaModel!!.videoDateAdded),
                        isSelect = false,
                        isFav = false
                    )
                ) {}
            }

            binding.bottomActions.bottomFavorite.setOnClickListener {
                val position = viewpagerselectedPosition
                val currentMedia = videoimageList[position]
                currentMedia.isFav = !currentMedia.isFav  // Toggle the favorite status

                CoroutineScope(Dispatchers.IO).launch {
                    val isFav = favouriteMediaDao?.isMediaFavorite(currentMedia.videoId) ?: false

                    if (isFav) {
                        // If it is already a favorite, remove it from the database
                        favouriteMediaDao?.getMediaById(currentMedia.videoId)
                            ?.let { favouriteMediaDao?.deleteMedia(it) }
                    } else {
                        // If it's not a favorite, add it to the database
                        favouriteMediaDao?.insertMedia(
                            FavouriteMediaModel(
                                mediaId = currentMedia.videoId,
                                mediaName = currentMedia.videoName,
                                mediaPath = currentMedia.videoPath,
                                mediaMimeType = currentMedia.videoPath,
                                mediaSize = currentMedia.videoSize,
                                mediaDateAdded = currentMedia.videoDateAdded,
                                isVideo = true,
                                displayDate = formatDate(currentMedia.videoDateAdded),
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
                    this@VideoViewPagerActivity, imageListVideo[viewpagerselectedPosition].videoPath
                )
            }

            ivMore.onClick {
                val topcustomtopcustompopup = ViewPagerPopupManager(this@VideoViewPagerActivity) {
                    when (it) {
                        "hiddentoid" -> {
                            renameAndHidePhoto(deleteMediaModel!!.mediaPath)
                            videoimageList.removeAt(viewpagerselectedPosition)
                            binding.viewPager.currentItem = viewpagerselectedPosition
                            if (videoimageList.isEmpty()) finish()
                            else setadpter(videoimageList, viewpagerselectedPosition)
                        }
                    }
                }
                topcustomtopcustompopup.show(ivMore, 0, 0)
            }
            bottomActions.bottomDelete.onClick {
                DeleteWithRememberDialog(this@VideoViewPagerActivity) {
                    run {
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

        imageListVideo[viewpagerselectedPosition].videoName = newFileName
//        updateImageTitle(viewpagerselectedPosition)
        binding.tvtitile.text = newFileName

        imageListVideo[viewpagerselectedPosition].apply {
            hideMediaModel!!.mediaId = videoId
            hideMediaModel!!.mediaName = newFileName
            hideMediaModel!!.mediaPath = hiddenFile.path
            hideMediaModel!!.mediaMimeType = "mp4"
            hideMediaModel!!.mediaDateAdded = videoDateAdded
            hideMediaModel!!.isVideo = true
            hideMediaModel!!.displayDate = formatDate(videoDateAdded)
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
                val randomMediaId = UUID.randomUUID().mostSignificantBits and Long.MAX_VALUE

                Log.d("MoveToRecycleBin", "Inserting media record into Room database.")
                deleteMediaModel!!.binPath = recycledFile.absolutePath
//                videoMediaModel!!.randomMediaId = randomMediaId

                deleteMediaDao!!.insertMedia(deleteMediaModel!!)  // Save path for restoration
//                imageList.removeAt(viewpagerselectedPosition)
//                MediaStoreSingleton.imageList.removeAt(viewpagerselectedPosition)
                runOnUiThread {
//                    binding.viewPager.currentItem = viewpagerselectedPosition + 1
                    videoimageList.removeAt(viewpagerselectedPosition)

                    if (videoimageList.isEmpty()) {
                        finish()
                    } else setadpter(videoimageList, viewpagerselectedPosition)
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

    /*
        private fun setupViewPager(imageList: ArrayList<VideoModel>, currentPosition: Int) {
            viewPagerAdapter = ViedoViewPagerAdapter(this, imageList)
            binding.viewPager.adapter = viewPagerAdapter
            binding.viewPager.setCurrentItem(currentPosition, false)

            binding.viewPager.offscreenPageLimit = 1
            // Set initial image title
    //        updateImageTitle(currentPosition)

            // Change title when page is scrolled
            binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    ("Delete onPageSelected: $position").log()
                    viewpagerselectedPosition = position

                    imageList[position].apply {
                        deleteMediaModel!!.mediaId = videoId
                        deleteMediaModel!!.mediaName = videoName
                        deleteMediaModel!!.mediaPath = videoPath
                        deleteMediaModel!!.mediaMimeType = "mp4"
                        deleteMediaModel!!.mediaDateAdded = 0
                        deleteMediaModel!!.isVideo = true
                        deleteMediaModel!!.displayDate = ""
                        deleteMediaModel!!.isSelect = isSelect

                        hideMediaModel!!.mediaId = videoId
                        hideMediaModel!!.mediaName = videoName
                        hideMediaModel!!.mediaPath = videoPath
                        hideMediaModel!!.mediaMimeType = "mp4"
                        hideMediaModel!!.mediaDateAdded = 0
                        hideMediaModel!!.isVideo = true
                        hideMediaModel!!.displayDate = ""
                        hideMediaModel!!.isSelect = isSelect

                    }
                    deleteselectedPosition = position
                    videoMediaModel = imageList[position]
                    updateImageTitle(position)
                    viewPagerAdapter.onDestroyVideoView()
                }
            })
        }
    */


    private fun updateImageTitle(position: Int) {
        val fileName = imageListVideo[position]  // Extract file name from path
        binding.tvtitile.text = fileName.videoName
        CoroutineScope(Dispatchers.IO).launch {
            favouriteMediaDao?.let { dao ->
                val isFav = dao.isMediaFavorite(fileName.videoId)
                runOnUiThread {
                    binding.bottomActions.bottomFavorite.setImageResource(if (isFav) R.drawable.fillfavourite else R.drawable.unfillfavourite)
                }
            }
        }
    }

    fun setadpter(imageList: ArrayList<VideoModel>, currentPosition: Int) {

        videoDisplayAdapter = VideoDisplayAdapter(this, imageList, object : VideoPreviousNext {
            override fun onPrevious(currentPos: Int) {

            }

            override fun onNext(currentPos: Int) {

            }
        })

        binding.viewPager.adapter = videoDisplayAdapter
        binding.viewPager.setCurrentItem(currentPosition, true)


        binding.viewPager.setOnPageChangeListener(object : CustomViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int,
            ) {

            }

            override fun onPageSelected(position: Int) {
                // Pause all other videos
                for (i in 0 until binding.viewPager.childCount) {
                    val otherView = binding.viewPager.findViewWithTag<ViewGroup>("video_$i")
                    val otherVideoView = otherView?.findViewById<VideoView>(R.id.video_view)
                    otherVideoView?.pause()
                }

                // Start the new video
                val currentView = binding.viewPager.findViewWithTag<ViewGroup>("video_$position")
                val videoView = currentView?.findViewById<VideoView>(R.id.video_view)
                videoView?.let {
                    it.start()
                }
                updateImageTitle(position)

                viewpagerselectedPosition = position

                imageList[position].apply {
                    deleteMediaModel!!.mediaId = videoId
                    deleteMediaModel!!.mediaName = videoName
                    deleteMediaModel!!.mediaPath = videoPath
                    deleteMediaModel!!.mediaMimeType = "mp4"
                    deleteMediaModel!!.mediaDateAdded = 0
                    deleteMediaModel!!.isVideo = true
                    deleteMediaModel!!.displayDate = ""
                    deleteMediaModel!!.isSelect = isSelect

                    hideMediaModel!!.mediaId = videoId
                    hideMediaModel!!.mediaName = videoName
                    hideMediaModel!!.mediaPath = videoPath
                    hideMediaModel!!.mediaMimeType = "mp4"
                    hideMediaModel!!.mediaDateAdded = 0
                    hideMediaModel!!.isVideo = true
                    hideMediaModel!!.displayDate = ""
                    hideMediaModel!!.isSelect = isSelect

                }
                deleteselectedPosition = position
                videoMediaModel = imageList[position]
                updateImageTitle(position)
            }

            override fun onPageScrollStateChanged(state: Int) {
            }
        })
    }
}