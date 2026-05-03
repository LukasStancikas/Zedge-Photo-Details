package com.lukasstancikas.zedge_photos_details.feature.photodetails.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.lukasstancikas.zedge_photos_details.core.common.model.Loadable
import com.lukasstancikas.zedge_photos_details.core.common.navigation.PhotoDetailsDestination
import com.lukasstancikas.zedge_photos_details.core.domain.repository.PhotoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PhotoDetailsViewModel @Inject constructor(
    private val repository: PhotoRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val destination: PhotoDetailsDestination =
        savedStateHandle.toRoute<PhotoDetailsDestination>()

    private val _uiState = MutableStateFlow(PhotoDetailsUiState(photoId = destination.photoId))
    val uiState: StateFlow<PhotoDetailsUiState> = _uiState.asStateFlow()

    private val _effect = Channel<PhotoDetailsEffect>()
    val effect = _effect.receiveAsFlow()

    init {
        loadPhoto()
    }

    fun action(action: PhotoDetailsAction) {
        when (action) {
            PhotoDetailsAction.LoadPhoto -> loadPhoto()
            PhotoDetailsAction.ToggleFavorite -> toggleFavorite()
            PhotoDetailsAction.SharePhoto -> sharePhoto()
        }
    }

    private fun loadPhoto() {
        viewModelScope.launch {
            _uiState.update { it.copy(photo = Loadable.Loading) }
            val result = repository.getPhoto(uiState.value.photoId)
            _uiState.update { state ->
                state.copy(
                    photo = result,
                )
            }
        }
    }

    private fun toggleFavorite() {
        val currentPhoto = (uiState.value.photo as? Loadable.Success)?.data ?: return
        viewModelScope.launch {
            val newFavoriteStatus = !currentPhoto.isFavorite
            repository.toggleFavorite(uiState.value.photoId, newFavoriteStatus)
            _uiState.update { state ->
                state.copy(
                    photo = state.photo.map { it.copy(isFavorite = newFavoriteStatus) }
                )
            }
        }
    }

    private fun sharePhoto() {
        val currentPhoto = (uiState.value.photo as? Loadable.Success)?.data ?: return
        viewModelScope.launch {
            _effect.send(PhotoDetailsEffect.SharePhotoUrl(currentPhoto.downloadUrl))
        }
    }
}

