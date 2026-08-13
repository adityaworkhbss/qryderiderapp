package com.qryde.qryderiderapp.presentation.auth.otp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OtpVerificationViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(OtpVerificationUiState())
    val uiState: StateFlow<OtpVerificationUiState> = _uiState.asStateFlow()

    private var countdownJob: Job? = null

    init {
        startCountdown()
    }

    fun onOtpChanged(value: String) {
        _uiState.update { it.copy(otp = value) }
    }

    fun onResendClicked() {
        if (!_uiState.value.canResend) return
        _uiState.update { it.copy(otp = "") }
        startCountdown()
        // TODO: trigger the resend-OTP request once the real auth API contract is available.
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
}
