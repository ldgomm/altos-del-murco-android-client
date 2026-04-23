package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.data

import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.adventure.domain.AdventureCatalogRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class AdventureRepositoryModule {

    @Binds
    abstract fun bindAdventureCatalogRepository(
        repository: FirebaseAdventureCatalogRepository,
    ): AdventureCatalogRepository
}
