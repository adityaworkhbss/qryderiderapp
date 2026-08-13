package com.qryde.qryderiderapp.core.di

import com.qryde.qryderiderapp.data.repository.AuthRepositoryImpl
import com.qryde.qryderiderapp.domain.repository.AuthRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository
}
