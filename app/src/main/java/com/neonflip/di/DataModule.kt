package com.neonflip.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * Data module for providing data layer dependencies.
 * Note: TokenStorage is automatically provided by Hilt via @Inject constructor.
 */
@Module
@InstallIn(SingletonComponent::class)
object DataModule

