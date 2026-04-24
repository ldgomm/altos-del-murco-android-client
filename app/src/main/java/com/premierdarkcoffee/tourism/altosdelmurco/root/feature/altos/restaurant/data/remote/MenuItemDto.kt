package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.data.remote

import com.google.firebase.Timestamp
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
    fun toDomain(): MenuItem = MenuItem(
        id = id,
        categoryId = categoryId,
        categoryTitle = categoryTitle,
        name = name,
        description = description,
        notes = notes,
        ingredients = ingredients,
        price = price,
        offerPrice = offerPrice,
        imageURL = imageURL,
        isAvailable = isAvailable,
        remainingQuantity = remainingQuantity.coerceAtLeast(0),
        isFeatured = isFeatured,
        sortOrder = sortOrder,
    )
}