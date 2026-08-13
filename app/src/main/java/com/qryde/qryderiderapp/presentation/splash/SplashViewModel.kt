package com.qryde.qryderiderapp.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.qryde.qryderiderapp.core.common.AppResult
import com.qryde.qryderiderapp.core.utils.AppConfig
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
    private val resolveServerConfigUseCase: ResolveServerConfigUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SplashUiState(appName = appConfig.appName))
    val uiState: StateFlow<SplashUiState> = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<SplashNavigationEvent>()
    val navigationEvent: SharedFlow<SplashNavigationEvent> = _navigationEvent

    // The permission request is a one-time gate the UI runs before the config
    // call; guarded here too so a recomposition (e.g. rotation while the
    // permission dialog is up) can't fire a second resolveConfig() call.
    private var configResolutionStarted = false

    /** Called by the UI once the permission flow (granted, denied, or already held) is done. */
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
                    _uiState.value = _uiState.value.copy(isLoading = false)
                    // Next step (once specified): hit the follow-up REST call here
                    // before emitting ConfigResolved, using result.data's resolved URLs.
                    _navigationEvent.emit(SplashNavigationEvent.ConfigResolved)
                }
                is AppResult.Error -> {
                    _uiState.value = _uiState.value.copy(isLoading = false, error = result.message)
                }
            }
        }
    }
}
