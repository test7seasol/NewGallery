package com.gallery.photos.editpic.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.gallery.photos.editpic.Repository.RecentPictureRepository

// ViewModel Factory to handle dependencies
class RecentPictureViewModelFactory(private val repository: RecentPictureRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RecentPictureViewModel::class.java)) {
            return RecentPictureViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}