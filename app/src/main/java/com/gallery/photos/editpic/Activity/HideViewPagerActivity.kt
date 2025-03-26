package com.gallery.photos.editpic.Activity

import android.content.ContentUris
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.viewpager2.widget.ViewPager2
import com.gallery.photos.editpic.Adapter.HideViewPagerAdapter
import com.gallery.photos.editpic.Dialogs.AllFilesAccessDialog
import com.gallery.photos.editpic.Dialogs.DeleteWithRememberDialog
import com.gallery.photos.editpic.Extensions.PREF_LANGUAGE_CODE
import com.gallery.photos.editpic.Extensions.beGone
import com.gallery.photos.editpic.Extensions.hasAllFilesAccessAs
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
import com.gallery.photos.editpic.myadsworld.MyAddPrefs
import com.gallery.photos.editpic.myadsworld.MyAllAdCommonClass
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class HideViewPagerActivity : BaseActivity() {
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

        hideBottomNavigationBar(R.color.black)

        MyAllAdCommonClass.showAdmobBanner(
            this@HideViewPagerActivity,
            binding.bannerContainer,
            binding.shimmerContainerBanner,
            false,
            MyAddPrefs(this@HideViewPagerActivity).admBannerId
        )

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
                            if (!hasAllFilesAccessAs(this@HideViewPagerActivity)) {
                                (getString(R.string.all_files_access_required)).tos(this@HideViewPagerActivity)
                                AllFilesAccessDialog(this@HideViewPagerActivity){

                                }
//                                startActivityWithBundle<AllFilePermissionActivity>(Bundle().apply {
//                                    putString("isFrom", "Activitys")
//                                })
                                return@TopMenuHideCustomPopup
                            }

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
                if (!hasAllFilesAccessAs(this@HideViewPagerActivity)) {
                    (getString(R.string.all_files_access_required)).tos(this@HideViewPagerActivity)
                    AllFilesAccessDialog(this@HideViewPagerActivity){

                    }
//                    startActivityWithBundle<AllFilePermissionActivity>(Bundle().apply {
//                        putString("isFrom", "Activitys")
//                    })
                    return@onClick
                }

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

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                // ✅ For Android 9 and below (API 28-)
                if (binFile.exists() && binFile.delete()) {
                    Log.d("PermanentlyDelete", "File deleted: ${binFile.absolutePath}")
                    deleteMediaFromDatabase(mediaItem)
                } else {
                    Log.e("PermanentlyDelete", "Failed to delete file: ${binFile.absolutePath}")
                }
            } else {
                // ✅ For Android 10+ (API 29+), delete via MediaStore
                val contentResolver = applicationContext.contentResolver
                val uri = getMediaUri(mediaItem.mediaPath) // Get URI for MediaStore deletion

                if (uri != null) {
                    val deleteCount = contentResolver.delete(uri, null, null)
                    if (deleteCount > 0) {
                        Log.d(
                            "PermanentlyDelete",
                            "File deleted via MediaStore: ${mediaItem.mediaPath}"
                        )
                        deleteMediaFromDatabase(mediaItem)
                    } else {
                        Log.e("PermanentlyDelete", "Failed to delete file via MediaStore")
                    }
                } else {
                    Log.e("PermanentlyDelete", "File URI not found in MediaStore")
                }
            }
        }
    }

    // ✅ Function to get MediaStore URI (Android 10+)
    private fun getMediaUri(filePath: String): Uri? {
        val contentResolver = applicationContext.contentResolver
        val projection = arrayOf(MediaStore.Images.Media._ID)
        val selection = MediaStore.Images.Media.DATA + " = ?"
        val selectionArgs = arrayOf(filePath)

        val queryUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        } else {
            MediaStore.Files.getContentUri("external")
        }

        contentResolver.query(queryUri, projection, selection, selectionArgs, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val id = cursor.getLong(0)
                return ContentUris.withAppendedId(queryUri, id)
            }
        }
        return null
    }

    // ✅ Function to delete from Room Database and update UI
    private fun deleteMediaFromDatabase(mediaItem: HideMediaModel) {
        CoroutineScope(Dispatchers.IO).launch {
            hideMediaDao?.deleteMedia(mediaItem)  // Remove from Room database
        }

        runOnUiThread {
            getString(R.string.file_permanently_deleted).tos(this@HideViewPagerActivity)
            hideImageListDelete.removeAt(viewpagerselectedPosition)

            try {
                if (hideImageListDelete.isEmpty()) {
                    finish()
                } else {
                    setupViewPager(hideImageListDelete, viewpagerselectedPosition)
                }
            } catch (e: IndexOutOfBoundsException) {
                hideImageListDelete.clear()
                CoroutineScope(Dispatchers.IO).launch {
                    hideMediaDao?.deleteAllMedia()
                }
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

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                try {
                    if (imageList.isEmpty()) {
                        Log.e("ViewPager", "Error: imageList is empty, skipping onPageSelected")
                        return  // Exit early to prevent crash
                    }

                    if (position < 0 || position >= imageList.size) {
                        Log.e(
                            "ViewPager",
                            "Invalid position: $position, list size: ${imageList.size}"
                        )
                        return  // Prevent accessing an invalid index
                    }

                    Log.d("ViewPager", "Delete onPageSelected: $position")
                viewpagerselectedPosition = position
                deleteselectedPosition = position
                hideMediaModel = imageList[position]
                updateImageTitle(position)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        })
    }


    private fun updateImageTitle(position: Int) {
        if (position < 0 || position >= hideImageListDelete.size) {
            Log.e("HideViewPagerActivity", "Invalid index: $position, list size: ${hideImageListDelete.size}")
            return
        }
        val fileName = hideImageListDelete[position].mediaName
        binding.tvtitile.text = fileName
    }


    private fun applyStatusBarColor() {
        window.statusBarColor =
            resources.getColor(android.R.color.black, theme) // Set black status bar
        window.decorView.systemUiVisibility = 0 // Ensures white text/icons
        window.navigationBarColor = resources.getColor(android.R.color.black, theme)
    }
}