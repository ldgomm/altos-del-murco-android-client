package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.di

import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.authentication.data.FirebaseAuthenticationRepository
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.authentication.data.ClientProfileRepository
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.authentication.domain.AuthenticationRepositoriable
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.authentication.domain.ClientProfileRepositoriable
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthRepositoryModule {

    @Binds
    abstract fun bindAuthenticationRepository(
        repository: FirebaseAuthenticationRepository,
    ): AuthenticationRepositoriable

    @Binds
    abstract fun bindClientProfileRepository(
        repository: ClientProfileRepository,
    ): ClientProfileRepositoriable
}
