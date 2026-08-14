package com.qryde.qryderiderapp.data.repository

import android.content.Context
import com.qryde.qryderiderapp.core.common.AppResult
import com.qryde.qryderiderapp.core.logging.AppLogger
import com.qryde.qryderiderapp.data.datastore.ServerConfigDataStore
import com.qryde.qryderiderapp.data.mapper.DeviceRegistrationFailedException
import com.qryde.qryderiderapp.data.mapper.RegistrationCheckFailedException
import com.qryde.qryderiderapp.data.mapper.buildDeviceRegistrationData
import com.qryde.qryderiderapp.data.mapper.requireAvailable
import com.qryde.qryderiderapp.data.mapper.requireDeviceRegistered
import com.qryde.qryderiderapp.data.mapper.resolveDeviceId
import com.qryde.qryderiderapp.data.remote.rest.QtipCommandClient
import com.qryde.qryderiderapp.domain.model.NewAccountDetails
import com.qryde.qryderiderapp.domain.repository.RegistrationRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.first
import javax.inject.Inject


/*
*
* Email check command: 5E
* req : devtest@ry.com......EN
* res : 5E~OK
*
* User ID check command: 100ID
* req : nt5555...EN
* res : 100ID~OK
*
* Create account command: 100U
* req : fbf0b5fb82d4d209ex7xBYA9TvO6LvZRlka1Oq:APA91bE0VVrThqGxFn1Kk1gZMXgh2EV4RsI5YmsaidR-MZzWRpvNeWmfzgaEXGMmPGEtdDA5CMZKcfAO0k2psSzO8MuWmNukQJUYrcS00y_jIXPkoiOXveYAndroidUS.9793706222Dev|Test0......Hbss2004nt5555devtest@ry.com..................QRydecom.QRyde.MarketplaceEN
* res : 100U~OK^qryde2^v28^1555307^QRyde
*
* */

class RegistrationRepositoryImpl @Inject constructor(
    private val qtipCommandClient: QtipCommandClient,
    private val serverConfigDataStore: ServerConfigDataStore,
    @ApplicationContext private val context: Context
) : RegistrationRepository {

    override suspend fun checkUserIdAvailable(userId: String): AppResult<Unit> {
        val qtipRestBase = resolveQtipRestBase()
            ?: return AppResult.Error("QTIP REST endpoint is not available")

        return try {
            val data = listOf(userId, NULL_PLACEHOLDER, LANGUAGE_CODE).joinToString(COLUMN_SEPARATOR.toString())
            val rawResponse = qtipCommandClient.sendCommand(
                baseUrl = "$qtipRestBase/QTIP_API/",
                command = CHECK_USER_ID_COMMAND,
                data = data
            )
            rawResponse.requireAvailable()
            AppResult.Success(Unit)
        } catch (e: RegistrationCheckFailedException) {
            AppResult.Error(e.message ?: "This User ID is not available.")
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            AppLogger.e(TAG, "Check user id failed", e)
            AppResult.Error("Could not reach the server. Please try again.")
        }
    }

    override suspend fun checkEmailAvailable(email: String): AppResult<Unit> {
        val qtipRestBase = resolveQtipRestBase()
            ?: return AppResult.Error("QTIP REST endpoint is not available")

        return try {
            val data = listOf(email, NULL_PLACEHOLDER, NULL_PLACEHOLDER, LANGUAGE_CODE)
                .joinToString(COLUMN_SEPARATOR.toString())
            val rawResponse = qtipCommandClient.sendCommand(
                baseUrl = "$qtipRestBase/QTIP_API/",
                command = CHECK_EMAIL_COMMAND,
                data = data
            )
            rawResponse.requireAvailable()
            AppResult.Success(Unit)
        } catch (e: RegistrationCheckFailedException) {
            AppResult.Error(e.message ?: "This email is not available.")
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            AppLogger.e(TAG, "Check email failed", e)
            AppResult.Error("Could not reach the server. Please try again.")
        }
    }

    override suspend fun createAccount(details: NewAccountDetails): AppResult<Unit> {
        val qtipRestBase = resolveQtipRestBase()
            ?: return AppResult.Error("QTIP REST endpoint is not available")

        return try {
            AppLogger.d(TAG, "Creating account via $CREATE_ACCOUNT_COMMAND")
            val data = buildDeviceRegistrationData(
                deviceId = resolveDeviceId(context),
                userName = "${details.firstName}|${details.lastName}",
                isoCode = details.isoCode,
                phoneNumber = details.phoneNumber,
                password = details.password,
                userId = details.userId,
                email = details.email
            )
            val rawResponse = qtipCommandClient.sendCommand(
                baseUrl = "$qtipRestBase/QTIP_API/",
                command = CREATE_ACCOUNT_COMMAND,
                data = data
            )
            rawResponse.requireDeviceRegistered()
            AppResult.Success(Unit)
        } catch (e: DeviceRegistrationFailedException) {
            AppResult.Error(e.message ?: "Could not create your account.")
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            AppLogger.e(TAG, "Create account failed", e)
            AppResult.Error("Could not reach the server. Please try again.")
        }
    }

    private suspend fun resolveQtipRestBase(): String? =
        serverConfigDataStore.current.first()?.urlFor(QTIP_REST_ENDPOINT_KEY)

    private companion object {
        const val TAG = "Registration"
        const val QTIP_REST_ENDPOINT_KEY = "QREST2_TestServer_IPPORT"
        const val CHECK_USER_ID_COMMAND = "100ID"
        const val CHECK_EMAIL_COMMAND = "5E"
        const val CREATE_ACCOUNT_COMMAND = "100U"
        const val NULL_PLACEHOLDER = "..."
        const val LANGUAGE_CODE = "EN"
        val COLUMN_SEPARATOR = 14.toChar()
    }
}
