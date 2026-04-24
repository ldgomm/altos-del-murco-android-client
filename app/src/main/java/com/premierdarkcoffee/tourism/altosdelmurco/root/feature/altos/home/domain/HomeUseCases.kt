package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.home.domain

import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveLatestFeaturedPostsUseCase @Inject constructor(
    private val repository: FeaturedFeedRepositoriable,
) {
    fun execute(limit: Int): Flow<FeaturedFeedPage> = repository.observeLatest(limit)
}

class FetchFeaturedPostsPageUseCase @Inject constructor(
    private val repository: FeaturedFeedRepositoriable,
) {
    suspend fun execute(limit: Int, after: DocumentSnapshot?): FeaturedFeedPage =
        repository.fetchNextPage(limit = limit, after = after)
}
