package com.qryde.qryderiderapp.presentation.auth.otp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.qryde.qryderiderapp.core.common.AppResult
import com.qryde.qryderiderapp.core.logging.AppLogger
import com.qryde.qryderiderapp.domain.usecase.RegisterDeviceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface OtpVerificationEvent {
    data object Verified : OtpVerificationEvent
}

@HiltViewModel
class OtpVerificationViewModel @Inject constructor(
    private val registerDeviceUseCase: RegisterDeviceUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(OtpVerificationUiState())
    val uiState: StateFlow<OtpVerificationUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<OtpVerificationEvent>()
    val events: SharedFlow<OtpVerificationEvent> = _events

    private var countdownJob: Job? = null

    init {
        startCountdown()
    }

    fun onOtpChanged(value: String) {
        _uiState.update { it.copy(otp = value, errorMessage = null) }
    }

    fun onResendClicked() {
        if (!_uiState.value.canResend) return
        _uiState.update { it.copy(otp = "") }
        startCountdown()
        // TODO: trigger a fresh 100UV send once resend is wired up (currently only
        // the initial PhoneVerification screen send is implemented).
    }

    fun onVerifyClicked(expectedCode: String) {
        val state = _uiState.value
        if (!state.isVerifyEnabled) return

        viewModelScope.launch {
            if (expectedCode.isBlank() || state.otp == expectedCode) {
                when (val result = registerDeviceUseCase()) {
                    is AppResult.Success -> Unit
                    is AppResult.Error -> AppLogger.w(TAG, "Device registration skipped/failed: ${result.message}")
                }
                _events.emit(OtpVerificationEvent.Verified)
            } else {
                _uiState.update { it.copy(errorMessage = "Incorrect code. Please try again.") }
            }
        }
    }

    private fun startCountdown() {
        countdownJob?.cancel()
        _uiState.update { it.copy(remainingSeconds = OtpVerificationUiState.RESEND_COOLDOWN_SECONDS) }
        countdownJob = viewModelScope.launch {
            while (_uiState.value.remainingSeconds > 0) {
                delay(1_000)
                _uiState.update { it.copy(remainingSeconds = it.remainingSeconds - 1) }
            }
        }
    }

    override fun onCleared() {
        countdownJob?.cancel()
    }

    private companion object {
        const val TAG = "OtpVerification"
    }
}
