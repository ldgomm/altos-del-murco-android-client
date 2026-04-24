package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.di

import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.data.local.CartDraftRepository
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.data.remote.MenuRepository
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.data.remote.OrdersRepository
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain.ClearCartDraftUseCase
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain.ObserveCartDraftUseCase
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain.ObserveMenuUseCase
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain.ObserveOrdersUseCase
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain.SaveCartDraftUseCase
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain.SubmitOrderUseCase
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

    @Provides
    fun provideObserveOrdersUseCase(
        repository: OrdersRepository,
    ): ObserveOrdersUseCase = ObserveOrdersUseCase(repository)

    @Provides
    fun provideSubmitOrderUseCase(
        repository: OrdersRepository,
    ): SubmitOrderUseCase = SubmitOrderUseCase(repository)

    @Provides
    fun provideObserveCartDraftUseCase(
        repository: CartDraftRepository,
    ): ObserveCartDraftUseCase = ObserveCartDraftUseCase(repository)

    @Provides
    fun provideSaveCartDraftUseCase(
        repository: CartDraftRepository,
    ): SaveCartDraftUseCase = SaveCartDraftUseCase(repository)

    @Provides
    fun provideClearCartDraftUseCase(
        repository: CartDraftRepository,
    ): ClearCartDraftUseCase = ClearCartDraftUseCase(repository)
}
