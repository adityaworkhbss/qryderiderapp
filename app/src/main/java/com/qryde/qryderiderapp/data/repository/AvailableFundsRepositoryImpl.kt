package com.qryde.qryderiderapp.data.repository

import com.qryde.qryderiderapp.core.common.AppResult
import com.qryde.qryderiderapp.core.logging.AppLogger
import com.qryde.qryderiderapp.data.datastore.DeviceRegistrationDataStore
import com.qryde.qryderiderapp.data.datastore.LoginSessionDataStore
import com.qryde.qryderiderapp.data.datastore.ServerConfigDataStore
import com.qryde.qryderiderapp.data.mapper.toAvailableFundsAmount
import com.qryde.qryderiderapp.data.remote.rest.QtipCommandClient
import com.qryde.qryderiderapp.domain.repository.AvailableFundsRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/*
* req 8FA :: <userId>(char14)"QRyde"(char14)<userFsId>
* res 8FA~<amount>
*/

class AvailableFundsRepositoryImpl @Inject constructor(
    private val qtipCommandClient: QtipCommandClient,
    private val serverConfigDataStore: ServerConfigDataStore,
    private val loginSessionDataStore: LoginSessionDataStore,
    private val deviceRegistrationDataStore: DeviceRegistrationDataStore
) : AvailableFundsRepository {

    override suspend fun fetchAvailableFunds(): AppResult<Double> {
        val userId = loginSessionDataStore.current.first()?.userId
        if (userId.isNullOrBlank()) {
            AppLogger.w(TAG, "No logged-in user, cannot fetch available funds")
            return AppResult.Error("Not logged in")
        }

        val userFsId = deviceRegistrationDataStore.current.first()?.userFsId
        if (userFsId.isNullOrBlank()) {
            AppLogger.w(TAG, "No user funding source id cached yet, cannot fetch available funds")
            return AppResult.Error("Funding source not available yet")
        }

        val qtipRestBase = serverConfigDataStore.current.first()?.urlFor(QTIP_REST_ENDPOINT_KEY)
        if (qtipRestBase.isNullOrBlank()) {
            AppLogger.w(TAG, "No resolved $QTIP_REST_ENDPOINT_KEY endpoint available, cannot fetch available funds")
            return AppResult.Error("QTIP REST endpoint is not available")
        }

        return try {
            val data = listOf(userId, REQUEST_TYPE, userFsId).joinToString(COLUMN_SEPARATOR.toString())
            val response = qtipCommandClient.sendCommand(
                baseUrl = "$qtipRestBase/QTIP_API/",
                command = AVAILABLE_FUNDS_COMMAND,
                data = data
            )
            AppResult.Success(response.toAvailableFundsAmount())
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            AppLogger.e(TAG, "Failed to fetch available funds", e)
            AppResult.Error("Could not reach the server. Please try again.")
        }
    }

    private companion object {
        const val TAG = "AvailableFunds"
        const val QTIP_REST_ENDPOINT_KEY = "QREST2_TestServer_IPPORT"
        const val AVAILABLE_FUNDS_COMMAND = "8FA"
        const val REQUEST_TYPE = "QRyde"
        val COLUMN_SEPARATOR = 14.toChar()
    }
}
