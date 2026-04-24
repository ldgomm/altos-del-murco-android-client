package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.di

import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.data.LoyaltyRewardsRepository
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain.LoyaltyRewardsRepositoriable
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class ProfileRepositoryModule {

    @Binds
    abstract fun bindLoyaltyRewardsRepository(
        repository: LoyaltyRewardsRepository,
    ): LoyaltyRewardsRepositoriable
}