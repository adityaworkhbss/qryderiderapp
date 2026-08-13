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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    appConfig: AppConfig,
    private val resolveServerConfigUseCase: ResolveServerConfigUseCase,
    private val fetchOeRegistryValuesUseCase: FetchOeRegistryValuesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SplashUiState(appName = appConfig.appName))
    val uiState: StateFlow<SplashUiState> = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<SplashNavigationEvent>()
    val navigationEvent: SharedFlow<SplashNavigationEvent> = _navigationEvent

    private var configResolutionStarted = false

    fun onPermissionsResolved() {
        if (configResolutionStarted) return
        configResolutionStarted = true
        resolveConfig()
    }

    fun onRetryClicked() {
        configResolutionStarted = true
        resolveConfig()
    }

    private fun resolveConfig() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            when (val result = resolveServerConfigUseCase()) {
                is AppResult.Success -> {
                    when (val oeRegistryResult = fetchOeRegistryValuesUseCase(result.data)) {
                        is AppResult.Success -> Unit
                        is AppResult.Error -> AppLogger.w(TAG, oeRegistryResult.message)
                    }
                    _uiState.value = _uiState.value.copy(isLoading = false)
                    _navigationEvent.emit(SplashNavigationEvent.ConfigResolved)
                }
                is AppResult.Error -> {
                    _uiState.value = _uiState.value.copy(isLoading = false, error = result.message)
                }
            }
        }
    }

    private companion object {
        const val TAG = "Splash"
    }
}
