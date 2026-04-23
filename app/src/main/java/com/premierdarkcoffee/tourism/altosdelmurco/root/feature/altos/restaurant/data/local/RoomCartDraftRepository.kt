package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.data.local

import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.data.CartDao
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain.CartDraftRepository
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain.OrderDraft
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Singleton
class RoomCartDraftRepository @Inject constructor(
    private val cartDao: CartDao,
) : CartDraftRepository {

    override fun observeDraft(): Flow<OrderDraft> = cartDao.observeCart().map { stored ->
        stored?.toDomain() ?: OrderDraft()
    }

    override suspend fun saveDraft(draft: OrderDraft) {
        cartDao.replaceDraft(
            draft = draft.toEntity(),
            items = draft.items.map { it.toEntity(draft.id) },
        )
    }

    override suspend fun clear() {
        cartDao.clearAll()
    }
}
