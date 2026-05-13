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
    private val repository: PhotoRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(PhotoListUiState())
    val uiState: StateFlow<PhotoListUiState> = _uiState.asStateFlow()

    private val _effect = Channel<PhotoListEffect>()
    val effect = _effect.receiveAsFlow()

    init {
        observePhotos()
        fetchCurrentPhotos()
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
                    _uiState.update {
                        it.copy(
                            loadedState = Loadable.Success(PhotoListUiState.LoadedState(photos))
                        )
                    }
                }
        }
    }

    fun action(action: PhotoListAction) {
        when (action) {
            is PhotoListAction.LoadPhotos -> fetchCurrentPhotos()
            is PhotoListAction.LoadNextPage -> fetchNextPage()
            is PhotoListAction.PullRefresh -> refreshPhotos()
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

    // error cases:
    // 1. pull refresh - show error dialog
    // 2. initial load / retry, failed with no data in db - show error dialog
    // 3. initial load / retry failed but db had some from previous sessions - Toast
    // 4. next page load failed - Toast

    private fun refreshPhotos() {
        if (uiState.value.showFavoritesOnly) return
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    loadedState = Loadable.Loading,
                    isNextPageLoading = false,
                )
            }
            repository.clearPhotos()
            val result = repository.loadMorePhotos()

            if (result is Loadable.Error) {
                _uiState.update { it.copy(loadedState = result) }
            }
        }
    }

    private fun fetchCurrentPhotos() {
        if (uiState.value.showFavoritesOnly) return

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    loadedState = Loadable.Loading,
                    isNextPageLoading = false,
                )
            }

            val result = repository.loadMorePhotos()

            if (result is Loadable.Error) {
                val noCachedPhotos =
                    (_uiState.value.loadedState as? Loadable.Success)?.data?.photos.isNullOrEmpty()

                if (!noCachedPhotos) {
                    _effect.send(
                        PhotoListEffect.ShowErrorToast(
                            result.throwable.message.orEmpty(),
                        ),
                    )
                } else {
                    _uiState.update { it.copy(loadedState = result) }
                }
            }
        }
    }

    private fun fetchNextPage() {
        if (uiState.value.showFavoritesOnly || uiState.value.isNextPageLoading) return

        viewModelScope.launch {
            _uiState.update { it.copy(isNextPageLoading = true) }
            val result = repository.loadMorePhotos()
            _uiState.update { it.copy(isNextPageLoading = false) }
            if (result is Loadable.Error) {
                // partial update show toast
                _effect.send(
                    PhotoListEffect.ShowErrorToast(
                        result.throwable.message.orEmpty(),
                    ),
                )
            }
        }
    }
}
