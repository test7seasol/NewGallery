package com.gallery.photos.editpic.Activity

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.gallery.photos.editpic.Adapter.DeleteViewPagerAdapter
import com.gallery.photos.editpic.Dialogs.DeleteWithRememberDialog
import com.gallery.photos.editpic.Extensions.PREF_LANGUAGE_CODE
import com.gallery.photos.editpic.Extensions.isVideoFile
import com.gallery.photos.editpic.Extensions.log
import com.gallery.photos.editpic.Extensions.name.getMediaDatabase
import com.gallery.photos.editpic.Extensions.onClick
import com.gallery.photos.editpic.Extensions.setLanguageCode
import com.gallery.photos.editpic.Extensions.tos
import com.gallery.photos.editpic.Model.DeleteMediaModel
import com.gallery.photos.editpic.RoomDB.Dao.DeleteMediaDao
import com.gallery.photos.editpic.Utils.DeleteMediaStoreSingleton
import com.gallery.photos.editpic.Utils.DeleteMediaStoreSingleton.deleteselectedPosition
import com.gallery.photos.editpic.databinding.ActivityDeleteviewPagerBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DeleteViewPagerActivity : AppCompatActivity() {

    private var viewpagerselectedPosition: Int = 1
    var deleteMediaModel: DeleteMediaModel? = null
    private lateinit var binding: ActivityDeleteviewPagerBinding
    private lateinit var viewPagerAdapter: DeleteViewPagerAdapter
    private var imageListDelete: ArrayList<DeleteMediaModel> = arrayListOf()
    var deleteMediaDao: DeleteMediaDao? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLanguageCode(this, MyApplicationClass.getString(PREF_LANGUAGE_CODE)!!)
        binding = ActivityDeleteviewPagerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        applyStatusBarColor()

        imageListDelete = DeleteMediaStoreSingleton.deleteimageList
        viewpagerselectedPosition = DeleteMediaStoreSingleton.deleteselectedPosition

        ("Delete onPageSelected: $viewpagerselectedPosition").log()

        deleteMediaModel = imageListDelete[viewpagerselectedPosition]

        setupViewPager(imageListDelete, viewpagerselectedPosition)

        binding.ivBack.setOnClickListener {
            finish()
        }

        deleteMediaDao = getMediaDatabase(this).deleteMediaDao()

        binding.apply {

            bottomActions.fileDelete.onClick {
                DeleteWithRememberDialog(this@DeleteViewPagerActivity, isFromBin = true) {
                    run {
                        deleteMediaModel?.let { it1 -> permanentlyDeleteFile(it1) }
                    }
                }
            }
            bottomActions.fileRestore.onClick {
                restoreSingleFileFromRecycleBin(deleteMediaModel!!)
            }
        }
    }

    fun restoreSingleFileFromRecycleBin(mediaItem: DeleteMediaModel) {
        val originalDate = mediaItem.mediaDateAdded// Fallback to current time if null
        val formattedDate = formatDate(originalDate) // Format the date for logging
        Log.d("RestoreFile", "Bin Path: ${mediaItem.binPath} | Date: $formattedDate")

        CoroutineScope(Dispatchers.IO).launch {
            val binFile = File(mediaItem.binPath)
            val originalFile = File(mediaItem.mediaPath)

            if (binFile.exists()) {
                try {
                    originalFile.parentFile?.mkdirs()

                    if (binFile.renameTo(originalFile)) {
                        originalFile.setLastModified(originalDate) // Set the modification date

                        Log.d(
                            "RestoreFile",
                            "File restored to: ${originalFile.absolutePath} with date: $formattedDate"
                        )
                        deleteMediaDao?.deleteMedia(mediaItem)

                        runOnUiThread {
                            ("File restored successfully").tos(this@DeleteViewPagerActivity)
                            imageListDelete.removeAt(viewpagerselectedPosition)

                            if (imageListDelete.isEmpty()) {
                                finish()
                            } else {
                                setupViewPager(imageListDelete, viewpagerselectedPosition)
                            }
                        }
                    } else {
                        Log.e(
                            "RestoreFile", "Failed to restore file to: ${originalFile.absolutePath}"
                        )
                    }
                } catch (e: Exception) {
                    Log.e("RestoreFile", "Error restoring file: ${e.message}")
                }
            } else {
                Log.e("RestoreFile", "File not found: ${binFile.absolutePath}")
            }
        }
    }

    private fun applyStatusBarColor() {
        window.statusBarColor =
            resources.getColor(android.R.color.black, theme) // Set black status bar
        window.decorView.systemUiVisibility = 0 // Ensures white text/icons
        window.navigationBarColor = resources.getColor(android.R.color.black, theme)
    }

    // Function to format the date
    fun formatDate(timestamp: Long): String {
        val date = Date(timestamp)
        val format = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        return format.format(date)
    }

    fun permanentlyDeleteFile(mediaItem: DeleteMediaModel) {
        Log.d("PermanentlyDelete", "Bin Path: ${mediaItem.binPath}")
        CoroutineScope(Dispatchers.IO).launch {
            val binFile = File(mediaItem.binPath)
            if (binFile.exists()) {
                if (binFile.delete()) {
                    Log.d("PermanentlyDelete", "File deleted: ${binFile.absolutePath}")
                    deleteMediaDao!!.deleteMedia(mediaItem)  // Remove from Room database

                    runOnUiThread {
                        ("File permanently deleted").tos(this@DeleteViewPagerActivity)
                        imageListDelete.removeAt(viewpagerselectedPosition)
//                        DeleteMediaStoreSingleton.deleteimageList.removeAt(viewpagerselectedPosition)
                        if (imageListDelete.isEmpty()) {
                            finish()
                        } else setupViewPager(imageListDelete, viewpagerselectedPosition)
                    }
                } else {
                    Log.e("PermanentlyDelete", "Failed to delete file: ${binFile.absolutePath}")
                }
            } else {
                Log.e("PermanentlyDelete", "File not found: ${binFile.absolutePath}")
            }
        }
    }

    private fun setupViewPager(imageList: ArrayList<DeleteMediaModel>, currentPosition: Int) {
        viewPagerAdapter = DeleteViewPagerAdapter(this, imageList)
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
                deleteselectedPosition = position
                deleteMediaModel = imageList[position]
                updateImageTitle(position)
            }
        })
    }

    private fun updateImageTitle(position: Int) {
        val fileName = File(imageListDelete[position].binPath).name  // Extract file name from path
        binding.tvtitile.text = fileName
    }
}