package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart_drafts")
data class CartDraftEntity(
    @PrimaryKey val id: String = DEFAULT_ID,
    val nationalId: String?,
    val clientName: String,
    val tableNumber: String,
    val scheduledAtMillis: Long,
    val revision: Int?,
    val lastConfirmedRevision: Int?,
    val createdAtMillis: Long,
    val updatedAtMillis: Long,
) {
    companion object {
        const val DEFAULT_ID: String = "active_cart"
    }
}
