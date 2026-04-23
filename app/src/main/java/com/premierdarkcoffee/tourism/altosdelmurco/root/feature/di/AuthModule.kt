package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.di

import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.authentication.data.FirebaseSessionRepository
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.authentication.domain.SessionRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthModule {
    @Binds
    abstract fun bindSessionRepository(
        repository: FirebaseSessionRepository,
    ): SessionRepository
}
