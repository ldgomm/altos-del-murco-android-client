package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.presentation.viewmodel

import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain.AdventureCatalogSnapshot

data class AdventureCatalogUiState(
    val isLoading: Boolean = true,
    val catalog: AdventureCatalogSnapshot = AdventureCatalogSnapshot.EMPTY,
    val errorMessage: String? = null,
)