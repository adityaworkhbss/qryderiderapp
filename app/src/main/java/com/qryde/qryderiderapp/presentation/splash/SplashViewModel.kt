package com.qryde.qryderiderapp.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.qryde.qryderiderapp.core.common.AppResult
import com.qryde.qryderiderapp.core.logging.AppLogger
import com.qryde.qryderiderapp.core.utils.AppConfig
import com.qryde.qryderiderapp.domain.usecase.AttemptSilentLoginUseCase
import com.qryde.qryderiderapp.domain.usecase.FetchClientDataUseCase
import com.qryde.qryderiderapp.domain.usecase.FetchJoinedCommunitiesUseCase
import com.qryde.qryderiderapp.domain.usecase.FetchOeRegistryValuesUseCase
import com.qryde.qryderiderapp.domain.usecase.ResolveServerConfigUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface SplashNavigationEvent {
    data object NavigateToAuth : SplashNavigationEvent
    data object NavigateToHome : SplashNavigationEvent
}

@HiltViewModel
class SplashViewModel @Inject constructor(
    appConfig: AppConfig,
    private val resolveServerConfigUseCase: ResolveServerConfigUseCase,
    private val fetchOeRegistryValuesUseCase: FetchOeRegistryValuesUseCase,
    private val attemptSilentLoginUseCase: AttemptSilentLoginUseCase,
    private val fetchJoinedCommunitiesUseCase: FetchJoinedCommunitiesUseCase,
    private val fetchClientDataUseCase: FetchClientDataUseCase
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
                    _navigationEvent.emit(resolveNextDestination())
                }
                is AppResult.Error -> {
                    AppLogger.w(TAG, "Failed to resolve server config: ${result.message}")
                    _errorEvent.emit(result.message)
                }
            }
        }
    }

    // Not-stored is treated the same as a failed attempt: fall back to the
    // auth graph so the user can log in by hand.
    private suspend fun resolveNextDestination(): SplashNavigationEvent {
        val silentLoginResult = attemptSilentLoginUseCase() ?: return SplashNavigationEvent.NavigateToAuth

        return when (silentLoginResult) {
            is AppResult.Success -> {
                when (val communitiesResult = fetchJoinedCommunitiesUseCase(silentLoginResult.data.userId)) {
                    is AppResult.Success -> {
                        when (val clientDataResult = fetchClientDataUseCase()) {
                            is AppResult.Success -> Unit
                            is AppResult.Error -> AppLogger.w(TAG, clientDataResult.message)
                        }
                    }
                    is AppResult.Error -> AppLogger.w(TAG, communitiesResult.message)
                }
                SplashNavigationEvent.NavigateToHome
            }
            is AppResult.Error -> {
                AppLogger.w(TAG, "Silent login failed: ${silentLoginResult.message}")
                SplashNavigationEvent.NavigateToAuth
            }
        }
    }

    private companion object {
        const val TAG = "Splash"
    }
}
