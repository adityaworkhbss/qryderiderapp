package com.qryde.qryderiderapp.presentation.home

import com.qryde.qryderiderapp.domain.model.User

data class HomeUiState(
    val user: User? = null,
    val isLoading: Boolean = true
)

sealed interface HomeNavigationEvent {
    data object LoggedOut : HomeNavigationEvent
}
