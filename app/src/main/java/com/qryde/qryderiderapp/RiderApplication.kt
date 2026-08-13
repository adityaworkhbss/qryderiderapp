package com.qryde.qryderiderapp

import android.app.Application
import com.qryde.qryderiderapp.core.logging.AppLogger
import com.qryde.qryderiderapp.core.utils.AppConfig
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class RiderApplication : Application() {

    @Inject
    lateinit var appConfig: AppConfig

    override fun onCreate() {
        super.onCreate()
        AppLogger.init(isEnabled = appConfig.isDeveloperMode)
    }
}
