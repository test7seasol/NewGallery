package com.gallery.photos.editpic.ViewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.gallery.photos.editpic.Model.FolderModel
import com.gallery.photos.editpic.Repository.AlbumRepository

import kotlinx.coroutines.launch

class AlbumViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AlbumRepository(application)
    val folderLiveData: LiveData<List<FolderModel>> = repository.folderLiveData

    fun refreshFolders() {
        viewModelScope.launch {
            repository.loadMediaFolders()
        }
    }
}
