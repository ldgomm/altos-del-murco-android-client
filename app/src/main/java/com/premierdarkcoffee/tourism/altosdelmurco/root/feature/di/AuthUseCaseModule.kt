package com.premierdarkcoffee.tourism.altosdelmurco.root.feature.di

import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.authentication.domain.AuthenticationRepositoriable
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.authentication.domain.ClientProfileRepositoriable
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.authentication.domain.CompleteClientProfileUseCase
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.authentication.domain.DeleteCurrentAccountUseCase
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.authentication.domain.ResolveSessionUseCase
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.authentication.domain.SignInWithGoogleUseCase
import com.premierdarkcoffee.tourism.altosdelmurco.root.feature.altos.authentication.domain.SignOutUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AuthUseCaseModule {

    @Provides
    fun provideResolveSessionUseCase(
        authRepository: AuthenticationRepositoriable,
        clientProfileRepository: ClientProfileRepositoriable,
    ): ResolveSessionUseCase = ResolveSessionUseCase(
        authRepository = authRepository,
        clientProfileRepository = clientProfileRepository,
    )

    @Provides
    fun provideSignInWithGoogleUseCase(
        repository: AuthenticationRepositoriable,
    ): SignInWithGoogleUseCase = SignInWithGoogleUseCase(repository)

    @Provides
    fun provideCompleteClientProfileUseCase(
        repository: ClientProfileRepositoriable,
    ): CompleteClientProfileUseCase = CompleteClientProfileUseCase(repository)

    @Provides
    fun provideDeleteCurrentAccountUseCase(
        authRepository: AuthenticationRepositoriable,
        clientProfileRepository: ClientProfileRepositoriable,
    ): DeleteCurrentAccountUseCase = DeleteCurrentAccountUseCase(
        authRepository = authRepository,
        clientProfileRepository = clientProfileRepository,
    )

    @Provides
    fun provideSignOutUseCase(
        repository: AuthenticationRepositoriable,
    ): SignOutUseCase = SignOutUseCase(repository)
}