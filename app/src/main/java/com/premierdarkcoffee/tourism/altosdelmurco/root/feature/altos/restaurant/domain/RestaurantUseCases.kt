package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain

import javax.inject.Inject

class ObserveOrdersUseCase @Inject constructor(private val repository: OrdersRepositoriable) {
    fun execute(userId: String) = repository.observeOrders(userId)
}

class SubmitOrderUseCase @Inject constructor(private val repository: OrdersRepositoriable) {
    suspend fun execute(order: Order) = repository.submit(order)
}

class ObserveCartDraftUseCase(
    private val repository: CartDraftRepositoriable,
) {
    fun execute() = repository.observeDraft()
}

class SaveCartDraftUseCase(
    private val repository: CartDraftRepositoriable,
) {
    suspend fun execute(draft: OrderDraft) = repository.saveDraft(draft)
}

class ClearCartDraftUseCase(
    private val repository: CartDraftRepositoriable,
) {
    suspend fun execute() = repository.clear()
}
