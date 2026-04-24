package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.di

import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.data.LoyaltyRewardsRepository
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.data.ProfileImageRepository
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.data.ProfileStatsRepository
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain.LoyaltyRewardsRepositoriable
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain.ProfileImageRepositoriable
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.profile.domain.ProfileStatsRepositoriable
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

    @Binds
    abstract fun bindProfileStatsRepository(
        repository: ProfileStatsRepository,
    ): ProfileStatsRepositoriable

    @Binds
    abstract fun bindProfileImageRepository(
        repository: ProfileImageRepository,
    ): ProfileImageRepositoriable
}
