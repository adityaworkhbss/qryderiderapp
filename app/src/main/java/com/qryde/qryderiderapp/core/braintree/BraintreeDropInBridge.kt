package com.qryde.qryderiderapp.core.braintree

import androidx.fragment.app.FragmentActivity
import com.braintreepayments.api.ClientTokenCallback
import com.braintreepayments.api.ClientTokenProvider
import com.braintreepayments.api.DropInClient
import com.braintreepayments.api.DropInListener
import com.braintreepayments.api.DropInRequest
import com.braintreepayments.api.DropInResult
import com.qryde.qryderiderapp.core.common.AppResult
import com.qryde.qryderiderapp.core.logging.AppLogger
import com.qryde.qryderiderapp.domain.usecase.FetchBraintreeClientTokenUseCase
import com.qryde.qryderiderapp.domain.usecase.ObserveBraintreeClientTokenUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class BraintreeDropInBridge @Inject constructor(
    private val observeBraintreeClientTokenUseCase: ObserveBraintreeClientTokenUseCase,
    private val fetchBraintreeClientTokenUseCase: FetchBraintreeClientTokenUseCase
) : ClientTokenProvider, DropInListener {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private var dropInClient: DropInClient? = null

    private val _results = MutableSharedFlow<BraintreeDropInResult>(extraBufferCapacity = 1)
    val results: SharedFlow<BraintreeDropInResult> = _results.asSharedFlow()

    fun attach(activity: FragmentActivity) {
        dropInClient = DropInClient(activity, this).also { it.setListener(this) }
    }

    fun detach() {
        dropInClient = null
    }

    /** Launches the Drop-in UI - call once the rider asks to add a payment method. */
    fun launch() {
        val client = dropInClient
        if (client == null) {
            _results.tryEmit(BraintreeDropInResult.Failure(IllegalStateException("Drop-in is not ready yet")))
            return
        }
        client.launchDropIn(DropInRequest())
    }

    override fun getClientToken(callback: ClientTokenCallback) {
        scope.launch {
            val cached = observeBraintreeClientTokenUseCase().first()
            val token = cached ?: when (val result = fetchBraintreeClientTokenUseCase()) {
                is AppResult.Success -> result.data
                is AppResult.Error -> null
            }
            if (token != null) {
                callback.onSuccess(token)
            } else {
                callback.onFailure(IllegalStateException("Could not load a Braintree client token"))
            }
        }
    }

    override fun onDropInSuccess(result: DropInResult) {
        AppLogger.d(TAG, "Drop-in succeeded")
        _results.tryEmit(BraintreeDropInResult.Success(result.paymentMethodNonce?.string))
    }

    override fun onDropInFailure(error: Exception) {
        AppLogger.w(TAG, "Drop-in failed", error)
        _results.tryEmit(BraintreeDropInResult.Failure(error))
    }

    private companion object {
        const val TAG = "Braintree"
    }
}
