package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.di

import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain.MenuRepository
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain.ObserveMenuUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object RestaurantUseCaseModule {

    @Provides
    fun provideObserveMenuUseCase(
        repository: MenuRepository,
    ): ObserveMenuUseCase = ObserveMenuUseCase(repository)
}
