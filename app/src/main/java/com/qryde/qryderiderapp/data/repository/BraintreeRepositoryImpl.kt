package com.qryde.qryderiderapp.data.repository

import com.qryde.qryderiderapp.core.common.AppResult
import com.qryde.qryderiderapp.core.logging.AppLogger
import com.qryde.qryderiderapp.data.datastore.BraintreeDataStore
import com.qryde.qryderiderapp.data.datastore.LoginSessionDataStore
import com.qryde.qryderiderapp.data.datastore.ServerConfigDataStore
import com.qryde.qryderiderapp.data.mapper.toBraintreeClientToken
import com.qryde.qryderiderapp.data.mapper.toBraintreeCustomerId
import com.qryde.qryderiderapp.data.remote.rest.QtipCommandClient
import com.qryde.qryderiderapp.domain.repository.BraintreeRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/*

8BTC :
req : https://reststg.qryde.net:443/QTIP_API/8BTC  data ::  nt5555QRyde...
res : 8BTC~43210248536...


8CTC:
req : https://reststg.qryde.net:443/QTIP_API/8CTC :: 43210248536
res : 8CTC~eyJ2ZXJzaW9uIjoyLCJhdXRob3JpemF0aW9uRmluZ2VycHJpbnQiOiJleUpyYVdRaU9pSXlNREU0TURReU5qRTJMWE5oYm1SaWIzZ2lMQ0pwYzNNaU9pSm9kSFJ3Y3pvdkwyRndhUzV6WVc1a1ltOTRMbUp5WVdsdWRISmxaV2RoZEdWM1lYa3VZMjl0SWl3aVlXeG5Jam9pUlZNeU5UWWlmUS5leUpsZUhBaU9qRTNPRFkzTWpneE9UWXNJbXAwYVNJNklqQXpaREpqWXpRM0xXTTRNR0l0TkRKaU9DMWlaV05oTFdRd1lUTm1OVGxtWkRnek9DSXNJbk4xWWlJNkluSTRaM1o0Y1hGbU1qYzRkek5qT1RVaUxDSnBjM01pT2lKb2RIUndjem92TDJGd2FTNXpZVzVrWW05NExtSnlZV2x1ZEhKbFpXZGhkR1YzWVhrdVkyOXRJaXdpYldWeVkyaGhiblFpT25zaWNIVmliR2xqWDJsa0lqb2ljamhuZG5oeGNXWXlOemgzTTJNNU5TSXNJblpsY21sbWVWOWpZWEprWDJKNVgyUmxabUYxYkhRaU9uUnlkV1VzSW5abGNtbG1lVjkzWVd4c1pYUmZZbmxmWkdWbVlYVnNkQ0k2Wm1Gc2MyVjlMQ0p5YVdkb2RITWlPbHNpYldGdVlXZGxYM1poZFd4MElsMHNJbk5qYjNCbElqcGJJa0p5WVdsdWRISmxaVHBXWVhWc2RDSXNJa0p5WVdsdWRISmxaVHBEYkdsbGJuUlRSRXNpWFN3aWIzQjBhVzl1Y3lJNmV5SmpkWE4wYjIxbGNsOXBaQ0k2SWpRek1qRXdNalE0TlRNMkluMTkuNU5JeThsN0tEUGN2c08xa19GdXhtYndFbklISmlxS2ZTaGhKaUFrd25fMFk5Z0lRVXB4V3JUUE5HX1ExNkkyVmN4bEotYUFtMG9xenlhUjAtZ0NhZWc/Y3VzdG9tZXJfaWQ9IiwiY29uZmlnVXJsIjoiaHR0cHM6Ly9hcGkuc2FuZGJveC5icmFpbnRyZWVnYXRld2F5LmNvbTo0NDMvbWVyY2hhbnRzL3I4Z3Z4cXFmMjc4dzNjOTUvY2xpZW50X2FwaS92MS9jb25maWd1cmF0aW9uIiwiZ3JhcGhRTCI6eyJ1cmwiOiJodHRwczovL3BheW1lbnRzLnNhbmRib3guYnJhaW50cmVlLWFwaS5jb20vZ3JhcGhxbCIsImRhdGUiOiIyMDE4LTA1LTA4IiwiZmVhdHVyZXMiOlsidG9rZW5pemVfY3JlZGl0X2NhcmRzIl19LCJoYXNDdXN0b21lciI6dHJ1ZSwiY2xpZW50QXBpVXJsIjoiaHR0cHM6Ly9hcGkuc2FuZGJveC5icmFpbnRyZWVnYXRld2F5LmNvbTo0NDMvbWVyY2hhbnRzL3I4Z3Z4cXFmMjc4dzNjOTUvY2xpZW50X2FwaSIsImVudmlyb25tZW50Ijoic2FuZGJveCIsIm1lcmNoYW50SWQiOiJyOGd2eHFxZjI3OHczYzk1IiwiYXNzZXRzVXJsIjoiaHR0cHM6Ly9hc3NldHMuYnJhaW50cmVlZ2F0ZXdheS5jb20iLCJhdXRoVXJsIjoiaHR0cHM6Ly9hdXRoLnZlbm1vLnNhbmRib3guYnJhaW50cmVlZ2F0ZXdheS5jb20iLCJ2ZW5tbyI6Im9mZiIsImNoYWxsZW5nZXMiOlsiY3Z2IiwicG9zdGFsX2NvZGUiXSwidGhyZWVEU2VjdXJlRW5hYmxlZCI6ZmFsc2UsImFuYWx5dGljcyI6eyJ1cmwiOiJodHRwczovL29yaWdpbi1hbmFseXRpY3Mtc2FuZC5zYW5kYm94LmJyYWludHJlZS1hcGkuY29tL3I4Z3Z4cXFmMjc4dzNjOTUifSwicGF5cGFsRW5hYmxlZCI6ZmFsc2V9

*/

