package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain

data class MenuItem(
    val id: String,
    val categoryId: String,
    val categoryTitle: String = "",
    val name: String,
    val description: String,
    val notes: String? = null,
    val ingredients: List<String>,
    val price: Double,
    val offerPrice: Double? = null,
    val imageURL: String? = null,
    val isAvailable: Boolean = true,
    val remainingQuantity: Int = 20,
    val isFeatured: Boolean = false,
    val sortOrder: Int = 0,
) {
    val hasOffer: Boolean = offerPrice != null && offerPrice < price

    val finalPrice: Double = offerPrice ?: price

    val isSoldOut: Boolean = remainingQuantity <= 0

    val canBeOrdered: Boolean = isAvailable && remainingQuantity > 0

    val stockLabel: String
        get() = when {
            !isAvailable -> "No disponible"
            remainingQuantity <= 0 -> "Agotado"
            remainingQuantity == 1 -> "Último plato"
            remainingQuantity <= 5 -> "Quedan $remainingQuantity"
            else -> "$remainingQuantity disponibles"
        }
}
