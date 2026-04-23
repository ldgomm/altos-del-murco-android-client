package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain

import kotlinx.coroutines.flow.Flow

interface AdventureCatalogRepository {
    suspend fun fetchCatalog(): AdventureCatalogSnapshot
    fun observeCatalog(): Flow<AdventureCatalogSnapshot>
}
