package com.qryde.qryderiderapp.core.di

import com.qryde.qryderiderapp.data.repository.ServerConfigRepositoryImpl
import com.qryde.qryderiderapp.domain.repository.ServerConfigRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindServerConfigRepository(impl: ServerConfigRepositoryImpl): ServerConfigRepository
}
