package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.home.presentation.viewmodel

import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.home.domain.FeaturedPost

data class FeaturedFeedUiState(
    val posts: List<FeaturedPost> = emptyList(),
    val isLoadingInitial: Boolean = false,
    val isLoadingMore: Boolean = false,
    val hasMore: Boolean = true,
    val errorMessage: String? = null,
) {
    val shouldShowInitialPlaceholders: Boolean
        get() = isLoadingInitial && posts.isEmpty()

    val shouldShowEmptyState: Boolean
        get() = !isLoadingInitial && posts.isEmpty()
}