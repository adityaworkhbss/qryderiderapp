package com.qryde.qryderiderapp.presentation.auth.forgotpassword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.qryde.qryderiderapp.core.common.AppResult
import com.qryde.qryderiderapp.domain.usecase.RequestPasswordResetUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface ForgotPasswordEvent {
    data class Sent(val message: String) : ForgotPasswordEvent
    data class ShowError(val message: String) : ForgotPasswordEvent
}

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val requestPasswordResetUseCase: RequestPasswordResetUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ForgotPasswordUiState())
    val uiState: StateFlow<ForgotPasswordUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<ForgotPasswordEvent>()
    val events: SharedFlow<ForgotPasswordEvent> = _events

    fun onUserIdChanged(value: String) {
        _uiState.update { it.copy(userId = value) }
    }

    fun onEmailChanged(value: String) {
        _uiState.update { it.copy(email = value) }
    }

    fun onSendClicked() {
        val state = _uiState.value
        if (!state.isSendEnabled) return

        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true) }
            when (val result = requestPasswordResetUseCase(state.userId, state.email)) {
                is AppResult.Success -> {
                    _uiState.update { it.copy(isSubmitting = false) }
                    _events.emit(ForgotPasswordEvent.Sent(result.data))
                }
                is AppResult.Error -> {
                    _uiState.update { it.copy(isSubmitting = false) }
                    _events.emit(ForgotPasswordEvent.ShowError(result.message))
                }
            }
        }
    }
}
