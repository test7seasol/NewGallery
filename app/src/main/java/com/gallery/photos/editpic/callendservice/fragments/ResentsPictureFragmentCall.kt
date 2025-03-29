package com.gallery.photos.editphotovideo.callendservice.fragments

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.gallery.photos.editpic.Activity.ViewPagerActivity
import com.gallery.photos.editpic.Extensions.beGone
import com.gallery.photos.editpic.Model.MediaListItem
import com.gallery.photos.editpic.Model.MediaModel
import com.gallery.photos.editpic.Repository.RecentPictureRepository
import com.gallery.photos.editpic.Utils.MediaStoreSingleton
import com.gallery.photos.editpic.ViewModel.RecentPictureViewModel
import com.gallery.photos.editpic.ViewModel.RecentPictureViewModelFactory
import com.gallery.photos.editpic.databinding.FragmentRecentCallPictureBinding
import kotlinx.coroutines.launch

class ResentsPictureFragmentCall : Fragment() {

    private lateinit var binding: FragmentRecentCallPictureBinding
    private val viewModel: RecentPictureViewModel by viewModels {
        RecentPictureViewModelFactory(RecentPictureRepository(requireContext()))
    }

    private lateinit var mediaAdapter: RecentPictureAdaptercall
    private lateinit var gridLayoutManager: GridLayoutManager
    private val mediaList = mutableListOf<MediaModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentRecentCallPictureBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        setupRecyclerView()
        observeViewModel()

        if (!hasMediaPermissions()) {
            requestMediaPermissions()
        } else {
            viewModel.loadRecentMediacall()
        }
    }

    private fun requestMediaPermissions() {
        val permissions = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> arrayOf(
                Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
            )

            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> arrayOf(
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_VIDEO
            )

            else -> arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        }

        ActivityCompat.requestPermissions(
            requireActivity(),
            permissions,
            MEDIA_PERMISSION_REQUEST_CODE
        )
    }

    private val MEDIA_PERMISSION_REQUEST_CODE = 1001

    private fun showSettingsDialog() {
        AlertDialog.Builder(requireActivity())
            .setTitle("Permission Required")
            .setMessage("This app needs media access permissions to function properly. Please grant the required permissions in Settings.")
            .setPositiveButton("Go to Settings") { _, _ -> openAppSettings() }
            .setNegativeButton("Cancel") { _, _ -> requireActivity().finishAffinity() }
            .setCancelable(false)
            .show()
    }

    private fun openAppSettings() {
        val intent = Intent(ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", requireActivity().packageName, null)
        }
        startActivity(intent)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == MEDIA_PERMISSION_REQUEST_CODE) {
            if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                Log.d("Permissions", "Media permissions granted!")
                viewModel.loadRecentMedia()
            } else {
                Log.e("Permissions", "Media permissions denied!")
                showSettingsDialog()
            }
        }
    }

    private fun hasMediaPermissions(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
            ) == PackageManager.PERMISSION_GRANTED
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.READ_MEDIA_IMAGES
            ) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(
                        requireActivity(),
                        Manifest.permission.READ_MEDIA_VIDEO
                    ) == PackageManager.PERMISSION_GRANTED
        } else {
            ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        }
    }


    private fun setupRecyclerView() {
        mediaAdapter = RecentPictureAdaptercall(requireActivity()) { selectedMedia, position ->
            openViewPagerActivity(selectedMedia, position)
        }

        gridLayoutManager = GridLayoutManager(requireContext(), 5).apply {
            spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return if (mediaAdapter.getItemViewType(position) == 0) 5 else 1
                }
            }
        }

        binding.recyclerViewRecentPictures.apply {
            layoutManager = gridLayoutManager
            adapter = mediaAdapter
        }
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            viewModel.loadRecentMedia()
        }
    }

    private fun openViewPagerActivity(selectedMedia: List<MediaModel>, position: Int) {
        MediaStoreSingleton.imageList = ArrayList(selectedMedia)
        MediaStoreSingleton.selectedPosition = position
        startActivity(Intent(requireContext(), ViewPagerActivity::class.java))
    }

    private fun observeViewModel() {
        viewModel.mediaLiveData.observe(viewLifecycleOwner) { mediaItems ->
            mediaList.clear()
            mediaList.addAll(mediaItems.filterIsInstance<MediaListItem.Media>().map { it.media })
            mediaAdapter.submitList(mediaItems)
            binding.shimmerLayout.shimmerLayout.beGone()
        }
    }
}
