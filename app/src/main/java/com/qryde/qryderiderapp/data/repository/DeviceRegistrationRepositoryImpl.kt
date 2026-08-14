package com.qryde.qryderiderapp.data.repository

import android.content.Context
import com.qryde.qryderiderapp.core.common.AppResult
import com.qryde.qryderiderapp.core.logging.AppLogger
import com.qryde.qryderiderapp.data.datastore.LoginCredentialsDataStore
import com.qryde.qryderiderapp.data.datastore.LoginSessionDataStore
import com.qryde.qryderiderapp.data.datastore.ServerConfigDataStore
import com.qryde.qryderiderapp.data.mapper.buildDeviceRegistrationData
import com.qryde.qryderiderapp.data.mapper.requireDeviceRegistered
import com.qryde.qryderiderapp.data.mapper.resolveDeviceId
import com.qryde.qryderiderapp.data.remote.rest.QtipCommandClient
import com.qryde.qryderiderapp.domain.repository.DeviceRegistrationRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/*
*
* req : fbf0b5fb82d4d209...AndroidUS.8307544913Northend Test0......Hbss@2004nt5656tolop99333@dwarkm.com..................QRydecom.QRyde.MarketplaceEN
* res : 100U~OK^qryde2^v28^1516704^QRyde
*
* */

class DeviceRegistrationRepositoryImpl @Inject constructor(
    private val qtipCommandClient: QtipCommandClient,
    private val serverConfigDataStore: ServerConfigDataStore,
    private val loginSessionDataStore: LoginSessionDataStore,
    private val loginCredentialsDataStore: LoginCredentialsDataStore,
    @ApplicationContext private val context: Context
) : DeviceRegistrationRepository {

    override suspend fun registerDevice(): AppResult<Unit> {
        val qtipRestBase = serverConfigDataStore.current.first()?.urlFor(QTIP_REST_ENDPOINT_KEY)
        if (qtipRestBase.isNullOrBlank()) {
            AppLogger.w(TAG, "No resolved $QTIP_REST_ENDPOINT_KEY endpoint available, cannot register device")
            return AppResult.Error("QTIP REST endpoint is not available")
        }

        val session = loginSessionDataStore.current.first()
            ?: return AppResult.Error("No logged-in session to register.")
        val password = loginCredentialsDataStore.current.first()?.password
            ?: return AppResult.Error("No stored credentials to register.")

        val data = buildDeviceRegistrationData(
            deviceId = resolveDeviceId(context),
            userName = session.userName,
            isoCode = session.isoCode,
            phoneNumber = session.phoneNumber,
            password = password,
            userId = session.userId,
            email = session.email
        )

        repeat(MAX_ATTEMPTS) { attempt ->
            AppLogger.d(TAG, "Registering device via $REGISTER_DEVICE_COMMAND, attempt ${attempt + 1}/$MAX_ATTEMPTS")
            try {
                val rawResponse = qtipCommandClient.sendCommand(
                    baseUrl = "$qtipRestBase/QTIP_API/",
                    command = REGISTER_DEVICE_COMMAND,
                    data = data
                )
                rawResponse.requireDeviceRegistered()
                return AppResult.Success(Unit)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                AppLogger.w(TAG, "Attempt ${attempt + 1}/$MAX_ATTEMPTS failed", e)
                if (attempt == MAX_ATTEMPTS - 1) {
                    return AppResult.Error(e.message ?: "Could not register this device.")
                }
            }
            delay(RETRY_DELAY_MILLIS)
        }
        return AppResult.Error("Could not register this device.")
    }

    private companion object {
        const val TAG = "DeviceRegistration"
        const val QTIP_REST_ENDPOINT_KEY = "QREST2_TestServer_IPPORT"
        const val REGISTER_DEVICE_COMMAND = "100U"
        const val MAX_ATTEMPTS = 3
        const val RETRY_DELAY_MILLIS = 1_500L
    }
}
