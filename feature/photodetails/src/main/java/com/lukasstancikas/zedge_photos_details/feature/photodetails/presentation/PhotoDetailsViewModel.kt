package com.lukasstancikas.zedge_photos_details.feature.photodetails.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lukasstancikas.zedge_photos_details.core.common.sharing.ContentSharingController
import com.lukasstancikas.zedge_photos_details.core.common.model.Loadable
import com.lukasstancikas.zedge_photos_details.core.domain.repository.PhotoRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = PhotoDetailsViewModel.Factory::class)
class PhotoDetailsViewModel @AssistedInject constructor(
    @Assisted photoId: String,
    private val repository: PhotoRepository,
    private val contentSharingController: ContentSharingController,
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(photoId: String): PhotoDetailsViewModel
    }

    private val _uiState = MutableStateFlow(PhotoDetailsUiState(photoId = photoId))
    val uiState: StateFlow<PhotoDetailsUiState> = _uiState.asStateFlow()

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
                    photo = state.photo.map { it.copy(isFavorite = newFavoriteStatus) },
                )
            }
        }
    }

    private fun sharePhoto() {
        val currentPhoto = (uiState.value.photo as? Loadable.Success)?.data ?: return
        contentSharingController.sharePhotoUrl(currentPhoto.downloadUrl)
    }
}
