package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.di

import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.data.local.CartDraftRepository
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.data.remote.MenuRepository
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.data.remote.OrdersRepository
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain.CartDraftRepositoriable
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain.MenuRepositoriable
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain.OrdersRepositoriable
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RestaurantRepositoryModule {

    @Binds
    abstract fun bindMenuRepository(repository: MenuRepository): MenuRepositoriable

    @Binds
    abstract fun bindOrdersRepository(repository: OrdersRepository): OrdersRepositoriable

    @Binds
    abstract fun bindCartDraftRepository(repository: CartDraftRepository): CartDraftRepositoriable
}