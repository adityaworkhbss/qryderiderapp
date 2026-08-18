package com.qryde.qryderiderapp.core.di

import com.qryde.qryderiderapp.data.repository.AuthRepositoryImpl
import com.qryde.qryderiderapp.data.repository.BraintreeRepositoryImpl
import com.qryde.qryderiderapp.data.repository.ClientDataRepositoryImpl
import com.qryde.qryderiderapp.data.repository.CommunityRepositoryImpl
import com.qryde.qryderiderapp.data.repository.DeviceRegistrationRepositoryImpl
import com.qryde.qryderiderapp.data.repository.ForgotPasswordRepositoryImpl
import com.qryde.qryderiderapp.data.repository.OeRegistryRepositoryImpl
import com.qryde.qryderiderapp.data.repository.PreferredCommunityRepositoryImpl
import com.qryde.qryderiderapp.data.repository.RegistrationRepositoryImpl
import com.qryde.qryderiderapp.data.repository.ServerConfigRepositoryImpl
import com.qryde.qryderiderapp.data.repository.SmsVerificationRepositoryImpl
import com.qryde.qryderiderapp.data.repository.StateCommunityRepositoryImpl
import com.qryde.qryderiderapp.data.repository.SuggestedAddressRepositoryImpl
import com.qryde.qryderiderapp.domain.repository.AuthRepository
import com.qryde.qryderiderapp.domain.repository.BraintreeRepository
import com.qryde.qryderiderapp.domain.repository.ClientDataRepository
import com.qryde.qryderiderapp.domain.repository.CommunityRepository
import com.qryde.qryderiderapp.domain.repository.DeviceRegistrationRepository
import com.qryde.qryderiderapp.domain.repository.ForgotPasswordRepository
import com.qryde.qryderiderapp.domain.repository.OeRegistryRepository
import com.qryde.qryderiderapp.domain.repository.PreferredCommunityRepository
import com.qryde.qryderiderapp.domain.repository.RegistrationRepository
import com.qryde.qryderiderapp.domain.repository.ServerConfigRepository
import com.qryde.qryderiderapp.domain.repository.SmsVerificationRepository
import com.qryde.qryderiderapp.domain.repository.StateCommunityRepository
import com.qryde.qryderiderapp.domain.repository.SuggestedAddressRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindServerConfigRepository(impl: ServerConfigRepositoryImpl): ServerConfigRepository

    @Binds
    abstract fun bindOeRegistryRepository(impl: OeRegistryRepositoryImpl): OeRegistryRepository

    @Binds
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds
    abstract fun bindCommunityRepository(impl: CommunityRepositoryImpl): CommunityRepository

    @Binds
    abstract fun bindSmsVerificationRepository(impl: SmsVerificationRepositoryImpl): SmsVerificationRepository

    @Binds
    abstract fun bindDeviceRegistrationRepository(impl: DeviceRegistrationRepositoryImpl): DeviceRegistrationRepository

    @Binds
    abstract fun bindForgotPasswordRepository(impl: ForgotPasswordRepositoryImpl): ForgotPasswordRepository

    @Binds
    abstract fun bindRegistrationRepository(impl: RegistrationRepositoryImpl): RegistrationRepository

    @Binds
    abstract fun bindBraintreeRepository(impl: BraintreeRepositoryImpl): BraintreeRepository

    @Binds
    abstract fun bindSuggestedAddressRepository(impl: SuggestedAddressRepositoryImpl): SuggestedAddressRepository

    @Binds
    abstract fun bindClientDataRepository(impl: ClientDataRepositoryImpl): ClientDataRepository

    @Binds
    abstract fun bindStateCommunityRepository(impl: StateCommunityRepositoryImpl): StateCommunityRepository

    @Binds
    abstract fun bindPreferredCommunityRepository(impl: PreferredCommunityRepositoryImpl): PreferredCommunityRepository
}
