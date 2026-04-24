package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain

import kotlinx.coroutines.flow.Flow

class ObserveMenuUseCase(
    private val repository: MenuRepositoriable,
) {
    fun execute(): Flow<List<MenuSection>> = repository.observeMenu()
}
