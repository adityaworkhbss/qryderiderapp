package com.qryde.qryderiderapp.presentation.payments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.qryde.qryderiderapp.core.braintree.BraintreeDropInBridge
import com.qryde.qryderiderapp.core.braintree.BraintreeDropInResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface AddFundsEvent {
    data class PaymentMethodAdded(val paymentMethodNonce: String?) : AddFundsEvent
    data class DropInError(val message: String) : AddFundsEvent
}

@HiltViewModel
class AddFundsViewModel @Inject constructor(
    private val braintreeDropInBridge: BraintreeDropInBridge
) : ViewModel() {

    private val _events = MutableSharedFlow<AddFundsEvent>()
    val events: SharedFlow<AddFundsEvent> = _events.asSharedFlow()

    init {
        viewModelScope.launch {
            braintreeDropInBridge.results.collect { result ->
                when (result) {
                    is BraintreeDropInResult.Success ->
                        _events.emit(AddFundsEvent.PaymentMethodAdded(result.paymentMethodNonce))
                    is BraintreeDropInResult.Failure ->
                        _events.emit(AddFundsEvent.DropInError(result.error.message ?: "Something went wrong"))
                }
            }
        }
    }

    fun onAddCardClicked() {
        braintreeDropInBridge.launch()
    }
}
