package com.gallery.photos.editpic.Fragment

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.gallery.photos.editpic.Activity.AllPhotosActivity
import com.gallery.photos.editpic.Activity.VideoViewPagerActivity
import com.gallery.photos.editpic.Adapter.VideoAdapter
import com.gallery.photos.editpic.Dialogs.AllFilesAccessDialog
import com.gallery.photos.editpic.Dialogs.CreateNewFolderDialog
import com.gallery.photos.editpic.Dialogs.DeleteWithRememberDialog
import com.gallery.photos.editpic.Extensions.formatDate
import com.gallery.photos.editpic.Extensions.gone
import com.gallery.photos.editpic.Extensions.hasAllFilesAccessAs
import com.gallery.photos.editpic.Extensions.isVideoFile
import com.gallery.photos.editpic.Extensions.log
import com.gallery.photos.editpic.Extensions.name.getMediaDatabase
import com.gallery.photos.editpic.Extensions.notifyGalleryRoot
import com.gallery.photos.editpic.Extensions.onClick
import com.gallery.photos.editpic.Extensions.shareMultipleFilesVideo
import com.gallery.photos.editpic.Extensions.tos
import com.gallery.photos.editpic.Extensions.visible
import com.gallery.photos.editpic.Model.DeleteMediaModel
import com.gallery.photos.editpic.Model.FavouriteMediaModel
import com.gallery.photos.editpic.Model.MediaModel
import com.gallery.photos.editpic.Model.VideoModel
import com.gallery.photos.editpic.PopupDialog.PicturesBottomPopup
import com.gallery.photos.editpic.PopupDialog.TopMenuVideosCustomPopup
import com.gallery.photos.editpic.R
import com.gallery.photos.editpic.RoomDB.Dao.DeleteMediaDao
import com.gallery.photos.editpic.RoomDB.Dao.FavouriteMediaDao
import com.gallery.photos.editpic.Utils.SelectionAlLPhotos.selectionArrayList
import com.gallery.photos.editpic.Utils.SelectionModeListener
import com.gallery.photos.editpic.Utils.VideoMediaStoreSingleton
import com.gallery.photos.editpic.ViewModel.VideoViewModel
import com.gallery.photos.editpic.ViewModel.VideoViewModelFactory
import com.gallery.photos.editpic.databinding.FragmentVideosBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException

class AllVideosFragment : Fragment() {

    private var _binding: FragmentVideosBinding? = null
    private val binding get() = _binding!!

