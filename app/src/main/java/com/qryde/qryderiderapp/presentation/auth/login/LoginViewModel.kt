package com.qryde.qryderiderapp.presentation.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.qryde.qryderiderapp.core.common.AppResult
import com.qryde.qryderiderapp.core.logging.AppLogger
import com.qryde.qryderiderapp.domain.usecase.FetchJoinedCommunitiesUseCase
import com.qryde.qryderiderapp.domain.usecase.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface LoginEvent {
    data class LoginSucceeded(val userId: String, val isoCode: String) : LoginEvent
    data object PasswordResetRequired : LoginEvent
    data class ShowError(val message: String) : LoginEvent
}

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val fetchJoinedCommunitiesUseCase: FetchJoinedCommunitiesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<LoginEvent>()
    val events: SharedFlow<LoginEvent> = _events

    fun onUserIdChanged(value: String) {
        _uiState.update { it.copy(userId = value) }
    }

    fun onPasswordChanged(value: String) {
        _uiState.update { it.copy(password = value) }
    }

    fun onPasswordVisibilityToggled() {
        _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    fun onLoginClicked() {
        val state = _uiState.value
        if (!state.isLoginEnabled) return

        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true) }
            when (val result = loginUseCase(state.userId, state.password)) {
                is AppResult.Success -> {
                    // 20AUC is fetched regardless of requiresPasswordReset, matching the
                    // legacy client (it always fetches joined communities right after a
                    // successful 5G, and only branches on isUserActive afterward).
                    when (val communitiesResult = fetchJoinedCommunitiesUseCase(result.data.userId)) {
                        is AppResult.Success -> Unit
                        is AppResult.Error -> AppLogger.w(TAG, communitiesResult.message)
                    }
                    _uiState.update { it.copy(isSubmitting = false) }
                    if (result.data.requiresPasswordReset) {
                        _events.emit(LoginEvent.PasswordResetRequired)
                    } else {
                        _events.emit(LoginEvent.LoginSucceeded(result.data.userId, result.data.isoCode))
                    }
                }
                is AppResult.Error -> {
                    _uiState.update { it.copy(isSubmitting = false) }
                    _events.emit(LoginEvent.ShowError(result.message))
                }
            }
        }
    }

    private companion object {
        const val TAG = "Login"
    }
}
