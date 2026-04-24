package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.home.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.DocumentSnapshot
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.home.domain.FeaturedPost
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.home.domain.FetchFeaturedPostsPageUseCase
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.home.domain.ObserveLatestFeaturedPostsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeaturedFeedViewModel @Inject constructor(
    private val fetchNextPageUseCase: FetchFeaturedPostsPageUseCase,
    private val observeLatestUseCase: ObserveLatestFeaturedPostsUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(FeaturedFeedUiState())
    val uiState: StateFlow<FeaturedFeedUiState> = _uiState.asStateFlow()

    private var observeJob: Job? = null
    private var loadMoreJob: Job? = null
    private var lastSnapshot: DocumentSnapshot? = null

    fun start() {
        if (observeJob?.isActive == true) return

        _uiState.update {
            it.copy(
                isLoadingInitial = it.posts.isEmpty(),
                errorMessage = null,
            )
        }

        observeJob = viewModelScope.launch {
            observeLatestUseCase.execute(PAGE_SIZE)
                .catch { error ->
                    _uiState.update {
                        it.copy(
                            isLoadingInitial = false,
                            errorMessage = error.message ?: "No se pudo cargar destacados.",
                        )
                    }
                }
                .collectLatest { page ->
                    lastSnapshot = page.lastSnapshot
                    _uiState.update { current ->
                        val merged = mergeKeepingNewest(
                            current = current.posts,
                            incomingTopPage = page.posts,
                        )
                        current.copy(
                            posts = merged,
                            hasMore = page.hasMore || merged.size > page.posts.size,
                            isLoadingInitial = false,
                            errorMessage = null,
                        )
                    }
                }
        }
    }

    fun refresh() {
        observeJob?.cancel()
        loadMoreJob?.cancel()
        observeJob = null
        loadMoreJob = null
        lastSnapshot = null
        _uiState.value = FeaturedFeedUiState(isLoadingInitial = true)
        start()
    }

    fun loadMoreIfNeeded(currentPost: FeaturedPost?) {
        val post = currentPost ?: return
        val state = _uiState.value
        if (state.posts.lastOrNull()?.id != post.id) return
        if (state.isLoadingInitial || state.isLoadingMore || !state.hasMore) return
        if (loadMoreJob?.isActive == true) return

        loadMoreJob = viewModelScope.launch { loadMore() }
    }

    fun dismissError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    override fun onCleared() {
        observeJob?.cancel()
        loadMoreJob?.cancel()
        super.onCleared()
    }

    private suspend fun loadMore() {
        _uiState.update { it.copy(isLoadingMore = true, errorMessage = null) }

        runCatching {
            fetchNextPageUseCase.execute(PAGE_SIZE, lastSnapshot)
        }.onSuccess { page ->
            lastSnapshot = page.lastSnapshot
            _uiState.update { current ->
                current.copy(
                    posts = mergeAppendingOlder(
                        current = current.posts,
                        incomingOlderPage = page.posts,
                    ),
                    hasMore = page.hasMore,
                    isLoadingMore = false,
                    errorMessage = null,
                )
            }
        }.onFailure { error ->
            _uiState.update {
                it.copy(
                    isLoadingMore = false,
                    errorMessage = error.message ?: "No se pudieron cargar más destacados.",
                )
            }
        }
    }

    private fun mergeKeepingNewest(
        current: List<FeaturedPost>,
        incomingTopPage: List<FeaturedPost>,
    ): List<FeaturedPost> {
        val map = current.associateBy { it.id }.toMutableMap()
        incomingTopPage.forEach { post -> map[post.id] = post }
        return map.values
            .filter { !it.isExpired && it.isVisible }
            .sortedWith(featuredPostSort)
    }

    private fun mergeAppendingOlder(
        current: List<FeaturedPost>,
        incomingOlderPage: List<FeaturedPost>,
    ): List<FeaturedPost> {
        val seen = current.mapTo(mutableSetOf()) { it.id }
        val merged = current.toMutableList()

        incomingOlderPage.forEach { post ->
            if (!seen.contains(post.id) && !post.isExpired && post.isVisible) {
                merged += post
                seen += post.id
            }
        }

        return merged.sortedWith(featuredPostSort)
    }

    private companion object {
        const val PAGE_SIZE = 5

        val featuredPostSort = compareByDescending<FeaturedPost> { it.expiresAt.time }
            .thenByDescending { it.createdAt.time }
    }
}