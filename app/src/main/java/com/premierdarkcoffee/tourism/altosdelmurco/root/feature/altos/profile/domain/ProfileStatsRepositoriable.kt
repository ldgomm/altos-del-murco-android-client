package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain

import kotlinx.coroutines.flow.Flow

interface ProfileStatsRepositoriable {
    suspend fun loadStats(nationalId: String): ProfileStats
    fun observeStats(nationalId: String): Flow<ProfileStats>
}