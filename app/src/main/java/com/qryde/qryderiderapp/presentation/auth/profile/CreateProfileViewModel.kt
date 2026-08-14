package com.qryde.qryderiderapp.presentation.auth.profile

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.qryde.qryderiderapp.core.common.AppResult
import com.qryde.qryderiderapp.core.logging.AppLogger
import com.qryde.qryderiderapp.domain.model.NewAccountDetails
import com.qryde.qryderiderapp.domain.usecase.CheckEmailAvailableUseCase
import com.qryde.qryderiderapp.domain.usecase.CheckUserIdAvailableUseCase
import com.qryde.qryderiderapp.domain.usecase.CreateAccountUseCase
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

sealed interface CreateProfileEvent {
    data object AccountCreated : CreateProfileEvent
    data class ShowError(val message: String) : CreateProfileEvent
}

@HiltViewModel
class CreateProfileViewModel @Inject constructor(
    private val checkUserIdAvailableUseCase: CheckUserIdAvailableUseCase,
    private val checkEmailAvailableUseCase: CheckEmailAvailableUseCase,
    private val createAccountUseCase: CreateAccountUseCase,
    private val loginUseCase: LoginUseCase,
    private val fetchJoinedCommunitiesUseCase: FetchJoinedCommunitiesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateProfileUiState())
    val uiState: StateFlow<CreateProfileUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<CreateProfileEvent>()
    val events: SharedFlow<CreateProfileEvent> = _events

    fun onAvatarSelected(uri: Uri?) {
        _uiState.update { it.copy(avatarUri = uri) }
    }

    fun onFirstNameChanged(value: String) {
        _uiState.update { it.copy(firstName = value) }
    }

    fun onLastNameChanged(value: String) {
        _uiState.update { it.copy(lastName = value) }
    }

    fun onUserIdChanged(value: String) {
        _uiState.update { it.copy(userId = value, userIdStatus = AvailabilityStatus.UNKNOWN, userIdStatusMessage = null) }
    }

    fun onEmailChanged(value: String) {
        _uiState.update { it.copy(email = value, emailStatus = AvailabilityStatus.UNKNOWN, emailStatusMessage = null) }
    }

    fun onPasswordChanged(value: String) {
        _uiState.update { it.copy(password = value) }
    }

    fun onPasswordVisibilityToggled() {
        _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    fun onTermsAcceptedChanged(accepted: Boolean) {
        _uiState.update { it.copy(isTermsAccepted = accepted) }
    }

    fun onUserIdFocusLost() {
        val userId = _uiState.value.userId
        if (userId.isBlank()) return

        viewModelScope.launch {
            _uiState.update { it.copy(userIdStatus = AvailabilityStatus.CHECKING) }
            when (val result = checkUserIdAvailableUseCase(userId)) {
                is AppResult.Success -> _uiState.update {
                    it.copy(userIdStatus = AvailabilityStatus.AVAILABLE, userIdStatusMessage = null)
                }
                is AppResult.Error -> _uiState.update {
                    it.copy(userIdStatus = AvailabilityStatus.UNAVAILABLE, userIdStatusMessage = result.message)
                }
            }
        }
    }

    fun onEmailFocusLost() {
        val email = _uiState.value.email
        if (email.isBlank()) return

        viewModelScope.launch {
            _uiState.update { it.copy(emailStatus = AvailabilityStatus.CHECKING) }
            when (val result = checkEmailAvailableUseCase(email)) {
                is AppResult.Success -> _uiState.update {
                    it.copy(emailStatus = AvailabilityStatus.AVAILABLE, emailStatusMessage = null)
                }
                is AppResult.Error -> _uiState.update {
                    it.copy(emailStatus = AvailabilityStatus.UNAVAILABLE, emailStatusMessage = result.message)
                }
            }
        }
    }

    fun onSubmitClicked(phoneNumber: String) {
        val state = _uiState.value
        if (!state.isSubmitEnabled) return

        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true) }
            val details = NewAccountDetails(
                firstName = state.firstName,
                lastName = state.lastName,
                userId = state.userId,
                email = state.email,
                password = state.password,
                isoCode = DEFAULT_ISO_CODE,
                phoneNumber = phoneNumber
            )
            when (val result = createAccountUseCase(details)) {
                is AppResult.Success -> {

                    when (val loginResult = loginUseCase(state.userId, state.password)) {
                        is AppResult.Success -> {
                            when (val communitiesResult = fetchJoinedCommunitiesUseCase(loginResult.data.userId)) {
                                is AppResult.Success -> Unit
                                is AppResult.Error -> AppLogger.w(TAG, communitiesResult.message)
                            }
                        }
                        is AppResult.Error -> AppLogger.w(TAG, "Auto-login after signup failed: ${loginResult.message}")
                    }
                    _uiState.update { it.copy(isSubmitting = false) }
                    _events.emit(CreateProfileEvent.AccountCreated)
                }
                is AppResult.Error -> {
                    _uiState.update { it.copy(isSubmitting = false) }
                    _events.emit(CreateProfileEvent.ShowError(result.message))
                }
            }
        }
    }

    private companion object {
        const val TAG = "CreateProfile"
        const val DEFAULT_ISO_CODE = "US"
    }
}
