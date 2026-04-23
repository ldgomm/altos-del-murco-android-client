package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.data

import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain.LoyaltyRewardsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class ProfileRepositoryModule {

    @Binds
    abstract fun bindLoyaltyRewardsRepository(
        repository: NoOpLoyaltyRewardsRepository,
    ): LoyaltyRewardsRepository
}
