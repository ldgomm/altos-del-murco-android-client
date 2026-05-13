package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain

private fun String.normalizedOrderItemStatusKey(): String =
    filter(Char::isLetterOrDigit).lowercase()

enum class OrderItemStatus(
    val rawValue: String,
    val title: String,
    val clientTitle: String,
) {
    PENDING(
        rawValue = "pending",
        title = "Pendiente",
        clientTitle = "En espera",
    ),
    PREPARING(
        rawValue = "preparing",
        title = "Preparando",
        clientTitle = "Preparando",
    ),
    READY_FOR_DELIVERY(
        rawValue = "readyForDelivery",
        title = "Listo",
        clientTitle = "Listo",
    ),
    DELIVERED(
        rawValue = "delivered",
        title = "Servido",
        clientTitle = "Servido",
    ),
    CANCELED(
        rawValue = "canceled",
        title = "Cancelado",
        clientTitle = "Cancelado",
    );

    val isActive: Boolean
        get() = this != CANCELED

    val hasStarted: Boolean
        get() = when (this) {
            PREPARING,
            READY_FOR_DELIVERY,
            DELIVERED -> true

            PENDING,
            CANCELED -> false
        }

    companion object {
        fun fromRaw(rawValue: String?): OrderItemStatus {
            val key = rawValue?.normalizedOrderItemStatusKey().orEmpty()

            return entries.firstOrNull {
                it.rawValue.normalizedOrderItemStatusKey() == key ||
                        it.name.normalizedOrderItemStatusKey() == key
            } ?: PENDING
        }
    }
}