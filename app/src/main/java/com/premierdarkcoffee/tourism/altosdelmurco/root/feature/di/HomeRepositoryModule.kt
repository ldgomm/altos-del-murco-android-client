package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.di

import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.home.data.FeaturedFeedRepository
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.home.domain.FeaturedFeedRepositoriable
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class HomeRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindFeaturedFeedRepository(
        repository: FeaturedFeedRepository,
    ): FeaturedFeedRepositoriable
}