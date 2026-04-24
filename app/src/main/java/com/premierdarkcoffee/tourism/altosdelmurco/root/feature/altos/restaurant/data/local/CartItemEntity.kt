package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey


@Entity(
    tableName = "cart_items",
    foreignKeys = [
        ForeignKey(
            entity = CartDraftEntity::class,
            parentColumns = ["id"],
            childColumns = ["draftId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("draftId")],
)
data class CartItemEntity(
    @PrimaryKey val id: String,
    val draftId: String = CartDraftEntity.DEFAULT_ID,
    val menuItemId: String,
    val categoryId: String,
    val categoryTitle: String,
    val name: String,
    val description: String,
    val notes: String?,
    val ingredients: List<String>,
    val quantity: Int,
    val unitPrice: Double,
    val offerPrice: Double?,
    val imageURL: String?,
    val isAvailable: Boolean,
    val remainingQuantity: Int,
    val isFeatured: Boolean,
    val sortOrder: Int,
    val itemNotes: String?,
)