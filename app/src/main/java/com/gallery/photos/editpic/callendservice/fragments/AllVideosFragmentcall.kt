package com.gallery.photos.editpic.Fragment

import com.gallery.photos.editpic.Dialogs.CreateNewFolderDialog
import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.gallery.photos.editpic.Activity.MainActivity
import com.gallery.photos.editpic.Adapter.VideoAdapter
import com.gallery.photos.editpic.Extensions.gone
import com.gallery.photos.editpic.Extensions.log
import com.gallery.photos.editpic.Extensions.name.getMediaDatabase
import com.gallery.photos.editpic.Extensions.notifyGalleryRoot
import com.gallery.photos.editpic.Extensions.tos
import com.gallery.photos.editpic.Extensions.visible
import com.gallery.photos.editpic.Model.DeleteMediaModel
import com.gallery.photos.editpic.Model.FavouriteMediaModel
import com.gallery.photos.editpic.RoomDB.Dao.DeleteMediaDao
import com.gallery.photos.editpic.RoomDB.Dao.FavouriteMediaDao
import com.gallery.photos.editpic.Utils.SelectionAlLPhotos.selectionArrayList
import com.gallery.photos.editpic.Utils.SelectionModeListener
import com.gallery.photos.editpic.Utils.VideoMediaStoreSingleton
import com.gallery.photos.editpic.ViewModel.VideoViewModel
import com.gallery.photos.editpic.ViewModel.VideoViewModelFactory
import com.gallery.photos.editpic.databinding.FragmentVideosCallBinding
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException

class AllVideosFragmentcall : Fragment() {

    private var _binding: FragmentVideosCallBinding? = null
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
        _binding = FragmentVideosCallBinding.inflate(inflater, container, false)
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
                    val fragmentB =
                        parentFragmentManager.findFragmentByTag("FragmentBTag") as? AlbumFragment
                    fragmentB?.refreshFolder()

                    ("Album created successfully").tos(requireActivity())
                }
            } else {
                Log.d("ActivityResult", "Result canceled or failed")
            }
        }

    fun toggleTopBarVisibility(isVisible: Boolean) {
        binding.recyclerViewVideos.visibility = View.VISIBLE
        ("is Visisble: $isVisible").log()
        if (isVisible) {
        } else {
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
        videoAdapter = VideoAdapter(requireActivity(), object : SelectionModeListener {
            override fun toggleTopBar(show: Boolean) {
                toggleTopBarVisibility(show)
            }

        }, {
            VideoMediaStoreSingleton.videoimageList = ArrayList(videoAdapter.currentList)
            VideoMediaStoreSingleton.videoselectedPosition = videoAdapter.currentList.indexOf(it)
            val intent = Intent(requireActivity(), MainActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }, { onLongItemClick ->
            if (onLongItemClick) {

            } else {
            }
        })
        binding.recyclerViewVideos.apply {
            layoutManager = GridLayoutManager(requireContext(), 5)
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
            if (videos.isEmpty()) {
                binding.tvDataNotFound.visible()
                binding.recyclerViewVideos.gone()
            } else {
                binding.tvDataNotFound.gone()
                binding.recyclerViewVideos.visible()
            }

            videos.forEach { media ->
                media.isFav = favouriteList.find { it.mediaId == media.videoId }?.isFav == true
            }

            videoAdapter.submitList(videos)
        }
    }
}