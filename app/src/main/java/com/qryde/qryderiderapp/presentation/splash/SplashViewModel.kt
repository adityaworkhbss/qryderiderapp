package com.qryde.qryderiderapp.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.qryde.qryderiderapp.core.common.AppResult
import com.qryde.qryderiderapp.core.logging.AppLogger
import com.qryde.qryderiderapp.core.utils.AppConfig
import com.qryde.qryderiderapp.domain.usecase.FetchOeRegistryValuesUseCase
import com.qryde.qryderiderapp.domain.usecase.ResolveServerConfigUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface SplashNavigationEvent {
    data object ConfigResolved : SplashNavigationEvent
}

@HiltViewModel
class SplashViewModel @Inject constructor(
    appConfig: AppConfig,
    private val resolveServerConfigUseCase: ResolveServerConfigUseCase,
    private val fetchOeRegistryValuesUseCase: FetchOeRegistryValuesUseCase
) : ViewModel() {

    val appName: String = appConfig.appName

    private val _navigationEvent = MutableSharedFlow<SplashNavigationEvent>()
    val navigationEvent: SharedFlow<SplashNavigationEvent> = _navigationEvent

    private val _errorEvent = MutableSharedFlow<String>()
    val errorEvent: SharedFlow<String> = _errorEvent

    private var configResolutionStarted = false

    fun onPermissionsResolved() {
        if (configResolutionStarted) return
        configResolutionStarted = true
        resolveConfig()
    }

    private fun resolveConfig() {
        viewModelScope.launch {
            when (val result = resolveServerConfigUseCase()) {
                is AppResult.Success -> {
                    when (val oeRegistryResult = fetchOeRegistryValuesUseCase(result.data)) {
                        is AppResult.Success -> Unit
                        is AppResult.Error -> AppLogger.w(TAG, oeRegistryResult.message)
                    }
                    _navigationEvent.emit(SplashNavigationEvent.ConfigResolved)
                }
                is AppResult.Error -> {
                    AppLogger.w(TAG, "Failed to resolve server config: ${result.message}")
                    _errorEvent.emit(result.message)
                }
            }
        }
    }

    private companion object {
        const val TAG = "Splash"
    }
}
