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
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
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
        whenever(repository.loadPhotoPage(1)).thenReturn(Loadable.Success(Unit))
        photosFlow.value = photos

        turbineScope {
            viewModel = PhotoListViewModel(repository)
            val stateTurbine = viewModel.uiState.testIn(this)

            // Initial State: Loading
            val initialState = stateTurbine.awaitItem()
            assertEquals(Loadable.Loading, initialState.photos)

            // After repository finishes loading and flow emits: Success
            advanceUntilIdle()
            val successState = stateTurbine.awaitItem()
            assertEquals(photos, (successState.photos as Loadable.Success<List<Photo>>).data)
            assertEquals(1, successState.currentPage)

            stateTurbine.cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `load photos failure updates state with error`() = runTest {
        val errorMessage = "Network error"
        whenever(repository.loadPhotoPage(1)).thenReturn(Loadable.Error(Exception(errorMessage)))

        turbineScope {
            viewModel = PhotoListViewModel(repository)
            val stateTurbine = viewModel.uiState.testIn(this)

            // Initial State: Loading
            val initialState = stateTurbine.awaitItem()
            assertEquals(Loadable.Loading, initialState.photos)

            // After repository failure: Error
            advanceUntilIdle()
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

        whenever(repository.loadPhotoPage(1)).thenReturn(Loadable.Success(Unit))

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
                (initialSuccessState.photos as Loadable.Success<List<Photo>>).data
            )

            // Refresh
            viewModel.action(PhotoListAction.PullRefresh)
            runCurrent()

            // 1. Switches to Loading during refresh
            val refreshingState = stateTurbine.awaitItem()
            assertEquals(Loadable.Loading, refreshingState.photos)

            // 2. Simulate repository triggering a new batch of items arriving to repository.getPhotosFlow() 
            // after repository.loadPhotoPage(page = 1)
            advanceUntilIdle()
            photosFlow.value = refreshedPhotos

            // 3. Returns to Success
            val refreshedState = stateTurbine.awaitItem()
            assertEquals(
                refreshedPhotos,
                (refreshedState.photos as Loadable.Success<List<Photo>>).data
            )
            assertEquals(1, refreshedState.currentPage)

            verify(repository, org.mockito.kotlin.times(2)).loadPhotoPage(1)
            stateTurbine.cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `load next page success increments page`() = runTest {
        turbineScope {
        val photos = listOf(createPhoto("1"))
        whenever(repository.loadPhotoPage(1)).thenReturn(Loadable.Success(Unit))
        photosFlow.value = photos

            viewModel = PhotoListViewModel(repository)
            val stateTurbine = viewModel.uiState.testIn(this)
            // Initial load
            val initialLoadingState = stateTurbine.awaitItem()
            assertEquals(Loadable.Loading, initialLoadingState.photos)

            val initialSuccessState = stateTurbine.awaitItem()
            assertTrue(initialSuccessState.photos is Loadable.Success)

            viewModel.action(PhotoListAction.LoadNextPage)

            // 1. Shows next page is loading
            val nextPageLoadingState = stateTurbine.awaitItem()
            assertTrue(nextPageLoadingState.isNextPageLoading)

            // 2. Page incremented and loading finished
            val nextPageSuccessState = stateTurbine.awaitItem()
            assertFalse(nextPageSuccessState.isNextPageLoading)
            assertEquals(2, nextPageSuccessState.currentPage)

            verify(repository).loadPhotoPage(2)
            stateTurbine.cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `load next page failure triggers ShowErrorToast effect`() = runTest {
        whenever(repository.loadPhotoPage(1)).thenReturn(Loadable.Success(Unit))
        whenever(repository.loadPhotoPage(2)).thenReturn(Loadable.Error(Exception("Paging error")))

        turbineScope {
            viewModel = PhotoListViewModel(repository)
            val stateTurbine = viewModel.uiState.testIn(this)
            val effectTurbine = viewModel.effect.testIn(this)

            // Initial load
            val initialLoadingState = stateTurbine.awaitItem()
            assertEquals(Loadable.Loading, initialLoadingState.photos)

            advanceUntilIdle()
            val initialSuccessState = stateTurbine.awaitItem()
            assertTrue(initialSuccessState.photos is Loadable.Success)

            viewModel.action(PhotoListAction.LoadNextPage)

            // 1. Paging starts
            val pagingLoadingState = stateTurbine.awaitItem()
            assertTrue(pagingLoadingState.isNextPageLoading)

            // 2. Effect emitted on failure
            val effect = effectTurbine.awaitItem()
            assertTrue(effect is PhotoListEffect.ShowErrorToast)
            assertEquals("Paging error", (effect as PhotoListEffect.ShowErrorToast).error)

            // 3. Paging finished (back to false)
            val finalState = stateTurbine.awaitItem()
            assertFalse(finalState.isNextPageLoading)

            assertEquals(1, viewModel.uiState.value.currentPage)

            stateTurbine.cancelAndIgnoreRemainingEvents()
            effectTurbine.cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `toggle favorites filter switches flows`() = runTest {
        whenever(repository.loadPhotoPage(any())).thenReturn(Loadable.Success(Unit))

        val regularPhotos = listOf(createPhoto("1"))
        val favoritePhotos = listOf(createPhoto("2", isFavorite = true))

        photosFlow.value = regularPhotos
        favoritePhotosFlow.value = favoritePhotos

        turbineScope {
            viewModel = PhotoListViewModel(repository)
            val stateTurbine = viewModel.uiState.testIn(this)

            // 1. Initial Loading
            val initialLoadingState = stateTurbine.awaitItem()
            assertEquals(Loadable.Loading, initialLoadingState.photos)

            // 2. Regular photos loaded from flow
            advanceUntilIdle()
            val regularPhotosState = stateTurbine.awaitItem()
            assertEquals(
                regularPhotos,
                (regularPhotosState.photos as Loadable.Success<List<Photo>>).data
            )

            viewModel.action(PhotoListAction.ToggleFavoritesFilter)

            // 3. Filter toggled (showFavoritesOnly = true)
            val filterToggledState = stateTurbine.awaitItem()
            assertTrue(filterToggledState.showFavoritesOnly)

            // 4. Data switched to favorite flow
            advanceUntilIdle()
            val favoritePhotosState = stateTurbine.awaitItem()
            assertEquals(
                favoritePhotos,
                (favoritePhotosState.photos as Loadable.Success<List<Photo>>).data
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
        isFavorite = isFavorite
    )
}
