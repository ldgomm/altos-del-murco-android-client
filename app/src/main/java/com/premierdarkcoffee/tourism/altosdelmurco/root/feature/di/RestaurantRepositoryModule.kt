package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.di

import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.data.local.RoomCartDraftRepository
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.data.remote.FirebaseMenuRepository
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.data.remote.FirebaseOrdersRepository
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain.CartDraftRepository
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain.MenuRepository
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.restaurant.domain.OrdersRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RestaurantRepositoryModule {

    @Binds
    abstract fun bindMenuRepository(repository: FirebaseMenuRepository): MenuRepository

    @Binds
    abstract fun bindOrdersRepository(repository: FirebaseOrdersRepository): OrdersRepository

    @Binds
    abstract fun bindCartDraftRepository(repository: RoomCartDraftRepository): CartDraftRepository
}