package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.data

import androidx.room.Embedded
import androidx.room.Relation

data class CartDraftWithItems(
    @Embedded val draft: CartDraftEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "draftId",
    )
    val items: List<CartItemEntity>,
)
