package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain

import kotlinx.coroutines.flow.Flow

interface MenuRepositoriable {
    fun observeMenu(): Flow<List<MenuSection>>
}
