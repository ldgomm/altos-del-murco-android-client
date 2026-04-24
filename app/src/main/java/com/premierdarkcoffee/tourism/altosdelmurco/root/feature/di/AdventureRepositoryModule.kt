package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.di

import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.booking.data.AdventureBookingsRepository
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.data.AdventureCatalogRepository
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.booking.domain.AdventureBookingsRepositoriable
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain.AdventureCatalogRepositoriable
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AdventureRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAdventureCatalogRepository(
        repository: AdventureCatalogRepository,
    ): AdventureCatalogRepositoriable

    @Binds
    @Singleton
    abstract fun bindAdventureBookingsRepository(
        repository: AdventureBookingsRepository,
    ): AdventureBookingsRepositoriable
}