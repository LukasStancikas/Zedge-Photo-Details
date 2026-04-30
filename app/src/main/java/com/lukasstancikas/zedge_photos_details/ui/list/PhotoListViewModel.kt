package com.lukasstancikas.zedge_photos_details.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lukasstancikas.zedge_photos_details.data.model.Photo
import com.lukasstancikas.zedge_photos_details.data.repository.PhotoRepository
import com.lukasstancikas.zedge_photos_details.ui.Loadable
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PhotoListUiState(
    val photos: Loadable<List<Photo>> = Loadable.Loading
)

@HiltViewModel
class PhotoListViewModel @Inject constructor(
    private val repository: PhotoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PhotoListUiState())
    val uiState: StateFlow<PhotoListUiState> = _uiState.asStateFlow()

    init {
        fetchPhotos()
    }

    fun fetchPhotos() {
        viewModelScope.launch {
            _uiState.update { it.copy(photos = Loadable.Loading) }
            delay(3000L) // for testing
            val photosResult = repository.getPhotos(page = 1, limit = 20)
            _uiState.update { it.copy(photos = photosResult) }
        }
    }
}
