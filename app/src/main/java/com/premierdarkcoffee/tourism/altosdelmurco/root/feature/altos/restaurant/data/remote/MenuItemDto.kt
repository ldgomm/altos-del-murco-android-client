package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.data.remote

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain.MenuItem


data class MenuItemDto(
    val id: String = "",
    val categoryId: String = "",
    val categoryTitle: String = "",
    val name: String = "",
    val description: String = "",
    val notes: String? = null,
    val ingredients: List<String> = emptyList(),
    val price: Double = 0.0,
    val offerPrice: Double? = null,
    val imageURL: String? = null,
    val isAvailable: Boolean = true,
    val remainingQuantity: Int = 0,
    val isFeatured: Boolean = false,
    val sortOrder: Int = 0,
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null,
) {
    fun toDomain(documentId: String? = null): MenuItem {
        val resolvedId = id.trim().ifBlank { documentId.orEmpty().trim() }
        val resolvedCategoryTitle =
            categoryTitle.trim().ifBlank { categoryId.toReadableCategoryTitle() }
        val resolvedCategoryId =
            categoryId.trim().ifBlank { resolvedCategoryTitle.toCategorySlug() }

        return MenuItem(
            id = resolvedId,
            categoryId = resolvedCategoryId,
            categoryTitle = resolvedCategoryTitle,
            name = name.trim(),
            description = description.trim(),
            notes = notes?.trim()?.takeIf { it.isNotEmpty() },
            ingredients = ingredients.map { it.trim() }.filter { it.isNotEmpty() },
            price = price.coerceAtLeast(0.0),
            offerPrice = offerPrice?.takeIf { it in 0.0..price },
            imageURL = imageURL?.trim()?.takeIf { it.isNotEmpty() },
            isAvailable = isAvailable,
            remainingQuantity = remainingQuantity.coerceAtLeast(0),
            isFeatured = isFeatured,
            sortOrder = sortOrder,
        )
    }

    companion object {
        fun fromDocument(document: DocumentSnapshot): MenuItemDto? {
            val id = document.stringValue("id").ifBlank { document.id }
            val name = document.stringValue("name")
            if (id.isBlank() || name.isBlank()) return null

            val price = document.doubleValue("price")
            val offerPrice = document.doubleValueOrNull("offerPrice") ?: document.doubleValueOrNull(
                "offer_price"
            )

            return MenuItemDto(
                id = id,
                categoryId = document.stringValue("categoryId").ifBlank {
                    document.stringValue("category_id")
                },
                categoryTitle = document.stringValue("categoryTitle").ifBlank {
                    document.stringValue("category_title")
                },
                name = name,
                description = document.stringValue("description"),
                notes = document.stringValueOrNull("notes"),
                ingredients = document.stringList("ingredients"),
                price = price,
                offerPrice = offerPrice,
                imageURL = document.stringValueOrNull("imageURL")
                    ?: document.stringValueOrNull("imageUrl")
                    ?: document.stringValueOrNull("image_url"),
                isAvailable = document.boolValue(
                    "isAvailable", default = document.boolValue("available", default = true)
                ),
                remainingQuantity = document.intValue(
                    "remainingQuantity",
                    default = document.intValue("remaining_quantity", default = 0)
                ),
                isFeatured = document.boolValue(
                    "isFeatured", default = document.boolValue("featured", default = false)
                ),
                sortOrder = document.intValue(
                    "sortOrder", default = document.intValue("sort_order", default = 0)
                ),
                createdAt = document.get("createdAt") as? Timestamp,
                updatedAt = document.get("updatedAt") as? Timestamp,
            )
        }
    }
}

private fun DocumentSnapshot.stringValue(field: String): String = stringValueOrNull(field).orEmpty()

private fun DocumentSnapshot.stringValueOrNull(field: String): String? = getString(field)?.trim()

private fun DocumentSnapshot.boolValue(field: String, default: Boolean): Boolean =
    getBoolean(field) ?: default

private fun DocumentSnapshot.intValue(field: String, default: Int): Int =
    when (val value = get(field)) {
        is Int -> value
        is Long -> value.toInt()
        is Double -> value.toInt()
        is Number -> value.toInt()
        else -> default
    }

private fun DocumentSnapshot.doubleValue(field: String): Double = doubleValueOrNull(field) ?: 0.0

private fun DocumentSnapshot.doubleValueOrNull(field: String): Double? =
    when (val value = get(field)) {
        is Double -> value
        is Long -> value.toDouble()
        is Int -> value.toDouble()
        is Number -> value.toDouble()
        else -> null
    }

private fun DocumentSnapshot.stringList(field: String): List<String> {
    val value = get(field) as? List<*> ?: return emptyList()
    return value.mapNotNull { it as? String }.map { it.trim() }.filter { it.isNotEmpty() }
}

private fun String.toCategorySlug(): String =
    trim().lowercase().replace("á", "a").replace("é", "e").replace("í", "i").replace("ó", "o")
        .replace("ú", "u").replace("ñ", "n").replace(Regex("[^a-z0-9]+"), "-").trim('-')
        .ifBlank { "otros" }

private fun String.toReadableCategoryTitle(): String =
    trim().replace("-", " ").replace("_", " ").split(" ").filter { it.isNotBlank() }
        .joinToString(" ") { word -> word.lowercase().replaceFirstChar { it.titlecase() } }
        .ifBlank { "Otros" }