    private val viewModel: VideoViewModel by viewModels { VideoViewModelFactory(requireActivity()) }
    private lateinit var videoAdapter: VideoAdapter
    private var favouriteList: ArrayList<FavouriteMediaModel> = arrayListOf()
    var deleteMediaModel: DeleteMediaModel? = null
    var deleteMediaDao: DeleteMediaDao? = null
    private lateinit var favouriteMediaDao: FavouriteMediaDao

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVideosBinding.inflate(inflater, container, false)
        return binding.root
    }

    var activityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data = result.data

                if (data?.extras?.getString("isFrom") != "CreateClick") return@registerForActivityResult
                // Handle the result here
                Log.d("ActivityResult", "Result received successfully")
                val selectedFiles = selectionArrayList

                if (selectedFiles.isEmpty()) {
                    Log.e("CreateNewFolder", "No files selected for moving")
                    return@registerForActivityResult
                }

                try {

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
                        val fragmentB =
                            parentFragmentManager.findFragmentByTag("FragmentBTag") as? AlbumFragment
                        fragmentB?.refreshFolder()

                        ("Album created successfully").tos(requireActivity())
                    }
                } catch (e: Exception) {
                    Log.e("CreateNewFolder", "Error creating folder dialog", e)
                    "Failed to create folder".tos(requireActivity())
                }
            } else {
                Log.d("ActivityResult", "Result canceled or failed")
            }
        }

    fun toggleTopBarVisibility(isVisible: Boolean) {
        binding.recyclerViewVideos.visibility = View.VISIBLE
        ("is Visisble: $isVisible").log()
        if (isVisible) {
            binding.selectedcontainerVideo.visible()
        } else {
            binding.selectedcontainerVideo.gone()
//            binding.ivSearch.visible()
            binding.menuDot.visible()
        }
    }

    private fun checkPermissions() {
        val permission = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> {
                // Android 15 (API 34) and above
                Manifest.permission.READ_MEDIA_VIDEO
            }

            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                // Android 13+ (API 33)
                Manifest.permission.READ_MEDIA_VIDEO
            }

            else -> {
                // Android 12 and below
                Manifest.permission.READ_EXTERNAL_STORAGE
            }
        }

        if (ContextCompat.checkSelfPermission(
                requireActivity(), permission
            ) == PackageManager.PERMISSION_GRANTED
        ) {
//            getAllVideos(requireActivity())
        } else {
            requestPermissionLauncher.launch(permission)
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
//            getAllVideos(requireActivity())
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkPermissions()
        setupRecyclerView()
        favouriteMediaDao = getMediaDatabase(requireActivity()).favouriteMediaDao()
        observeViewModel()
        setUpListioner()
        viewModel.loadAllVideos()
    }

    private fun setUpListioner() {
        deleteMediaModel = DeleteMediaModel()
        deleteMediaDao = getMediaDatabase(requireActivity()).deleteMediaDao()

        binding.apply {
            ivBack.onClick {
                if (videoAdapter.selectedItems.isEmpty()) {
                    requireActivity().finish()
                } else {
                    videoAdapter.disableSelectionMode()
                }
            }

            ivSearch.onClick { }

            menuDot.onClick {
                val videoMenu = TopMenuVideosCustomPopup(requireActivity()) {
                    when (it) {
                        "llStartSlide" -> {

                        }

                        "llSelectAll" -> {
                            videoAdapter.selectAllItems()
                            binding.selectedcontainerVideo.visible()
//                            binding.ivSearch.gone()
                            binding.menuDot.gone()
                        }
                    }
                }
                videoMenu.show(menuDot, 0, 0)
            }
            llShare.onClick {
                val selectedFiles = videoAdapter.selectedItems.distinctBy { it.videoPath }
                if (selectedFiles.size <= 100) {
                    shareMultipleFilesVideo(selectedFiles, requireActivity())
                } else {
                    (getString(R.string.max_selection_limit_is_100)).tos(requireActivity())
                }
            }

            llDelete.setOnClickListener {
                if (!hasAllFilesAccessAs(requireActivity())) {
                    (requireActivity().getString(R.string.all_files_access_required)).tos(
                        requireActivity()
                    )
//                    activity.startActivityWithBundle<AllFilePermissionActivity>(Bundle().apply {
//                        putString("isFrom", "Activitys")
//                    })
                    AllFilesAccessDialog(requireActivity()) {

                    }
                    return@setOnClickListener
                }

                val selectedFiles = videoAdapter.selectedItems.distinctBy { it.videoPath }

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
                                            mediaId = mediaItem.videoId,
                                            mediaName = mediaItem.videoName,
                                            mediaPath = mediaItem.videoPath,
                                            mediaMimeType = "mp4",
                                            mediaDateAdded = mediaItem.videoDateAdded,
                                            isVideo = isVideoFile(mediaItem.videoPath),
                                            displayDate = formatDate(mediaItem.videoDateAdded),
                                            isSelect = mediaItem.isSelect
                                        )

                                        val isMoved = moveToRecycleBin(deleteMediaModel.mediaPath)
                                        if (isMoved) {
                                            deleteMediaModel.binPath = File(
                                                createRecycleBin(), mediaItem.videoName
                                            ).absolutePath
                                            deleteMediaDao!!.insertMedia(deleteMediaModel)  // Insert into Recycle Bin

                                            favouriteMediaDao.getMediaById(mediaItem.videoId)
                                                ?.let { it1 -> favouriteMediaDao.deleteMedia(it1) }

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
                            videoAdapter.deleteSelectedItems()
                            videoAdapter.unselectAllItems()
                            tvTitalVideo.text = getString(R.string.videos)
//                            viewModel.loadRecentMedia()
                        }
                    }
                } else {
                    (getString(R.string.max_selection_limit_is_100)).tos(requireActivity())
                }
            }

            llMore.onClick {
                val isSelectAll = (videoAdapter?.selectedItems?.size != mediaListCheck.size)

                val pictureBottom = PicturesBottomPopup(requireActivity(), isSelectAll) {
                    when (it) {
                        "deselectall" -> {
                            try {
                            videoAdapter.unselectAllItems()
                            binding.selectedcontainerVideo.gone()
                            requireActivity().findViewById<RelativeLayout>(R.id.mainTopTabsContainer)
                                .visible()
//                            binding.ivSearch.visible()
                            binding.menuDot.visible()
                            binding.tvTitalVideo.text = getString(R.string.videos)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }

                        "selectallid" -> {
                            binding.selectedcontainerVideo.visible()
                            videoAdapter.selectAllItems()
                        }

                        "movetoid" -> {
                            selectionArrayList.clear()
                            selectionArrayList =
                                videoAdapter.selectedItems.map { it.videoPath } as ArrayList<String>

                            activityResultLauncher.launch(
                                Intent(
                                    requireActivity(),
                                    AllPhotosActivity::class.java
                                ).putExtra("from", "Move")
                            )

                            videoAdapter.unselectAllItems()
                            binding.selectedcontainerVideo.gone()
//                            binding.ivSearch.visible()
                            binding.menuDot.visible()
                            binding.tvTitalVideo.text = getString(R.string.videos)
                        }

                        "copytoid" -> {
                            selectionArrayList.clear()
                            selectionArrayList =
                                videoAdapter.selectedItems.map { it.videoPath } as ArrayList<String>

                            activityResultLauncher.launch(
                                Intent(
                                    requireActivity(),
                                    AllPhotosActivity::class.java
                                ).putExtra("from", "Copy")
                            )

                            videoAdapter.unselectAllItems()
                            binding.selectedcontainerVideo.gone()
//                            binding.ivSearch.visible()
                            binding.menuDot.visible()
                            binding.tvTitalVideo.text = getString(R.string.videos)
                        }
                    }
                }
                pictureBottom.show(llMore, 0, 0)
            }

        }
    }

    fun createRecycleBin(): File {
        val recycleBin = File(requireActivity().getExternalFilesDir(null), ".gallery_recycleBin")
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
        viewModel.loadAllVideos()
    }

    private fun setupRecyclerView() {
        videoAdapter = VideoAdapter(requireActivity(),object : SelectionModeListener{
            override fun toggleTopBar(show: Boolean) {
                toggleTopBarVisibility(show)
            }

        },{
            VideoMediaStoreSingleton.videoimageList = ArrayList(videoAdapter.currentList)
            VideoMediaStoreSingleton.videoselectedPosition = videoAdapter.currentList.indexOf(it)
            val intent = Intent(requireActivity(), VideoViewPagerActivity::class.java)
            startActivity(intent)
        }, { onLongItemClick ->
            if (onLongItemClick) {
                binding.selectedcontainerVideo.visible()
//                binding.ivSearch.gone()
                binding.menuDot.gone()

            } else {
                binding.tvTitalVideo.text = getString(R.string.videos)
//                binding.ivSearch.visible()
                binding.menuDot.visible()
                binding.selectedcontainerVideo.gone()
            }
        })
        binding.recyclerViewVideos.apply {
            layoutManager = GridLayoutManager(requireContext(), 4)
            adapter = videoAdapter
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun observeViewModel() {
        lifecycleScope.launch {
            getMediaDatabase(requireActivity()).favouriteMediaDao().getAllMediaLive()
                .observe(requireActivity()) {
                    favouriteList.clear()
                    favouriteList.addAll(it)
                    favouriteList.forEach { media ->
                        media.isFav =
                            favouriteList.find { it.mediaId == media.mediaId }?.isFav == true
                    }
                    videoAdapter.notifyDataSetChanged()
                }
        }

        viewModel.videosLiveData.observe(viewLifecycleOwner) { videos ->
            mediaListCheck.clear()
            if (videos.isEmpty()) {
                binding.tvDataNotFound.visible()
                binding.recyclerViewVideos.gone()
                binding.menuDot.gone()
            } else {
                binding.tvDataNotFound.gone()
                binding.recyclerViewVideos.visible()
                binding.menuDot.visible()
            }

            videos.forEach { media ->
                media.isFav = favouriteList.find { it.mediaId == media.videoId }?.isFav == true
            }
            mediaListCheck.addAll(videos)

            videoAdapter.submitList(videos)
        }
    }
    var mediaListCheck: ArrayList<VideoModel> = arrayListOf()

}