package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain

import kotlinx.coroutines.flow.Flow

interface AdventureCatalogRepositoriable {
    suspend fun fetchCatalog(): AdventureCatalogSnapshot
    fun observeCatalog(): Flow<AdventureCatalogSnapshot>
}