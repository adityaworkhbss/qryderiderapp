package com.qryde.qryderiderapp.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.qryde.qryderiderapp.core.common.AppResult
import com.qryde.qryderiderapp.core.logging.AppLogger
import com.qryde.qryderiderapp.domain.usecase.FetchBraintreeClientTokenUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BraintreeViewModel @Inject constructor(
    private val fetchBraintreeClientTokenUseCase: FetchBraintreeClientTokenUseCase
) : ViewModel() {

    init {
        viewModelScope.launch {
            when (val result = fetchBraintreeClientTokenUseCase()) {
                is AppResult.Success -> AppLogger.d(TAG, "Braintree client token ready")
                is AppResult.Error -> AppLogger.w(TAG, "Braintree client token fetch failed: ${result.message}")
            }
        }
    }

    private companion object {
        const val TAG = "Braintree"
    }
}
