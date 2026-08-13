package com.qryde.qryderiderapp.presentation.auth.verification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.qryde.qryderiderapp.core.common.AppResult
import com.qryde.qryderiderapp.domain.usecase.SendVerificationCodeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

sealed interface PhoneVerificationEvent {
    data class CodeSent(val contact: String, val expectedCode: String) : PhoneVerificationEvent
    data class ShowError(val message: String) : PhoneVerificationEvent
}

@HiltViewModel
class PhoneVerificationViewModel @Inject constructor(
    private val sendVerificationCodeUseCase: SendVerificationCodeUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(PhoneVerificationUiState())
    val uiState: StateFlow<PhoneVerificationUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<PhoneVerificationEvent>()
    val events: SharedFlow<PhoneVerificationEvent> = _events

    fun onContactChanged(value: String) {
        _uiState.update { it.copy(contact = value) }
    }

    /**
     * userId is blank pre-signup (no account exists yet) and real for the
     * LOGIN flow (from the 5G response) - the server accepts a blank/rc_null
     * userId either way, confirmed against real sign-up traffic.
     */
    fun onSendOtpClicked(userId: String, isoCode: String) {
        val state = _uiState.value
        if (!state.isSendEnabled) return

        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true) }
            val code = generateVerificationCode()
            when (val result = sendVerificationCodeUseCase(userId, isoCode, state.contact, code)) {
                is AppResult.Success -> {
                    _uiState.update { it.copy(isSubmitting = false) }
                    _events.emit(PhoneVerificationEvent.CodeSent(state.contact, code))
                }
                is AppResult.Error -> {
                    _uiState.update { it.copy(isSubmitting = false) }
                    _events.emit(PhoneVerificationEvent.ShowError(result.message))
                }
            }
        }
    }

    private fun generateVerificationCode(): String =
        Random.nextInt(0, 1_000_000).toString().padStart(6, '0')
}
