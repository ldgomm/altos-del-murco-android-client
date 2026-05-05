package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain

import kotlinx.coroutines.flow.Flow

interface ProfileStatsRepositoriable {
    suspend fun loadStats(userId: String): ProfileStats
    fun observeStats(userId: String): Flow<ProfileStats>
}
