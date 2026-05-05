package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.presentation.viewmodel

import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain.AdventureCatalogSnapshot
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain.AdventureReservationItemDraft
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain.ReservationFoodItemDraft

data class RewardPreviewInput(
    val userId: String,
    val activityItems: List<AdventureReservationItemDraft>,
    val foodItems: List<ReservationFoodItemDraft>,
    val catalog: AdventureCatalogSnapshot,
)