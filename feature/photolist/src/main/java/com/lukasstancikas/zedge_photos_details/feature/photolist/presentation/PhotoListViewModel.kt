package com.lukasstancikas.zedge_photos_details.feature.photolist.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lukasstancikas.zedge_photos_details.core.common.model.Loadable
import com.lukasstancikas.zedge_photos_details.core.domain.repository.PhotoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
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
        .onStart {
            action(PhotoListAction.LoadPhotos)
            observePhotos()
        }
        .stateIn(
            viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = _uiState.value
        )

    private val _effect = Channel<PhotoListEffect>()
    val effect = _effect.receiveAsFlow()

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
            is PhotoListAction.LoadPhotos -> fetchPhotos(isRefresh = false)
            is PhotoListAction.LoadNextPage -> fetchNextPage()
            is PhotoListAction.PullRefresh -> fetchPhotos(isRefresh = true)
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

    private fun fetchPhotos(isRefresh: Boolean = false) {
        if (uiState.value.showFavoritesOnly) return

        viewModelScope.launch {
            val pageToLoad = if (isRefresh) {
                1
            } else {
                _uiState.value.currentPage
            }

            _uiState.update {
                it.copy(
                    photos = Loadable.Loading,
                    currentPage = pageToLoad,
                    isNextPageLoading = false
                )
            }

            val result = repository.loadPhotoPage(page = pageToLoad)
            _uiState.update { it.copy(isNextPageLoading = false) }
            if (result is Loadable.Error) {
                _uiState.update { it.copy(photos = result) }
            }
        }
    }

    private fun fetchNextPage() {
        if (uiState.value.showFavoritesOnly || uiState.value.isNextPageLoading) return

        viewModelScope.launch {
            _uiState.update { it.copy(isNextPageLoading = true) }
            val nextPage = _uiState.value.currentPage + 1
            val result = repository.loadPhotoPage(page = nextPage)
            _uiState.update { it.copy(isNextPageLoading = false) }
            if (result is Loadable.Error) {
                // report error as a dialog
                _uiState.update { it.copy(photos = result) }
            } else {
                _uiState.update { it.copy(currentPage = nextPage) }
            }
        }
    }


    private fun clearPhotos() {
        viewModelScope.launch {
            repository.clearPhotos()
            _uiState.update { it.copy(currentPage = 1) }
        }
    }
}
