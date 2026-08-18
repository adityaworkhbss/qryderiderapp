package com.qryde.qryderiderapp.data.repository

import com.qryde.qryderiderapp.core.common.AppResult
import com.qryde.qryderiderapp.core.logging.AppLogger
import com.qryde.qryderiderapp.data.datastore.DeviceRegistrationDataStore
import com.qryde.qryderiderapp.data.datastore.LoginSessionDataStore
import com.qryde.qryderiderapp.data.datastore.ServerConfigDataStore
import com.qryde.qryderiderapp.data.mapper.toEchoedUserFsId
import com.qryde.qryderiderapp.data.mapper.toTransactions
import com.qryde.qryderiderapp.data.remote.rest.QtipCommandClient
import com.qryde.qryderiderapp.domain.model.DeviceRegistrationInfo
import com.qryde.qryderiderapp.domain.model.Transaction
import com.qryde.qryderiderapp.domain.repository.TransactionsRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/*
* req 25RT :: <userId>(char14)"QRyde"(char14)<languageCode>
              testaastha1QRydeEN
*
* res 25RT~<echoedFsId>(char15)<record>(char15)<record>...
*   or 25RT~<echoedFsId>(char15)NO_DATA_FOUND
*
* 25RT~1555138NO_DATA_FOUND
	 25RT~15551382026-08-18 14:30:00TXN10001C1035707Trip Payment25.5025.502026-08-17 10:15:00TXN10002C1054321Fare Card Purchase50.0050.00

*/

class TransactionsRepositoryImpl @Inject constructor(
    private val qtipCommandClient: QtipCommandClient,
    private val serverConfigDataStore: ServerConfigDataStore,
    private val loginSessionDataStore: LoginSessionDataStore,
    private val deviceRegistrationDataStore: DeviceRegistrationDataStore
) : TransactionsRepository {

    override suspend fun fetchTransactions(): AppResult<List<Transaction>> {
        val userId = loginSessionDataStore.current.first()?.userId
        if (userId.isNullOrBlank()) {
            AppLogger.w(TAG, "No logged-in user, cannot fetch transactions")
            return AppResult.Error("Not logged in")
        }

        val qtipRestBase = serverConfigDataStore.current.first()?.urlFor(QTIP_REST_ENDPOINT_KEY)
        if (qtipRestBase.isNullOrBlank()) {
            AppLogger.w(TAG, "No resolved $QTIP_REST_ENDPOINT_KEY endpoint available, cannot fetch transactions")
            return AppResult.Error("QTIP REST endpoint is not available")
        }

        return try {
            val data = listOf(userId, REQUEST_TYPE, LANGUAGE_CODE).joinToString(COLUMN_SEPARATOR.toString())
            val response = qtipCommandClient.sendCommand(
                baseUrl = "$qtipRestBase/QTIP_API/",
                command = TRANSACTIONS_COMMAND,
                data = data
            )
            response.toEchoedUserFsId()?.let { deviceRegistrationDataStore.save(DeviceRegistrationInfo(userFsId = it)) }
            AppResult.Success(response.toTransactions())
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            AppLogger.e(TAG, "Failed to fetch transactions", e)
            AppResult.Error("Could not reach the server. Please try again.")
        }
    }

    private companion object {
        const val TAG = "Transactions"
        const val QTIP_REST_ENDPOINT_KEY = "QREST2_TestServer_IPPORT"
        const val TRANSACTIONS_COMMAND = "25RT"
        const val REQUEST_TYPE = "QRyde"
        const val LANGUAGE_CODE = "EN"
        val COLUMN_SEPARATOR = 14.toChar()
    }
}