class BraintreeRepositoryImpl @Inject constructor(
    private val qtipCommandClient: QtipCommandClient,
    private val serverConfigDataStore: ServerConfigDataStore,
    private val loginSessionDataStore: LoginSessionDataStore,
    private val braintreeDataStore: BraintreeDataStore
) : BraintreeRepository {

    override suspend fun fetchAndPersistClientToken(): AppResult<String> {
        val userId = loginSessionDataStore.current.first()?.userId
        if (userId.isNullOrBlank()) {
            AppLogger.w(TAG, "No logged-in user, cannot fetch Braintree client token")
            return AppResult.Error("Not logged in")
        }

        val qtipRestBase = serverConfigDataStore.current.first()?.urlFor(QTIP_REST_ENDPOINT_KEY)
        if (qtipRestBase.isNullOrBlank()) {
            AppLogger.w(TAG, "No resolved $QTIP_REST_ENDPOINT_KEY endpoint available, cannot fetch Braintree client token")
            return AppResult.Error("QTIP REST endpoint is not available")
        }
        val baseUrl = "$qtipRestBase/QTIP_API/"

        return try {
            AppLogger.d(TAG, "Requesting Braintree customer id via $FETCH_CUSTOMER_ID_COMMAND")
            val customerIdData = listOf(userId, COMMUNITY_ID, NULL_PLACEHOLDER).joinToString(COLUMN_SEPARATOR.toString())
            val customerIdResponse = qtipCommandClient.sendCommand(baseUrl, FETCH_CUSTOMER_ID_COMMAND, customerIdData)
            val customerId = customerIdResponse.toBraintreeCustomerId()

            AppLogger.d(TAG, "Requesting Braintree client token via $FETCH_CLIENT_TOKEN_COMMAND")
            val clientTokenResponse = qtipCommandClient.sendCommand(
                baseUrl,
                FETCH_CLIENT_TOKEN_COMMAND,
                customerId ?: NULL_PLACEHOLDER
            )
            val clientToken = clientTokenResponse.toBraintreeClientToken()
            if (clientToken.isBlank()) {
                return AppResult.Error("Braintree did not return a client token")
            }

            braintreeDataStore.save(customerId, clientToken)
            AppResult.Success(clientToken)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            AppLogger.e(TAG, "Failed to fetch Braintree client token", e)
            AppResult.Error("Could not reach the server. Please try again.")
        }
    }

    override fun observeClientToken(): Flow<String?> = braintreeDataStore.clientToken

    private companion object {
        const val TAG = "Braintree"
        const val QTIP_REST_ENDPOINT_KEY = "QREST2_TestServer_IPPORT"
        const val FETCH_CUSTOMER_ID_COMMAND = "8BTC"
        const val FETCH_CLIENT_TOKEN_COMMAND = "8CTC"
        const val COMMUNITY_ID = "QRyde"
        const val NULL_PLACEHOLDER = "..."
        val COLUMN_SEPARATOR = 14.toChar()
    }
}
