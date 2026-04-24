package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain

import kotlinx.coroutines.flow.Flow

interface CartDraftRepositoriable {
    fun observeDraft(): Flow<OrderDraft>
    suspend fun saveDraft(draft: OrderDraft)
    suspend fun clear()
}
