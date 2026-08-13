package com.qryde.qryderiderapp.data.repository

import com.qryde.qryderiderapp.BuildConfig
import com.qryde.qryderiderapp.core.common.AppResult
import com.qryde.qryderiderapp.core.logging.AppLogger
import com.qryde.qryderiderapp.data.datastore.ServerConfigDataStore
import com.qryde.qryderiderapp.data.mapper.SendVerificationCodeFailedException
import com.qryde.qryderiderapp.data.mapper.requireVerificationCodeSent
import com.qryde.qryderiderapp.data.remote.rest.QtipCommandClient
import com.qryde.qryderiderapp.domain.repository.SmsVerificationRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class SmsVerificationRepositoryImpl @Inject constructor(
    private val qtipCommandClient: QtipCommandClient,
    private val serverConfigDataStore: ServerConfigDataStore
) : SmsVerificationRepository {

    override suspend fun sendVerificationCode(
        userId: String,
        isoCode: String,
        phone: String,
        code: String
    ): AppResult<Unit> {
        val qtipRestBase = serverConfigDataStore.current.first()?.urlFor(QTIP_REST_ENDPOINT_KEY)
        if (qtipRestBase.isNullOrBlank()) {
            AppLogger.w(TAG, "No resolved $QTIP_REST_ENDPOINT_KEY endpoint available, cannot send verification code")
            return AppResult.Error("QTIP REST endpoint is not available")
        }

        return try {
            AppLogger.d(TAG, "Sending verification code via $SEND_CODE_COMMAND")
            // isoCode is unknown pre-signup too - this app is US-only for now.
            val isoNumber = "${isoCode.ifBlank { DEFAULT_ISO_CODE }}.$phone"
            val message = "Your QRyde Code is: $code"
            AppLogger.rest(message)
            // userId is blank pre-signup (no account exists yet) - the server accepts the
            // rc_null placeholder there, confirmed against real sign-up traffic.
            val data = listOf(isoNumber, userId.ifBlank { NULL_PLACEHOLDER }, message, NULL_PLACEHOLDER, BuildConfig.APPLICATION_ID, LANGUAGE_CODE)
                .joinToString(COLUMN_SEPARATOR.toString())
            val rawResponse = qtipCommandClient.sendCommand(
                baseUrl = "$qtipRestBase/QTIP_API/",
                command = SEND_CODE_COMMAND,
                data = data
            )
            rawResponse.requireVerificationCodeSent()
            AppResult.Success(Unit)
        } catch (e: SendVerificationCodeFailedException) {
            AppResult.Error(e.message ?: "Could not send verification code.")
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            AppLogger.e(TAG, "Send verification code request failed", e)
            AppResult.Error("Could not reach the server. Please try again.")
        }
    }

    private companion object {
        const val TAG = "SmsVerification"
        const val QTIP_REST_ENDPOINT_KEY = "QREST2_TestServer_IPPORT"
        const val SEND_CODE_COMMAND = "100UV"
        const val NULL_PLACEHOLDER = "..."
        const val LANGUAGE_CODE = "EN"
        const val DEFAULT_ISO_CODE = "US"
        val COLUMN_SEPARATOR = 14.toChar()
    }
}
