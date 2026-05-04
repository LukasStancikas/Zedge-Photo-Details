package com.lukasstancikas.zedge_photos_details.feature.photolist.presentation

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.turbineScope
import com.lukasstancikas.zedge_photos_details.core.common.model.Loadable
import com.lukasstancikas.zedge_photos_details.core.domain.model.Photo
import com.lukasstancikas.zedge_photos_details.core.domain.repository.PhotoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class PhotoListViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    private val repository: PhotoRepository = mock()
    private lateinit var viewModel: PhotoListViewModel

    private val photosFlow = MutableStateFlow<List<Photo>>(emptyList())
    private val favoritePhotosFlow = MutableStateFlow<List<Photo>>(emptyList())

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        whenever(repository.getPhotosFlow()).thenReturn(photosFlow)
        whenever(repository.getFavoritePhotosFlow()).thenReturn(favoritePhotosFlow)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial load success updates state with photos`() = runTest {
        val photos = listOf(createPhoto("1"))
        whenever(repository.loadMorePhotos()).thenReturn(Loadable.Success(Unit))
        photosFlow.value = photos

        turbineScope {
            viewModel = PhotoListViewModel(repository)
            val stateTurbine = viewModel.uiState.testIn(this)

            // Initial State: Loading
            val initialState = stateTurbine.awaitItem()
            assertEquals(Loadable.Loading, initialState.photos)

            // After repository finishes loading and flow emits: Success
            val successState = stateTurbine.awaitItem()
            assertEquals(photos, (successState.photos as Loadable.Success<List<Photo>>).data)

            stateTurbine.cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `load photos failure updates state with error`() = runTest {
        val errorMessage = "Network error"
        whenever(repository.loadMorePhotos()).thenReturn(Loadable.Error(Exception(errorMessage)))

        turbineScope {
            viewModel = PhotoListViewModel(repository)
            val stateTurbine = viewModel.uiState.testIn(this)

            // Initial State: Loading
            val initialState = stateTurbine.awaitItem()
            assertEquals(Loadable.Loading, initialState.photos)

            // After repository failure: Error
            val errorState = stateTurbine.awaitItem()
            assertTrue(errorState.photos is Loadable.Error)
            assertEquals(errorMessage, (errorState.photos as Loadable.Error).throwable.message)

            stateTurbine.cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `refresh resets page and reloads photos`() = runTest {
        val initialPhotos = listOf(createPhoto("1"))
        val refreshedPhotos = listOf(createPhoto("1"), createPhoto("2"))

        whenever(repository.loadMorePhotos()).thenReturn(Loadable.Success(Unit))

        turbineScope {
            viewModel = PhotoListViewModel(repository)
            val stateTurbine = viewModel.uiState.testIn(this)

            // Initial load
            val initialLoadingState = stateTurbine.awaitItem()
            assertEquals(Loadable.Loading, initialLoadingState.photos)

            photosFlow.value = initialPhotos
            val initialSuccessState = stateTurbine.awaitItem()
            assertEquals(
                initialPhotos,
                (initialSuccessState.photos as Loadable.Success<List<Photo>>).data,
            )

            // Refresh
            viewModel.action(PhotoListAction.PullRefresh)

            // Switches to Loading during refresh
            val refreshingState = stateTurbine.awaitItem()
            assertEquals(Loadable.Loading, refreshingState.photos)

            // Simulate repository triggering a new batch of items arriving to repository.getPhotosFlow()
            // after repository.loadMorePhotos()
            photosFlow.value = refreshedPhotos

            // Returns to Success
            val refreshedState = stateTurbine.awaitItem()
            assertEquals(
                refreshedPhotos,
                (refreshedState.photos as Loadable.Success<List<Photo>>).data,
            )

            verify(repository, times(2)).loadMorePhotos()
            stateTurbine.cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `load next page success`() = runTest {
        turbineScope {
            val photos = listOf(createPhoto("1"))
            whenever(repository.loadMorePhotos()).thenReturn(Loadable.Success(Unit))
            photosFlow.value = photos

            viewModel = PhotoListViewModel(repository)
            val stateTurbine = viewModel.uiState.testIn(this)
            // Initial load
            val initialLoadingState = stateTurbine.awaitItem()
            assertEquals(Loadable.Loading, initialLoadingState.photos)

            val initialSuccessState = stateTurbine.awaitItem()
            assertTrue(initialSuccessState.photos is Loadable.Success)

            viewModel.action(PhotoListAction.LoadNextPage)
            // Page incremented and loading finished
            val nextPageLoadingState = stateTurbine.awaitItem()
            assertTrue(nextPageLoadingState.isNextPageLoading)
            val nextPageLoadedState = stateTurbine.awaitItem()
            assertFalse(nextPageLoadedState.isNextPageLoading)

            verify(repository, times(2)).loadMorePhotos()
            stateTurbine.cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `load next page failure triggers ShowErrorToast effect`() = runTest {
        whenever(repository.loadMorePhotos()).thenReturn(Loadable.Success(Unit))
            .thenReturn(Loadable.Error(Exception("Paging error")))

        turbineScope {
            viewModel = PhotoListViewModel(repository)
            val stateTurbine = viewModel.uiState.testIn(this)
            val effectTurbine = viewModel.effect.testIn(this)
            // Initial load
            val initialLoadingState = stateTurbine.awaitItem()
            assertEquals(Loadable.Loading, initialLoadingState.photos)

            val initialSuccessState = stateTurbine.awaitItem()
            assertTrue(initialSuccessState.photos is Loadable.Success)

            viewModel.action(PhotoListAction.LoadNextPage)
            // Effect emitted on failure
            val effect = effectTurbine.awaitItem()
            assertTrue(effect is PhotoListEffect.ShowErrorToast)
            assertEquals("Paging error", (effect as PhotoListEffect.ShowErrorToast).error)

            // Paging started (back to false)
            val nextPageLoadingState = stateTurbine.awaitItem()
            assertTrue(nextPageLoadingState.isNextPageLoading)
            val nextPageLoadedState = stateTurbine.awaitItem()
            assertFalse(nextPageLoadedState.isNextPageLoading)

            stateTurbine.cancelAndIgnoreRemainingEvents()
            effectTurbine.cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `toggle favorites filter switches flows`() = runTest {
        whenever(repository.loadMorePhotos()).thenReturn(Loadable.Success(Unit))

        val regularPhotos = listOf(createPhoto("1"))
        val favoritePhotos = listOf(createPhoto("2", isFavorite = true))

        photosFlow.value = regularPhotos
        favoritePhotosFlow.value = favoritePhotos

        turbineScope {
            viewModel = PhotoListViewModel(repository)
            val stateTurbine = viewModel.uiState.testIn(this)

            // Initial Loading
            val initialLoadingState = stateTurbine.awaitItem()
            assertEquals(Loadable.Loading, initialLoadingState.photos)

            // Regular photos loaded from flow
            val regularPhotosState = stateTurbine.awaitItem()
            assertEquals(
                regularPhotos,
                (regularPhotosState.photos as Loadable.Success<List<Photo>>).data,
            )

            viewModel.action(PhotoListAction.ToggleFavoritesFilter)

            // Filter toggled (showFavoritesOnly = true)
            val filterToggledState = stateTurbine.awaitItem()
            assertTrue(filterToggledState.showFavoritesOnly)

            // Data switched to favorite flow
            val favoritePhotosState = stateTurbine.awaitItem()
            assertEquals(
                favoritePhotos,
                (favoritePhotosState.photos as Loadable.Success<List<Photo>>).data,
            )

            stateTurbine.cancelAndIgnoreRemainingEvents()
        }
    }

    private fun createPhoto(id: String, isFavorite: Boolean = false) = Photo(
        id = id,
        author = "Author $id",
        width = 100,
        height = 100,
        url = "url",
        downloadUrl = "downloadUrl",
        isFavorite = isFavorite,
    )
}
