package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.home.domain

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import java.util.Date
import kotlin.collections.get

internal fun DocumentSnapshot.toFeaturedPostOrNull(): FeaturedPost? {
    val id = id.trim().takeIf { it.isNotEmpty() } ?: return null
    val category = FeaturedPostCategory.fromRaw(stringValueOrNull("category")) ?: return null

    val createdAt = dateValue("createdAt") ?: dateValue("created_at") ?: Date(0)
    val updatedAt = dateValue("updatedAt") ?: dateValue("updated_at") ?: createdAt
    val expiresAt = dateValue("expiresAt") ?: dateValue("expires_at") ?: return null
    val isVisible = boolValue("isVisible", default = boolValue("visible", default = true))

    val media = listMapValue("media")
        .mapIndexedNotNull { index, raw -> raw.toFeaturedPostMedia(documentId = id, index = index) }
        .sortedBy { it.position }

    return FeaturedPost(
        id = id,
        category = category,
        description = stringValueOrNull("description")?.takeIf { it.isNotBlank() },
        media = media,
        createdAt = createdAt,
        updatedAt = updatedAt,
        expiresAt = expiresAt,
        isVisible = isVisible,
    )
}

private fun Map<*, *>.toFeaturedPostMedia(
    documentId: String,
    index: Int,
): FeaturedPostMedia? {
    val downloadURL = stringValueOrNull("downloadURL")
        ?: stringValueOrNull("downloadUrl")
        ?: stringValueOrNull("download_url")
        ?: stringValueOrNull("url")

    val storagePath = stringValueOrNull("storagePath")
        ?: stringValueOrNull("storage_path")
        ?: ""

    if (downloadURL.isNullOrBlank() && storagePath.isBlank()) return null

    val id = stringValueOrNull("id")?.takeIf { it.isNotBlank() } ?: "$documentId-media-$index"

    return FeaturedPostMedia(
        id = id,
        downloadURL = downloadURL?.takeIf { it.isNotBlank() },
        storagePath = storagePath,
        width = doubleValueOrNull("width") ?: 1.0,
        height = doubleValueOrNull("height") ?: 1.0,
        position = intValueOrNull("position") ?: index,
    )
}

private fun DocumentSnapshot.stringValueOrNull(field: String): String? =
    getString(field)?.trim()

private fun DocumentSnapshot.boolValue(field: String, default: Boolean): Boolean =
    getBoolean(field) ?: default

private fun DocumentSnapshot.dateValue(field: String): Date? = when (val value = get(field)) {
    is Timestamp -> value.toDate()
    is Date -> value
    else -> null
}

private fun DocumentSnapshot.listMapValue(field: String): List<Map<*, *>> =
    (get(field) as? List<*>).orEmpty().mapNotNull { it as? Map<*, *> }

private fun Map<*, *>.stringValueOrNull(field: String): String? =
    (this[field] as? String)?.trim()

private fun Map<*, *>.intValueOrNull(field: String): Int? = when (val value = this[field]) {
    is Int -> value
    is Long -> value.toInt()
    is Double -> value.toInt()
    is Number -> value.toInt()
    else -> null
}

private fun Map<*, *>.doubleValueOrNull(field: String): Double? = when (val value = this[field]) {
    is Double -> value
    is Long -> value.toDouble()
    is Int -> value.toDouble()
    is Number -> value.toDouble()
    else -> null
}
