package com.qryde.qryderiderapp.core.di

import android.content.Context
import com.qryde.qryderiderapp.BuildConfig
import com.qryde.qryderiderapp.R
import com.qryde.qryderiderapp.core.utils.AppConfig
import com.qryde.qryderiderapp.core.utils.Environment
import com.qryde.qryderiderapp.core.utils.Tenant
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppConfigModule {

    @Provides
    @Singleton
    fun provideAppConfig(@ApplicationContext context: Context): AppConfig {
        val resources = context.resources
        val environment = when (BuildConfig.FLAVOR) {
            "dev" -> Environment.DEV
            "staging" -> Environment.STAGING
            else -> Environment.PROD
        }
        return AppConfig(
            tenant = Tenant.QRYDE,
            environment = environment,
            apiBaseUrl = resources.getString(R.string.api_base_url),
            webSocketUrl = resources.getString(R.string.websocket_url),
            appName = resources.getString(R.string.app_name),
            isDeveloperMode = resources.getBoolean(R.bool.isDeveloperMode),
            bypassOtp = resources.getBoolean(R.bool.bypassOtp),
            sendAnalytics = resources.getBoolean(R.bool.isSendAnalytics)
        )
    }
}
