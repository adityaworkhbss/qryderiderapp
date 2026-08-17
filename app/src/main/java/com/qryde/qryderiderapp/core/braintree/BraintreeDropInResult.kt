package com.qryde.qryderiderapp.core.braintree

sealed interface BraintreeDropInResult {
    data class Success(val paymentMethodNonce: String?) : BraintreeDropInResult
    data class Failure(val error: Throwable) : BraintreeDropInResult
}
