package com.gallery.photos.editpic.Fragment

import CreateNewFolderDialog
import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ActionMode
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.GridLayoutManager
import com.gallery.photos.editpic.Activity.AllPhotosActivity
import com.gallery.photos.editpic.Activity.SearchAct
import com.gallery.photos.editpic.Activity.ViewPagerActivity
import com.gallery.photos.editpic.Adapter.RecentPictureAdapter
import com.gallery.photos.editpic.Dialogs.DeleteWithRememberDialog
import com.gallery.photos.editpic.Dialogs.SlideShowDialog
import com.gallery.photos.editpic.Extensions.beGone
import com.gallery.photos.editpic.Extensions.gone
import com.gallery.photos.editpic.Extensions.invisible
import com.gallery.photos.editpic.Extensions.log
import com.gallery.photos.editpic.Extensions.name.getMediaDatabase
import com.gallery.photos.editpic.Extensions.notifyGalleryRoot
import com.gallery.photos.editpic.Extensions.onClick
import com.gallery.photos.editpic.Extensions.shareMultipleFiles
import com.gallery.photos.editpic.Extensions.startActivityWithBundle
import com.gallery.photos.editpic.Extensions.tos
import com.gallery.photos.editpic.Extensions.visible
import com.gallery.photos.editpic.Model.DeleteMediaModel
import com.gallery.photos.editpic.Model.FavouriteMediaModel
import com.gallery.photos.editpic.Model.HideMediaModel
import com.gallery.photos.editpic.Model.MediaListItem
import com.gallery.photos.editpic.Model.MediaModel
import com.gallery.photos.editpic.PopupDialog.PicturesBottomPopup
import com.gallery.photos.editpic.PopupDialog.TopMenuRecentCustomPopup
import com.gallery.photos.editpic.R
import com.gallery.photos.editpic.Repository.RecentPictureRepository
import com.gallery.photos.editpic.RoomDB.Dao.DeleteMediaDao
import com.gallery.photos.editpic.RoomDB.Dao.FavouriteMediaDao
import com.gallery.photos.editpic.RoomDB.Dao.HideMediaDao
import com.gallery.photos.editpic.Utils.MediaStoreSingleton
import com.gallery.photos.editpic.Utils.SelectionAlLPhotos.selectionArrayList
import com.gallery.photos.editpic.ViewModel.RecentPictureViewModel
import com.gallery.photos.editpic.ViewModel.RecentPictureViewModelFactory
import com.gallery.photos.editpic.databinding.FragmentRecentPictureBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException

class RecentsPictureFragment : Fragment() {
    private lateinit var favouriteMediaDao: FavouriteMediaDao
    private lateinit var binding: FragmentRecentPictureBinding
    private val viewModel: RecentPictureViewModel by viewModels {
        RecentPictureViewModelFactory(RecentPictureRepository(requireContext()))
    }
    private var mediaList: ArrayList<MediaModel> = arrayListOf() // Store full media list
    var hideMediaModel: HideMediaModel? = null
    private var favouriteList: ArrayList<FavouriteMediaModel> = arrayListOf()

