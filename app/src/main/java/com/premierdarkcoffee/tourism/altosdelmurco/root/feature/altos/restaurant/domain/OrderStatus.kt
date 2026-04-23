package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain

enum class OrderStatus(val title: String) {
    PENDING("Pending"),
    CONFIRMED("Confirmed"),
    PREPARING("Preparing"),
    COMPLETED("Completed"),
    CANCELED("Canceled");

    companion object {
        fun fromRaw(rawValue: String?): OrderStatus = entries.firstOrNull {
            it.name.equals(rawValue, ignoreCase = true)
        } ?: PENDING
    }
}
