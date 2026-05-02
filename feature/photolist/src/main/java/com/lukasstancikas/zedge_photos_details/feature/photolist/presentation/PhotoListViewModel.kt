package com.lukasstancikas.zedge_photos_details.feature.photolist.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lukasstancikas.zedge_photos_details.core.common.model.Loadable
import com.lukasstancikas.zedge_photos_details.core.domain.repository.PhotoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class PhotoListViewModel @Inject constructor(
    private val repository: PhotoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PhotoListUiState())
    val uiState: StateFlow<PhotoListUiState> = _uiState.asStateFlow()

    private val _effect = Channel<PhotoListEffect>()
    val effect = _effect.receiveAsFlow()

    init {
        observePhotos()
        action(PhotoListAction.LoadPhotos)
    }

    private fun observePhotos() {
        viewModelScope.launch {
            _uiState
                .map { it.showFavoritesOnly }
                .distinctUntilChanged()
                .flatMapLatest { favoritesOnly ->
                    if (favoritesOnly) {
                        repository.getFavoritePhotosFlow()
                    } else {
                        repository.getPhotosFlow()
                    }
                }
                .collect { photos ->
                    _uiState.update { it.copy(photos = Loadable.Success(photos)) }
                }
        }
    }

    fun action(action: PhotoListAction) {
        when (action) {
            is PhotoListAction.LoadPhotos -> fetchPhotos()
            is PhotoListAction.Refresh -> onRefresh()
            is PhotoListAction.ClearPhotos -> clearPhotos()
            is PhotoListAction.ToggleFavoritesFilter -> {
                _uiState.update { it.copy(showFavoritesOnly = !it.showFavoritesOnly) }
            }

            is PhotoListAction.PhotoClicked -> {
                viewModelScope.launch {
                    _effect.send(PhotoListEffect.NavigateToDetails(action.photoId))
                }
            }
        }
    }

    private fun fetchPhotos() {
        viewModelScope.launch {
            _uiState.update { it.copy(photos = Loadable.Loading) }
            val result = repository.getPhotos(page = 1, limit = 20)
            _uiState.update { it.copy(photos = result) }
        }
    }

    private fun onRefresh() {
        fetchPhotos()
    }

    private fun clearPhotos() {
        viewModelScope.launch {
            repository.clearPhotos()
        }
    }
}
