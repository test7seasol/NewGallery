package com.gallery.photos.editpic.ViewModel

import android.content.Context
import androidx.lifecycle.*
import com.gallery.photos.editpic.Model.MediaListItem
import com.gallery.photos.editpic.Model.VideoModel
import com.gallery.photos.editpic.Repository.VideoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class VideoViewModel(context: Context) : ViewModel() {

    private val repository = VideoRepository(context)
    private val _videosLiveData = MutableLiveData<List<VideoModel>>()
    val videosLiveData: LiveData<List<VideoModel>> get() = _videosLiveData

    fun loadAllVideos() {
        viewModelScope.launch {
            val videos = repository.getAllVideos()
            _videosLiveData.postValue(videos)
        }
    }

    fun loadRecentVideoscall() {
        viewModelScope.launch {
            val videos = repository.getAllVideoscall()
            _videosLiveData.postValue(videos)
        }
    }
}


