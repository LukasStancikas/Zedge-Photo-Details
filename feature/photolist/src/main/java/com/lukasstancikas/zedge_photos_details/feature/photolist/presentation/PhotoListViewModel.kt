package com.lukasstancikas.zedge_photos_details.feature.photolist.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lukasstancikas.zedge_photos_details.core.domain.repository.PhotoRepository
import com.lukasstancikas.zedge_photos_details.core.common.model.Loadable
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

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
            val photosResult = repository.getPhotos(page = 1, limit = 20)
            _uiState.update { it.copy(photos = photosResult) }
        }
    }

    fun onRefresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }
            repository.clearPhotos()
            val result = repository.getPhotos(page = 1, limit = 20)
            _uiState.update {
                it.copy(
                    isRefreshing = false,
                    photos = result
                )
            }
        }
    }
}
