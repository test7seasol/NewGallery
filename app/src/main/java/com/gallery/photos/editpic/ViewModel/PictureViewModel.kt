package com.gallery.photos.editpic.ViewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.gallery.photos.editpic.Model.MediaModel
import com.gallery.photos.editpic.Repository.PictureRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

/*
class PictureViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = PictureRepository(application)
    val mediaLiveData: LiveData<List<MediaModel>> = repository.mediaLiveData

    fun loadMedia(bucketId: String) {
        viewModelScope.launch {
            repository.loadMediaByBucketId(bucketId)
        }
    }
}
*/


class PictureViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = PictureRepository(application)

    private val _mediaLiveData = MutableLiveData<List<MediaModel>>()
    val mediaLiveData: LiveData<List<MediaModel>> = _mediaLiveData

    fun loadMedia(bucketId: String) {
        viewModelScope.launch {
            repository.getMediaFiles(bucketId)
                .flowOn(Dispatchers.IO) // Fetch on background thread
                .collect { mediaList ->
                    _mediaLiveData.postValue(mediaList) // Update UI
                }
        }
    }
}
