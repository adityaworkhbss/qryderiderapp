package com.qryde.qryderiderapp.data.repository

import com.qryde.qryderiderapp.BuildConfig
import com.qryde.qryderiderapp.core.common.AppResult
import com.qryde.qryderiderapp.core.logging.AppLogger
import com.qryde.qryderiderapp.data.datastore.ServerConfigDataStore
import com.qryde.qryderiderapp.data.mapper.PasswordResetFailedException
import com.qryde.qryderiderapp.data.mapper.toPasswordResetMessage
import com.qryde.qryderiderapp.data.remote.rest.QtipCommandClient
import com.qryde.qryderiderapp.domain.repository.ForgotPasswordRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.first
import javax.inject.Inject


/*
*
* res : nt56561tolop99333@dwarkm.comhttps://stgq.qryde.net/MOBILEcom.QRyde.MarketplaceEN
* req : 5FP2~OKWe've sent a temporary password to your mobile number. Please use it to log in a update your password.
*
* */

class ForgotPasswordRepositoryImpl @Inject constructor(
    private val qtipCommandClient: QtipCommandClient,
    private val serverConfigDataStore: ServerConfigDataStore
) : ForgotPasswordRepository {

    override suspend fun requestPasswordReset(userId: String, email: String): AppResult<String> {
        val serverConfig = serverConfigDataStore.current.first()
        val qtipRestBase = serverConfig?.urlFor(QTIP_REST_ENDPOINT_KEY)
        if (qtipRestBase.isNullOrBlank()) {
            AppLogger.w(TAG, "No resolved $QTIP_REST_ENDPOINT_KEY endpoint available, cannot request password reset")
            return AppResult.Error("QTIP REST endpoint is not available")
        }
        val websiteBaseUrl = serverConfig.urlFor(WEBSITE_BASE_URL_KEY).orEmpty()

        return try {
            AppLogger.d(TAG, "Requesting password reset via $FORGOT_PASSWORD_COMMAND")
            val data = listOf(
                userId,
                USER_TYPE,
                email,
                "$websiteBaseUrl/",
                IDENTIFIER,
                BuildConfig.APPLICATION_ID,
                LANGUAGE_CODE
            ).joinToString(COLUMN_SEPARATOR.toString())
            val rawResponse = qtipCommandClient.sendCommand(
                baseUrl = "$qtipRestBase/QTIP_API/",
                command = FORGOT_PASSWORD_COMMAND,
                data = data
            )
            AppResult.Success(rawResponse.toPasswordResetMessage())
        } catch (e: PasswordResetFailedException) {
            AppResult.Error(e.message ?: "Could not process this request.")
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            AppLogger.e(TAG, "Password reset request failed", e)
            AppResult.Error("Could not reach the server. Please try again.")
        }
    }

    private companion object {
        const val TAG = "ForgotPassword"
        const val QTIP_REST_ENDPOINT_KEY = "QREST2_TestServer_IPPORT"
        const val WEBSITE_BASE_URL_KEY = "QTIP2_TestServer_BASEURL"
        const val FORGOT_PASSWORD_COMMAND = "5FP2"
        const val USER_TYPE = "1"
        const val IDENTIFIER = "MOBILE"
        const val LANGUAGE_CODE = "EN"
        val COLUMN_SEPARATOR = 14.toChar()
    }
}
