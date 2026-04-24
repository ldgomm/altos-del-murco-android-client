package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.home.domain

import com.google.firebase.firestore.DocumentSnapshot
import java.util.Date

private fun String.normalizedFeaturedPostKey(): String =
    filter(Char::isLetterOrDigit).lowercase()

enum class FeaturedPostCategory(
    val rawValue: String,
    val title: String,
) {
    RESTAURANT("restaurant", "Restaurante"),
    ADVENTURE("adventure", "Aventura"),
    CLIENTS("clients", "Clientes");

    companion object {
        fun fromRaw(rawValue: String?): FeaturedPostCategory? {
            val key = rawValue?.normalizedFeaturedPostKey().orEmpty()
            return entries.firstOrNull {
                it.rawValue.normalizedFeaturedPostKey() == key || it.name.normalizedFeaturedPostKey() == key
            }
        }
    }
}

data class FeaturedPostMedia(
    val id: String,
    val downloadURL: String?,
    val storagePath: String,
    val width: Double,
    val height: Double,
    val position: Int,
) {
    val aspectRatio: Double
        get() = if (height > 0.0) width / height else 1.0
}

data class FeaturedPost(
    val id: String,
    val category: FeaturedPostCategory,
    val description: String?,
    val media: List<FeaturedPostMedia>,
    val createdAt: Date,
    val updatedAt: Date,
    val expiresAt: Date,
    val isVisible: Boolean,
) {
    val isExpired: Boolean
        get() = expiresAt.time <= Date().time

    val orderedMedia: List<FeaturedPostMedia>
        get() = media.sortedBy { it.position }
}

data class FeaturedFeedPage(
    val posts: List<FeaturedPost>,
    val lastSnapshot: DocumentSnapshot?,
    val hasMore: Boolean,
)