    private lateinit var mediaAdapter: RecentPictureAdapter
    private var actionMode: ActionMode? = null
    var deleteMediaModel: DeleteMediaModel? = null
    var deleteMediaDao: DeleteMediaDao? = null
    var hideMediaDao: HideMediaDao? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentRecentPictureBinding.inflate(inflater, container, false)
        return binding.root
    }

    fun toggleTopBarVisibility(isVisible: Boolean) {
        binding.rlTop.visibility = View.VISIBLE
        if (isVisible) {
            binding.searchiconid.invisible()
            binding.menuthreeid.invisible()
        } else {
            binding.searchiconid.visible()
            binding.menuthreeid.visible()
            binding.selectedcontainerid.gone()
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
                    requireActivity(),
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).path,
                    selectedFiles,
                    isFromWhere = (data.extras?.getString("where")!!)
                ) { newAlbumPath ->
                    Log.d("NewAlbum", "Created new album at $newAlbumPath")
                    notifyGalleryRoot(
                        requireActivity(),
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).path
                    )
                    val intent = Intent("com.example.FOLDER_CREATED")
                    LocalBroadcastManager.getInstance(requireActivity()).sendBroadcast(intent)

                    ("Album created successfully").tos(requireActivity())
                }
            } else {
                Log.d("ActivityResult", "Result canceled or failed")
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        deleteMediaModel = DeleteMediaModel()
        deleteMediaDao = getMediaDatabase(requireActivity()).deleteMediaDao()
        hideMediaDao = getMediaDatabase(requireActivity()).hideMediaDao()
        favouriteMediaDao = getMediaDatabase(requireActivity()).favouriteMediaDao()

        hideMediaModel = HideMediaModel()
        setupRecyclerView()
        observeViewModel()
        viewModel.loadRecentMedia()
    }

    @SuppressLint("SetTextI18n")
    private fun setupRecyclerView() {
        binding.apply {

            menuthreeid.onClick {
                val topmenurecentpopup = TopMenuRecentCustomPopup(requireActivity()) {
                    when (it) {
                        "llStartSlide" -> {
                            SlideShowDialog(requireActivity()) {
                                when (it) {
                                    "lloneSec" -> {
                                        openViewPagerSlideShowActivity(mediaList, 0, 1)
                                    }

                                    "lltwoSec" -> {
                                        openViewPagerSlideShowActivity(mediaList, 0, 2)
                                    }

                                    "llthreeSec" -> {
                                        openViewPagerSlideShowActivity(mediaList, 0, 3)
                                    }

                                    "llfourSec" -> {
                                        openViewPagerSlideShowActivity(mediaList, 0, 4)
                                    }

                                    "llfiveSec" -> {
                                        openViewPagerSlideShowActivity(mediaList, 0, 5)
                                    }

                                }
                            }
                        }

                        "llSelectAll" -> {
                            mediaAdapter.selectAllItems()
                            binding.selectedcontainerid.visible()
                            binding.searchiconid.invisible()
                            binding.menuthreeid.gone()
                        }
                    }
                }
                topmenurecentpopup.show(menuthreeid, 0, 0)
            }

            searchiconid.onClick {
                requireActivity().startActivityWithBundle<SearchAct>()
            }

            llShare.onClick {
                val selectedFiles = mediaAdapter.selectedItems.distinctBy { it.mediaPath }
                if (selectedFiles.size <= 100) {
                    shareMultipleFiles(selectedFiles, requireActivity())
                } else {
                    ("Max selection limit is 100").tos(requireActivity())
                }
            }

            llDelete.onClick {
                val selectedFiles = mediaAdapter.selectedItems.distinctBy { it.mediaPath }
                if (selectedFiles.size <= 100) {
                    DeleteWithRememberDialog(requireActivity(), false) {
                        CoroutineScope(Dispatchers.Main).launch {
                            // Show Progress Dialog
                            val progressDialog = ProgressDialog(requireActivity())
                            progressDialog.setMessage("Deleting files...")
                            progressDialog.setCancelable(false)
                            progressDialog.show()

                            withContext(Dispatchers.IO) {
                                val deletionJobs = mediaAdapter.selectedItems.map {
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
                            mediaAdapter.deleteSelectedItems()
                            mediaAdapter.unselectAllItems()
                            tvSelection.text = "Pictures"
                            viewModel.loadRecentMedia()
                        }
                    }
                } else {
                    ("Max selection limit is 100").tos(requireActivity())
                }
            }

            llMore.onClick {
                val pictureBottom = PicturesBottomPopup(requireActivity(), true) {
                    when (it) {
                        "llAddTohide" -> {
                            val progressDialog = ProgressDialog(requireActivity()).apply {
                                setMessage("Hiding files...")
                                setCancelable(false)
                                show()
                            }

                            CoroutineScope(Dispatchers.Main).launch {
                                val hiddenItems = mediaAdapter?.selectedItems ?: mutableListOf()

                                val hiddenCount = withContext(Dispatchers.IO) {
                                    hiddenItems.count { hideSelectedItems(it) }
                                }

                                progressDialog.dismiss()

                                if (hiddenCount > 0) {
                                    // Remove items from the list and update adapter
                                    mediaList.removeAll(hiddenItems)

                                    mediaAdapter.unselectAllItems()
                                    mediaAdapter.notifyDataSetChanged() // Notify RecyclerView that data has changed

                                    // UI updates
                                    selectedcontainerid.gone()
                                    searchiconid.visible()
                                    menuthreeid.visible()
                                    tvSelection.text = "Pictures"

                                    Toast.makeText(
                                        requireActivity(),
                                        "$hiddenCount files hidden successfully",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    Toast.makeText(
                                        requireActivity(),
                                        "No files hidden",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }

                        "llFavourite" -> {
                            CoroutineScope(Dispatchers.IO).launch {
                                mediaAdapter!!.selectedItems.forEach {
                                    favouriteMediaDao.deleteMedia(FavouriteMediaModel().apply {
                                        mediaId = it.mediaId
                                        mediaName = it.mediaName
                                        mediaPath = it.mediaPath
                                        mediaMimeType = it.mediaMimeType
                                        mediaDateAdded = it.mediaDateAdded
                                        isVideo = it.isVideo
                                        displayDate = it.displayDate
                                        isSelect = it.isSelect
                                    })
                                }
                            }

                            mediaAdapter.unselectAllItems()
                            selectedcontainerid.gone()
                            searchiconid.visible()
                            menuthreeid.visible()
                            tvSelection.text = "Pictures"
                        }

                        "deselectall" -> {
                            mediaAdapter.unselectAllItems()
                            binding.selectedcontainerid.gone()
                            requireActivity().findViewById<RelativeLayout>(R.id.mainTopTabsContainer)
                                .visible()
                            binding.searchiconid.visible()
                            binding.menuthreeid.visible()
                            binding.tvSelection.text = "Pictures"
                        }

                        "selectallid" -> {
                            binding.selectedcontainerid.visible()
                            mediaAdapter.selectAllItems()
                            binding.searchiconid.invisible()
                            binding.menuthreeid.gone()
                        }

                        "movetoid" -> {
                            selectionArrayList.clear()
                            selectionArrayList =
                                mediaAdapter.selectedItems.map { it.mediaPath } as ArrayList<String>

                            activityResultLauncher.launch(
                                Intent(
                                    requireActivity(), AllPhotosActivity::class.java
                                ).putExtra("from", "Move")
                            )

                            mediaAdapter.unselectAllItems()
                            binding.selectedcontainerid.gone()
                            requireActivity().findViewById<RelativeLayout>(R.id.mainTopTabsContainer)
                                .visible()
                            binding.searchiconid.visible()
                            binding.menuthreeid.visible()
                            binding.tvSelection.text = "Pictures"
                        }

                        "copytoid" -> {
                            selectionArrayList.clear()
                            selectionArrayList =
                                mediaAdapter.selectedItems.map { it.mediaPath } as ArrayList<String>

                            activityResultLauncher.launch(
                                Intent(
                                    requireActivity(), AllPhotosActivity::class.java
                                ).putExtra("from", "Copy")
                            )

                            mediaAdapter.unselectAllItems()
                            binding.selectedcontainerid.gone()
                            requireActivity().findViewById<RelativeLayout>(R.id.mainTopTabsContainer)
                                .visible()
                            binding.searchiconid.visible()
                            binding.menuthreeid.visible()
                            binding.tvSelection.text = "Pictures"
                        }
                    }
                }
                pictureBottom.show(llMore, 0, 0)
            }
        }

        mediaAdapter =
            RecentPictureAdapter(activity = requireActivity() as AppCompatActivity, // Pass activity for ActionMode
                onItemClick = { selectedMedia, position ->
                    openViewPagerActivity(selectedMedia, position)
                }, onLongItemClick = {
                    ("LongClick: " + it).log()
                    if (it) {
                        binding.selectedcontainerid.visible()
                        requireActivity().findViewById<RelativeLayout>(R.id.mainTopTabsContainer)
                            .gone()
                        binding.searchiconid.invisible()
                        binding.menuthreeid.gone()
                    } else {
                        binding.selectedcontainerid.gone()
                        requireActivity().findViewById<RelativeLayout>(R.id.mainTopTabsContainer)
                            .visible()
                        binding.searchiconid.visible()
                        binding.menuthreeid.visible()
                        binding.tvSelection.text = "Pictures"
                    }
                })

        val layoutManager = GridLayoutManager(requireContext(), 4)
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                // Make headers span all 4 columns, while media items take 1 column
                return if (mediaAdapter.getItemViewType(position) == 0) 4 else 1
            }
        }

        binding.recyclerViewRecentPictures.apply {
            this.layoutManager = layoutManager
            this.adapter = mediaAdapter
        }
    }

    private fun hideSelectedItems(mediaModel: MediaModel): Boolean {
        val originalFile = File(mediaModel.mediaPath)
        if (!originalFile.exists()) {
            Log.e("RenameAndHide", "File not found: ${mediaModel.mediaPath}")
            return false
        }

        val hiddenFile = File(originalFile.parentFile, ".${originalFile.name}")

        mediaModel.apply {
            hideMediaModel!!.mediaId = mediaId
            hideMediaModel!!.mediaName = hiddenFile.name
            hideMediaModel!!.mediaPath = hiddenFile.path
            hideMediaModel!!.mediaMimeType = mediaMimeType
            hideMediaModel!!.mediaDateAdded = mediaDateAdded
            hideMediaModel!!.isVideo = isVideo
            hideMediaModel!!.displayDate = displayDate
            hideMediaModel!!.isSelect = isSelect
        }

        if (originalFile.renameTo(hiddenFile)) {
            Log.d("RenameAndHide", "File hidden: ${hiddenFile.absolutePath}")

            CoroutineScope(Dispatchers.IO).launch {
                favouriteMediaDao?.getMediaById(hideMediaModel!!.mediaId)?.let {
                    favouriteMediaDao?.deleteMedia(it)
                }
                hideMediaDao?.insertMedia(hideMediaModel!!)
            }
            return true
        } else {
            Log.e("RenameAndHide", "Failed to hide file: ${originalFile.path}")
            return false
        }
    }

    fun createRecycleBin(): File {
        val recycleBin =
            File(requireActivity().getExternalFilesDir(null), ".gallery_recycleBin")
        if (!recycleBin.exists()) {
            if (recycleBin.mkdirs()) {
                Log.d("RecycleBin", "Recycle bin created at: ${recycleBin.absolutePath}")
            } else {
                Log.e(
                    "RecycleBin",
                    "Failed to create recycle bin at: ${recycleBin.absolutePath}"
                )
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
            Log.d(
                "MoveToRecycleBin",
                "Moving file to recycle bin: ${originalFile.absolutePath}"
            )

            originalFile.copyTo(recycledFile, overwrite = true)  // Copy to recycle bin
            Log.d(
                "MoveToRecycleBin",
                "File copied to recycle bin: ${recycledFile.absolutePath}"
            )

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
        ("onResume Fragment").log()
        viewModel.loadRecentMedia()
        binding.recyclerViewRecentPictures.adapter?.notifyDataSetChanged() // Ensure UI updates

    }

    private fun openViewPagerActivity(selectedMedia: List<MediaModel>, position: Int) {
        // Store images & position in Singleton to avoid large transaction errors
        MediaStoreSingleton.imageList = mediaList
        MediaStoreSingleton.selectedPosition = position

        val intent = Intent(requireContext(), ViewPagerActivity::class.java)
        startActivity(intent)
    }


    private fun openViewPagerSlideShowActivity(
        selectedMedia: List<MediaModel>,
        position: Int,
        secound: Int
    ) {
        // Store images & position in Singleton to avoid large transaction errors

        MediaStoreSingleton.imageList = mediaList
        MediaStoreSingleton.selectedPosition = position

        val intent = Intent(requireContext(), ViewPagerActivity::class.java)
        intent.putExtra("slideshow", true)
        intent.putExtra("secoundSlideShow", secound)
        startActivity(intent)
    }


    @SuppressLint("NotifyDataSetChanged")
    private fun observeViewModel() {

        lifecycleScope.launch {
            getMediaDatabase(requireActivity()).favouriteMediaDao().getAllMediaLive()
                .observe(requireActivity()) {
                    favouriteList.clear()
                    favouriteList.addAll(it)
                    mediaList.forEach { media ->
                        media.isFav =
                            favouriteList.find { it.mediaId == media.mediaId }?.isFav == true
                    }
                    mediaAdapter.notifyDataSetChanged()
                }
        }

        viewModel.mediaLiveData.observe(viewLifecycleOwner) { mediaItems ->
            ("Size: ${mediaItems.size}").log()
            // Extract only MediaModel items (exclude headers)

            mediaList = mediaItems.filterIsInstance<MediaListItem.Media>()
                .map { it.media } as ArrayList<MediaModel>

            mediaList.forEach { media ->
                media.isFav = favouriteList.find { it.mediaId == media.mediaId }?.isFav == true
            }

            mediaAdapter.submitList(mediaItems)
            binding.shimmerLayout.shimmerLayout.beGone()
        }
    }
}