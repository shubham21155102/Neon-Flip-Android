package com.neonflip.di

import com.neonflip.data.repository.AuthRepositoryImpl
import com.neonflip.data.repository.ScoreRepositoryImpl
import com.neonflip.domain.repository.AuthRepository
import com.neonflip.domain.repository.ScoreRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindScoreRepository(
        scoreRepositoryImpl: ScoreRepositoryImpl
    ): ScoreRepository
}
