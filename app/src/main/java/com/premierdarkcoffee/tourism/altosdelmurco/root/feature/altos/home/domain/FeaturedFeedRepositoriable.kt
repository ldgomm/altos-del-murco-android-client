package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.home.domain

import com.google.firebase.firestore.DocumentSnapshot

interface FeaturedFeedRepositoriable {
    fun observeLatest(limit: Int): kotlinx.coroutines.flow.Flow<FeaturedFeedPage>
    suspend fun fetchNextPage(limit: Int, after: DocumentSnapshot?): FeaturedFeedPage
}
