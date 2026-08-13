package com.qryde.qryderiderapp.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.qryde.qryderiderapp.domain.usecase.LogoutUseCase
import com.qryde.qryderiderapp.domain.usecase.ObserveCurrentUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    observeCurrentUser: ObserveCurrentUserUseCase,
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<HomeNavigationEvent>()
    val navigationEvent: SharedFlow<HomeNavigationEvent> = _navigationEvent

    init {
        viewModelScope.launch {
            observeCurrentUser().collect { user ->
                _uiState.value = _uiState.value.copy(user = user, isLoading = false)
            }
        }
    }

    fun onLogoutClicked() {
        viewModelScope.launch {
            logoutUseCase()
            _navigationEvent.emit(HomeNavigationEvent.LoggedOut)
        }
    }
}
