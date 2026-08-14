package com.qryde.qryderiderapp

import android.app.Application
import com.qryde.qryderiderapp.core.logging.AppLogger
import com.qryde.qryderiderapp.core.utils.AppConfig
import dagger.hilt.android.HiltAndroidApp
import org.osmdroid.config.Configuration
import java.io.File
import javax.inject.Inject

@HiltAndroidApp
class RiderApplication : Application() {

    @Inject
    lateinit var appConfig: AppConfig

    override fun onCreate() {
        super.onCreate()
        AppLogger.init(isEnabled = appConfig.isDeveloperMode)
        configureOsmdroid()
    }

    private fun configureOsmdroid() {
        val osmCacheDir = File(cacheDir, "osmdroid")
        Configuration.getInstance().apply {
            userAgentValue = packageName
            osmdroidBasePath = osmCacheDir
            osmdroidTileCache = File(osmCacheDir, "tiles")
        }
    }
}
