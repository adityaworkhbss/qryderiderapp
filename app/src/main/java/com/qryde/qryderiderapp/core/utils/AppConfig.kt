package com.qryde.qryderiderapp.core.utils

enum class Tenant {
    QRYDE
}

enum class Environment {
    DEV,
    STAGING,
    PROD
}

data class AppConfig(
    val tenant: Tenant,
    val environment: Environment,
    val apiBaseUrl: String,
    val webSocketUrl: String,
    val appName: String,
    val isDeveloperMode: Boolean,
    val bypassOtp: Boolean,
    val sendAnalytics: Boolean
)
