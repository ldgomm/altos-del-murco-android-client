package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.authentication.domain

import kotlinx.coroutines.flow.Flow

interface SessionRepositoriable {
    fun sessionState(): Flow<SessionState>
    suspend fun refresh()
}
