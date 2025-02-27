package com.gallery.photos.editpic.Fragment

import CreateNewFolderDialog
import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
import android.transition.TransitionManager
import android.util.Log
import android.view.LayoutInflater
import android.view.ScaleGestureDetector
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ActionMode
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gallery.photos.editpic.Activity.AllPhotosActivity
import com.gallery.photos.editpic.Activity.MyApplicationClass
import com.gallery.photos.editpic.Activity.SearchAct
import com.gallery.photos.editpic.Activity.ViewPagerActivity
import com.gallery.photos.editpic.Activity.ZoomInZoomOutAct
import com.gallery.photos.editpic.Adapter.RecentPictureAdapter
import com.gallery.photos.editpic.Dialogs.AllFilesAccessDialog
import com.gallery.photos.editpic.Dialogs.DeleteWithRememberDialog
import com.gallery.photos.editpic.Dialogs.SlideShowDialog
import com.gallery.photos.editpic.Extensions.beGone
import com.gallery.photos.editpic.Extensions.gone
import com.gallery.photos.editpic.Extensions.hasAllFilesAccessAs
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

    lateinit var gridLayoutManager: GridLayoutManager
    private lateinit var scaleGestureDetector: ScaleGestureDetector
    private var currentSpanCount = 4  // Default span count
    private val minSpanCount = 2
    private val maxSpanCount = 6

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
//            binding.searchiconid.visible()
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
        setupPinchToZoomGesture()
        observeViewModel()

        viewModel.loadRecentMedia()
    }

    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    private fun setupRecyclerView() {
        binding.apply {

            menuthreeid.onClick {
                val topmenurecentpopup = TopMenuRecentCustomPopup(requireActivity()) {
                    when (it) {
                        "llSort" -> {

                        }
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
                    (getString(R.string.max_selection_limit_is_100)).tos(requireActivity())
                }
            }

            llDelete.setOnClickListener {
                if (!hasAllFilesAccessAs(requireActivity())) {
                    (getString(R.string.all_files_access_required)).tos(requireActivity())
                    AllFilesAccessDialog(requireActivity()) {

                    }
//                    requireActivity()!!.startActivityWithBundle<AllFilePermissionActivity>(Bundle().apply {
//                        putString("isFrom", "Activitys")
//                    })

                    return@setOnClickListener
                }

                val selectedFiles = mediaAdapter.selectedItems.distinctBy { it.mediaPath }

                if (selectedFiles.size <= 100) {
                    DeleteWithRememberDialog(requireActivity(), false) {
                        CoroutineScope(Dispatchers.Main).launch {
                            // Show Progress Dialog
                            val progressDialog = ProgressDialog(requireActivity()).apply {
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
                                            deleteMediaDao!!.insertMedia(deleteMediaModel)  // Insert into Room DB

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
                            mediaAdapter.deleteSelectedItems()
                            mediaAdapter.unselectAllItems()
                            tvSelection.text = getString(R.string.pictures)
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
                                setMessage(getString(R.string.hiding_files))
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
//                                    searchiconid.visible()
                                    menuthreeid.visible()
                                    tvSelection.text = getString(R.string.pictures)

                                    Toast.makeText(
                                        requireActivity(),
                                        "$hiddenCount files hidden successfully",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    Toast.makeText(
                                        requireActivity(), getString(R.string.no_files_hidden),
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
//                            searchiconid.visible()
                            menuthreeid.visible()
                            tvSelection.text = getString(R.string.pictures)
                        }

                        "deselectall" -> {
                            mediaAdapter.unselectAllItems()
                            binding.selectedcontainerid.gone()
                            requireActivity().findViewById<RelativeLayout>(R.id.mainTopTabsContainer)
                                .visible()
//                            binding.searchiconid.visible()
                            binding.menuthreeid.visible()
                            binding.tvSelection.text = getString(R.string.pictures)
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
//                            binding.searchiconid.visible()
                            binding.menuthreeid.visible()
                            binding.tvSelection.text = getString(R.string.pictures)
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
//                            binding.searchiconid.visible()
                            binding.menuthreeid.visible()
                            binding.tvSelection.text = getString(R.string.pictures)
                        }
                    }
                }
                pictureBottom.show(llMore, 0, 0)
            }
        }

        mediaAdapter =
            RecentPictureAdapter(activity = requireActivity() as AppCompatActivity, // Pass activity for ActionMode
                onItemClick = { selectedMedia, position ->
                    ("Pos: $position").log()
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
//                        binding.searchiconid.visible()
                        binding.menuthreeid.visible()
                        binding.tvSelection.text = getString(R.string.pictures)
                    }
                })

        val layoutManager = GridLayoutManager(requireContext(), 3)
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                // Make headers span all 4 columns, while media items take 1 column
                return if (mediaAdapter.getItemViewType(position) == 0) 3 else 1
            }
        }


        binding.ivTopArrow.onClick {
            binding.recyclerViewRecentPictures.scrollToPosition(0)
            binding.ivTopArrow.gone()
        }

        binding.recyclerViewRecentPictures.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                if (firstVisibleItemPosition >= 50) {
                    if (binding.ivTopArrow.visibility == View.GONE) {
                        binding.ivTopArrow.visibility = View.VISIBLE
                        binding.ivTopArrow.animate()
                            .translationY(0f) // Move up
                            .alpha(1f)
                            .setDuration(300)
                            .start()
                    }
                } else {
                    if (binding.ivTopArrow.visibility == View.VISIBLE) {
                        binding.ivTopArrow.animate()
                            .translationY(100f) // Move down
                            .alpha(0f)
                            .setDuration(300)
                            .withEndAction { binding.ivTopArrow.visibility = View.GONE }
                            .start()
                    }
                }
            }
        })

        gridLayoutManager = GridLayoutManager(requireContext(), currentSpanCount)
        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (mediaAdapter.getItemViewType(position) == 0) currentSpanCount else 1
            }
        }

        binding.recyclerViewRecentPictures.apply {
            this.layoutManager = gridLayoutManager
            adapter = mediaAdapter
        }
        binding.recyclerViewRecentPictures.itemAnimator = CustomItemAnimator()
        binding.recyclerViewRecentPictures.scheduleLayoutAnimation()
    }

    class CustomItemAnimator : DefaultItemAnimator() {
        override fun animateChange(
            oldHolder: RecyclerView.ViewHolder,
            newHolder: RecyclerView.ViewHolder,
            preInfo: ItemHolderInfo,
            postInfo: ItemHolderInfo
        ): Boolean {
            return super.animateChange(oldHolder, newHolder, preInfo, postInfo)
        }
    }

    private fun setupPinchToZoomGesture() {
        scaleGestureDetector = ScaleGestureDetector(requireContext(),
            object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
                override fun onScale(detector: ScaleGestureDetector): Boolean {
                    val scaleFactor = detector.scaleFactor

                    // Define a threshold to prevent frequent small changes
                    val zoomThreshold = 1.1f

                    if (scaleFactor > zoomThreshold && currentSpanCount > minSpanCount) {
                        currentSpanCount--
                    } else if (scaleFactor < (1 / zoomThreshold) && currentSpanCount < maxSpanCount) {
                        currentSpanCount++
                    } else {
                        return false
                    }

                    // Smoothly update the span count with animation
                    TransitionManager.beginDelayedTransition(binding.recyclerViewRecentPictures)
                    gridLayoutManager.spanCount = currentSpanCount
                    binding.recyclerViewRecentPictures.adapter?.notifyItemRangeChanged(
                        0, binding.recyclerViewRecentPictures.adapter?.itemCount ?: 0
                    )
                    return true
                }
            })

        binding.recyclerViewRecentPictures.setOnTouchListener { _, event ->
            scaleGestureDetector.onTouchEvent(event)
            false
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

    override fun onResume() {
        super.onResume()
        ("onResume Fragment").log()
        if (isOneTime) {
            isOneTime = false
            requestMediaPermission()
        }
        viewModel.loadRecentMedia()
//        binding.recyclerViewRecentPictures.adapter?.notifyDataSetChanged() // Ensure UI updates

    }

    var isOneTime: Boolean = true

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
            if (mediaItems.isNotEmpty()) {
                if (MyApplicationClass.getBoolean("isAOne") == false) {
                    MyApplicationClass.putBoolean("isAOne", true)
                    startActivity(
                        Intent(
                            requireActivity(), ZoomInZoomOutAct::class.java
                        )
                    )
                    requireActivity().overridePendingTransition(R.anim.slide_in, R.anim.slide_out)
                }
            }

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

    private fun requestMediaPermission() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        if (ContextCompat.checkSelfPermission(
                requireActivity(), permission
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            Log.d("PermissionCheck", "Permission already granted: $permission")
        } else {
            if (shouldShowRequestPermissionRationale(permission)) {
                // Show an explanation dialog before requesting again
                showPermissionRationaleDialog(permission)
            } else {
                // Directly request permission
//                showPermissionRationaleDialog(permission)
                requestPermissionLauncher.launch(permission)
            }
        }
    }

    private fun showPermissionRationaleDialog(permission: String) {
        AlertDialog.Builder(requireContext()).setTitle("Permission Needed")
            .setMessage("This app requires access to your media to function properly.")
            .setCancelable(false)
            .setPositiveButton("OK") { _, _ ->
                requestPermissionLauncher.launch(permission)
            }.setNegativeButton("Cancel") { dialog, _ ->
                requireActivity().finishAffinity()
                dialog.dismiss()
            }.show()
    }

    // Handle the result of the permission request
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                Log.d("PermissionCheck", "Permission granted")
            } else {
                Log.e("PermissionCheck", "Permission denied")
//                if (!shouldShowRequestPermissionRationale(getRequiredPermission())) {
                showSettingsDialog()
//                }
            }
        }

    private fun getRequiredPermission(): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }
    }

    private val settingsLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (!isPermissionGranted()) {
            showSettingsDialog()  // Re-show dialog if permission is still denied
        }
    }

    private fun isPermissionGranted(): Boolean {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        return ContextCompat.checkSelfPermission(
            requireContext(), permission
        ) == PackageManager.PERMISSION_GRANTED
    }


    private fun showSettingsDialog() {
        AlertDialog.Builder(requireContext()).setTitle("Permission Required")
            .setMessage("You have denied the permission permanently. Please enable it in app settings.")
            .setCancelable(false)
            .setPositiveButton("Go to Settings") { _, _ ->
                val intent = Intent(ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", requireActivity().packageName, null)
                }
                settingsLauncher.launch(intent)  // Start settings screen
            }.setNegativeButton("Cancel") { dialog, _ ->
                ("Click rto Nagative Bitton").log()
                requireActivity().finishAffinity()
                dialog.dismiss()
            }.show()
    }

}