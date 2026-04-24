package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain

class ObserveOrdersUseCase(
    private val repository: OrdersRepositoriable,
) {
    fun execute(nationalId: String) = repository.observeOrders(nationalId)
}

class SubmitOrderUseCase(
    private val repository: OrdersRepositoriable,
) {
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
