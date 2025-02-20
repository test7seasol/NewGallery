package com.gallery.photos.editpic.Activity

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.gallery.photos.editpic.Adapter.HideViewPagerAdapter
import com.gallery.photos.editpic.Dialogs.DeleteWithRememberDialog
import com.gallery.photos.editpic.Extensions.PREF_LANGUAGE_CODE
import com.gallery.photos.editpic.Extensions.beGone
import com.gallery.photos.editpic.Extensions.log
import com.gallery.photos.editpic.Extensions.name.getMediaDatabase
import com.gallery.photos.editpic.Extensions.onClick
import com.gallery.photos.editpic.Extensions.setLanguageCode
import com.gallery.photos.editpic.Extensions.shareFile
import com.gallery.photos.editpic.Extensions.tos
import com.gallery.photos.editpic.Model.HideMediaModel
import com.gallery.photos.editpic.PopupDialog.TopMenuHideCustomPopup
import com.gallery.photos.editpic.R
import com.gallery.photos.editpic.RoomDB.Dao.HideMediaDao
import com.gallery.photos.editpic.Utils.DeleteMediaStoreSingleton.deleteselectedPosition
import com.gallery.photos.editpic.Utils.HideMediaStoreSingleton
import com.gallery.photos.editpic.databinding.ActivityHideviewPagerBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class HideViewPagerActivity : AppCompatActivity() {
    private var viewpagerselectedPosition: Int = 1
    var hideMediaModel: HideMediaModel? = null
    private lateinit var binding: ActivityHideviewPagerBinding
    private lateinit var viewPagerAdapter: HideViewPagerAdapter
    private var hideImageListDelete: ArrayList<HideMediaModel> = arrayListOf()
    var hideMediaDao: HideMediaDao? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLanguageCode(this, MyApplicationClass.getString(PREF_LANGUAGE_CODE)!!)
        binding = ActivityHideviewPagerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        applyStatusBarColor()

        hideImageListDelete = HideMediaStoreSingleton.hideimageList
        viewpagerselectedPosition = HideMediaStoreSingleton.hideselectedPosition

        ("Delete onPageSelected: $viewpagerselectedPosition").log()

        setupViewPager(hideImageListDelete, viewpagerselectedPosition)

        hideMediaModel = hideImageListDelete[viewpagerselectedPosition]

        binding.ivBack.setOnClickListener {
            finish()
        }

        hideMediaDao = getMediaDatabase(this).hideMediaDao()

        binding.apply {


            bottomActions.bottomShare.onClick {
                shareFile(
                    this@HideViewPagerActivity,
                    hideImageListDelete[viewpagerselectedPosition].mediaPath
                )  // Replace with the file path

            }

            bottomActions.bottomProperties.beGone()

            ivMore.onClick {
                val topcustomtopcustompopup = TopMenuHideCustomPopup(this@HideViewPagerActivity) {
                    when (it) {
                        "tvUnHidden" -> {
                            CoroutineScope(Dispatchers.IO).launch {
                                val filePath = hideMediaModel!!.mediaPath
// Rename the file to remove the dot prefix
                                val hiddenFile = File(filePath)
                                if (hiddenFile.exists()) {
                                    val unhiddenFile =
                                        File(hiddenFile.parent, hiddenFile.name.removePrefix("."))

                                    hideImageListDelete[viewpagerselectedPosition].mediaName =
                                        unhiddenFile.name
                                    hideImageListDelete[viewpagerselectedPosition].mediaPath =
                                        unhiddenFile.path

                                    runOnUiThread {
                                        updateImageTitle(viewpagerselectedPosition)
                                    }

                                    if (hiddenFile.renameTo(unhiddenFile)) {
                                        Log.d("File", "File unhidden: ${unhiddenFile.absolutePath}")
                                        // Remove the entry from the database
                                        val media = hideMediaDao?.getMediaByPath(filePath)
                                        media?.let {
                                            hideMediaDao?.deleteMedia(it)
                                            Log.d("Database", "Media entry removed from database")
                                        }
                                    } else {
                                        Log.e("File", "Failed to unhide the file.")
                                    }
                                }

                            }
                        }
                    }
                }
                topcustomtopcustompopup.show(ivMore, 0, 0)
            }

            bottomActions.bottomDelete.onClick {
                DeleteWithRememberDialog(this@HideViewPagerActivity, isFromBin = true) {
                    run {
                        hideMediaModel?.let { it1 -> permanentlyDeleteFile(it1) }
                    }
                }
            }
        }
    }

    fun permanentlyDeleteFile(mediaItem: HideMediaModel) {
        Log.d("PermanentlyDelete", "Bin Path: ${mediaItem.mediaPath}")
        CoroutineScope(Dispatchers.IO).launch {
            val binFile = File(mediaItem.mediaPath)
            if (binFile.exists()) {
                if (binFile.delete()) {
                    Log.d("PermanentlyDelete", "File deleted: ${binFile.absolutePath}")
                    hideMediaDao!!.deleteMedia(mediaItem)  // Remove from Room database

                    runOnUiThread {
                        (getString(R.string.file_permanently_deleted)).tos(this@HideViewPagerActivity)
                        hideImageListDelete.removeAt(viewpagerselectedPosition)
//                        DeleteMediaStoreSingleton.deleteimageList.removeAt(viewpagerselectedPosition)
                        try {
                            if (hideImageListDelete.isEmpty()) {
                                finish()
                            } else setupViewPager(hideImageListDelete, viewpagerselectedPosition)
                        } catch (e: IndexOutOfBoundsException) {
                            hideImageListDelete.clear()
                            CoroutineScope(Dispatchers.IO).launch {
                                hideMediaDao!!.deleteAllMedia()
                            }
                        }
                    }
                } else {
                    Log.e("PermanentlyDelete", "Failed to delete file: ${binFile.absolutePath}")
                }
            } else {
                Log.e("PermanentlyDelete", "File not found: ${binFile.absolutePath}")
            }
        }
    }

    private fun setupViewPager(imageList: ArrayList<HideMediaModel>, currentPosition: Int) {
        viewPagerAdapter = HideViewPagerAdapter(this, imageList)
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
                hideMediaModel = imageList[position]
                updateImageTitle(position)

            }
        })
    }

    private fun updateImageTitle(position: Int) {
        val fileName = hideImageListDelete[position].mediaName  // Extract file name from path
        binding.tvtitile.text = fileName
//        binding.bottomActions.bottomEdit.visibility =
//            if (isVideoFile(hideImageListDelete[position].mediaPath)) View.GONE else View.VISIBLE
    }
    private fun applyStatusBarColor() {
        window.statusBarColor =
            resources.getColor(android.R.color.black, theme) // Set black status bar
        window.decorView.systemUiVisibility = 0 // Ensures white text/icons
        window.navigationBarColor = resources.getColor(android.R.color.black, theme)
    }
}