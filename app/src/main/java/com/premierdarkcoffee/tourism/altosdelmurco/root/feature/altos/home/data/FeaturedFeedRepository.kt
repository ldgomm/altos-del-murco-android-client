package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.home.data

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.home.domain.FeaturedFeedPage
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.home.domain.FeaturedFeedRepositoriable
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.home.domain.toFeaturedPostOrNull
import com.premierdarkcoffee.tourism.altosdelmurco.util.constant.FirestoreCollections
import com.premierdarkcoffee.tourism.altosdelmurco.util.database.awaitResult
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FeaturedFeedRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
) : FeaturedFeedRepositoriable {

    override fun observeLatest(limit: Int): Flow<FeaturedFeedPage> = callbackFlow {
        val registration = baseActiveQuery()
            .limit(limit.toLong().coerceAtLeast(1L))
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val documents = snapshot?.documents.orEmpty()
                val posts = documents.mapNotNull { it.toFeaturedPostOrNull() }

                trySend(
                    FeaturedFeedPage(
                        posts = posts,
                        lastSnapshot = documents.lastOrNull(),
                        hasMore = documents.size == limit,
                    ),
                ).isSuccess
            }

        awaitClose { registration.remove() }
    }

    override suspend fun fetchNextPage(
        limit: Int,
        after: DocumentSnapshot?,
    ): FeaturedFeedPage {
        var query: Query = baseActiveQuery().limit(limit.toLong().coerceAtLeast(1L))

        if (after != null) {
            query = query.startAfter(after)
        }

        val snapshot = query.get().awaitResult()
        val documents = snapshot.documents

        return FeaturedFeedPage(
            posts = documents.mapNotNull { it.toFeaturedPostOrNull() },
            lastSnapshot = documents.lastOrNull() ?: after,
            hasMore = documents.size == limit,
        )
    }

    private fun baseActiveQuery(): Query = firestore
        .collection(FirestoreCollections.FEATURED_POSTS)
        .whereEqualTo("isVisible", true)
        .whereGreaterThan("expiresAt", Timestamp(Date()))
        .orderBy("expiresAt", Query.Direction.DESCENDING)
}