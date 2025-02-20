package com.gallery.photos.editpic.Activity

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.gallery.photos.editpic.Adapter.HideAdapter
import com.gallery.photos.editpic.Dialogs.DeleteWithRememberDialog
import com.gallery.photos.editpic.Dialogs.SlideShowDialog
import com.gallery.photos.editpic.Extensions.PREF_LANGUAGE_CODE
import com.gallery.photos.editpic.Extensions.gone
import com.gallery.photos.editpic.Extensions.handleBackPress
import com.gallery.photos.editpic.Extensions.log
import com.gallery.photos.editpic.Extensions.name.getMediaDatabase
import com.gallery.photos.editpic.Extensions.onClick
import com.gallery.photos.editpic.Extensions.setLanguageCode
import com.gallery.photos.editpic.Extensions.shareMultipleFilesHide
import com.gallery.photos.editpic.Extensions.tos
import com.gallery.photos.editpic.Extensions.visible
import com.gallery.photos.editpic.Model.DeleteMediaModel
import com.gallery.photos.editpic.Model.HideMediaModel
import com.gallery.photos.editpic.PopupDialog.HideItemsBottomPopup
import com.gallery.photos.editpic.PopupDialog.TopMenuHideActCustomPopup
import com.gallery.photos.editpic.R
import com.gallery.photos.editpic.RoomDB.Dao.DeleteMediaDao
import com.gallery.photos.editpic.RoomDB.Dao.HideMediaDao
import com.gallery.photos.editpic.Utils.HideMediaStoreSingleton
import com.gallery.photos.editpic.databinding.ActivityHideBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class HideActivity : AppCompatActivity() {
    private var hideadapter: HideAdapter? = null
    private lateinit var hideMediaDao: HideMediaDao
    lateinit var bind: ActivityHideBinding
    private var hideList: ArrayList<HideMediaModel> = arrayListOf()
    var deleteMediaModel: DeleteMediaModel? = null
    var deleteMediaDao: DeleteMediaDao? = null

    fun toggleTopBarVisibility(isVisible: Boolean) {
        bind.rvHide.visibility = View.VISIBLE
        ("is Fasvoutrite Visisble: $isVisible").log()
        if (isVisible) {
            bind.selectedcontainerHideiteid.visible()
        } else {
            bind.selectedcontainerHideiteid.gone()
//            binding.ivSearch.visible()
            bind.menuHide.visible()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLanguageCode(this, MyApplicationClass.getString(PREF_LANGUAGE_CODE)!!)
        bind = ActivityHideBinding.inflate(layoutInflater)
        setContentView(bind.root)


        handleBackPress {
            if (bind.tvHide.text != getString(R.string.hide)) {
                hideadapter!!.unselectAllItems()
                bind.tvHide.text = getString(R.string.hide)
            } else {
                finish()
            }
        }

        deleteMediaModel = DeleteMediaModel()
        deleteMediaDao = getMediaDatabase(this).deleteMediaDao()
        hideMediaDao = getMediaDatabase(this).hideMediaDao()

        hideMediaDao.getAllMediaLive().observe(this) {
            hideList.clear()
            hideList.addAll(it)

            runOnUiThread {
                if (hideList.isNotEmpty()) {
                    bind.rvHide.visible()
                    bind.menuHide.visible()
                    bind.tvDataNotFound.gone()
                    bind.rvHide.adapter?.notifyDataSetChanged()
                } else {
                    bind.rvHide.gone()
                    bind.menuHide.gone()
                    bind.tvDataNotFound.visible()
                }
            }
        }

        bind.apply {
            hideadapter = HideAdapter(this@HideActivity, hideList) { onLongItemClick ->
                if (onLongItemClick) {
                    bind.selectedcontainerHideiteid.visible()
//                binding.ivSearch.gone()
                    bind.menuHide.gone()

                } else {
                    bind.tvHide.text = getString(R.string.hide)
//                binding.ivSearch.visible()
                    bind.selectedcontainerHideiteid.gone()
                    bind.menuHide.visible()
                }
            }
            rvHide.adapter = hideadapter

            ivBack.onClick {
                if (bind.tvHide.text != getString(R.string.hide)) {
                    hideadapter!!.unselectAllItems()
                    bind.tvHide.text = getString(R.string.hide)
                } else {
                    finish()
                }
            }


            llShare.onClick {
                val selectedFiles = hideadapter!!.selectedItems.distinctBy { it.mediaPath }
                if (selectedFiles.size <= 100) {
                    shareMultipleFilesHide(selectedFiles, this@HideActivity)
                } else {
                    (getString(R.string.max_selection_limit_is_100)).tos(this@HideActivity)
                }
            }

            llMore.onClick {
                val hideDialog = HideItemsBottomPopup(this@HideActivity) {
                    when (it) {

                        "selectallid" -> {
                            bind.selectedcontainerHideiteid.visible()
                            hideadapter!!.selectAllItems()
                            searchiconid.gone()
                            menuHide.gone()
                        }

                        "llUnHideAll" -> {
                            val progressDialog = ProgressDialog(this@HideActivity).apply {
                                setMessage(getString(R.string.unhiding_files))
                                setCancelable(false)
                                show()
                            }

                            CoroutineScope(Dispatchers.Main).launch {
                                withContext(Dispatchers.IO) {
                                    hideadapter?.selectedItems?.forEach {
                                        unHideAll(it) // This will wait for each item to finish before proceeding
                                    }
                                }
                                Toast.makeText(
                                    this@HideActivity,
                                    "${hideadapter?.selectedItems?.size} items unhidden successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                                progressDialog.dismiss()
                                hideadapter!!.deleteSelectedItems()
                                hideadapter!!.unselectAllItems()
                                tvHide.text = getString(R.string.hide)
                            }
                        }
                    }
                }
                hideDialog.show(llMore, 0, 0)
            }

            llDelete.onClick {
                ("Click").log()
                val selectedFiles = hideadapter!!.selectedItems.distinctBy { it.mediaPath }
                if (selectedFiles.size <= 100) {
                    DeleteWithRememberDialog(this@HideActivity, true) {
                        CoroutineScope(Dispatchers.Main).launch {
                            // Show Progress Dialog
                            val progressDialog = ProgressDialog(this@HideActivity)
                            progressDialog.setMessage(getString(R.string.deleting_files))
                            progressDialog.setCancelable(false)
                            progressDialog.show()

                            withContext(Dispatchers.IO) {
                                val deletionJobs = hideadapter!!.selectedItems.map {
                                    async {
                                        permanentlyDeleteFile(
                                            it
                                        )
                                    }
                                }
                                // Wait for all deletion tasks to complete
                                deletionJobs.awaitAll()
                            }

                            // Dismiss Progress Dialog and update UI
                            progressDialog.dismiss()
                            hideadapter!!.deleteSelectedItems()
                            hideadapter!!.unselectAllItems()
                            tvHide.text = getString(R.string.hide)
                        }
                    }
                } else {
                    (getString(R.string.max_selection_limit_is_100)).tos(this@HideActivity)
                }
            }

            menuHide.onClick {
                val topcustomtopcustompopup = TopMenuHideActCustomPopup(this@HideActivity) {
                    when (it) {
                        "llStartSlide" -> {
                            SlideShowDialog(this@HideActivity) {
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
                            hideadapter!!.selectAllItems()
                            menuHide.gone()
                        }
                    }
                }
                topcustomtopcustompopup.show(menuHide, 0, 0)
            }
        }
    }

    private fun openViewPagerSlideShowActivity(position: Int, sec: Int) {
        HideMediaStoreSingleton.hideimageList = hideList
        HideMediaStoreSingleton.hideselectedPosition = position

        val intent = Intent(this, HideViewPagerActivity::class.java)
        intent.putExtra("slideshow", true)
        intent.putExtra("secoundSlideShow", sec)
        startActivity(intent)
    }

    private suspend fun unHideAll(hideMediaModel: HideMediaModel) {
        val filePath = hideMediaModel.mediaPath
        val hiddenFile = File(filePath)

        if (hiddenFile.exists()) {
            val unhiddenFile = File(hiddenFile.parent, hiddenFile.name.removePrefix("."))

            if (hiddenFile.renameTo(unhiddenFile)) {
                Log.d("File", "File unhidden: ${unhiddenFile.absolutePath}")

                // Ensure database operation is completed
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


    fun permanentlyDeleteFile(mediaItem: HideMediaModel) {
        Log.d("PermanentlyDelete", "Bin Path: ${mediaItem.mediaPath}")
        CoroutineScope(Dispatchers.IO).launch {
            val binFile = File(mediaItem.mediaPath)
            if (binFile.exists()) {
                if (binFile.delete()) {
                    Log.d("PermanentlyDelete", "File deleted: ${binFile.absolutePath}")
                    hideMediaDao.deleteMedia(mediaItem)  // Remove from Room database

                    runOnUiThread {
                        (getString(R.string.file_permanently_deleted)).tos(this@HideActivity)
                        hideList.remove(mediaItem)
                    }
                } else {
                    Log.e("PermanentlyDelete", "Failed to delete file: ${binFile.absolutePath}")
                }
            } else {
                hideMediaDao.deleteMedia(mediaItem)  // Remove from Room database
                hideList.remove(mediaItem)
                Log.e("PermanentlyDelete", "File not found: ${binFile.absolutePath}")
            }
        }
    }
}