package com.qryde.qryderiderapp.data.repository

import com.qryde.qryderiderapp.core.common.AppResult
import com.qryde.qryderiderapp.core.logging.AppLogger
import com.qryde.qryderiderapp.data.datastore.LoginCredentialsDataStore
import com.qryde.qryderiderapp.data.datastore.LoginSessionDataStore
import com.qryde.qryderiderapp.data.datastore.ServerConfigDataStore
import com.qryde.qryderiderapp.data.mapper.LoginFailedException
import com.qryde.qryderiderapp.data.mapper.toLoginSession
import com.qryde.qryderiderapp.data.remote.rest.QtipCommandClient
import com.qryde.qryderiderapp.domain.model.LoginCredentials
import com.qryde.qryderiderapp.domain.model.LoginSession
import com.qryde.qryderiderapp.domain.repository.AuthRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val qtipCommandClient: QtipCommandClient,
    private val loginCredentialsDataStore: LoginCredentialsDataStore,
    private val loginSessionDataStore: LoginSessionDataStore,
    private val serverConfigDataStore: ServerConfigDataStore
) : AuthRepository {

    override suspend fun hasStoredCredentials(): Boolean =
        loginCredentialsDataStore.current.first() != null

    override suspend fun login(userId: String, password: String): AppResult<LoginSession> {
        val result = performLogin(userId, password)
        if (result is AppResult.Success) {
            loginCredentialsDataStore.save(LoginCredentials(userId, password))
        }
        return result
    }

    override suspend fun silentLogin(): AppResult<LoginSession> {
        val credentials = loginCredentialsDataStore.current.first()
            ?: return AppResult.Error("No stored credentials.")
        return performLogin(credentials.userId, credentials.password)
    }

    private suspend fun performLogin(userId: String, password: String): AppResult<LoginSession> {
        val qtipRestBase = serverConfigDataStore.current.first()?.urlFor(QTIP_REST_ENDPOINT_KEY)
        if (qtipRestBase.isNullOrBlank()) {
            AppLogger.w(TAG, "No resolved $QTIP_REST_ENDPOINT_KEY endpoint available, cannot log in")
            return AppResult.Error("QTIP REST endpoint is not available")
        }

        return try {
            AppLogger.d(TAG, "Logging in via $LOGIN_COMMAND")
            val data = listOf(userId, password, NULL_PLACEHOLDER, NULL_PLACEHOLDER, LANGUAGE_CODE)
                .joinToString(COLUMN_SEPARATOR.toString())
            val rawResponse = qtipCommandClient.sendCommand(
                baseUrl = "$qtipRestBase/QTIP_API/",
                command = LOGIN_COMMAND,
                data = data
            )
            val session = rawResponse.toLoginSession()
            loginSessionDataStore.save(session)
            AppResult.Success(session)
        } catch (e: LoginFailedException) {
            AppResult.Error(e.message ?: "Login failed.")
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            AppLogger.e(TAG, "Login request failed", e)
            AppResult.Error("Could not reach the server. Please try again.")
        }
    }

    private companion object {
        const val TAG = "Auth"
        const val QTIP_REST_ENDPOINT_KEY = "QREST2_TestServer_IPPORT"
        const val LOGIN_COMMAND = "5G"
        const val NULL_PLACEHOLDER = "..."
        const val LANGUAGE_CODE = "EN"
        val COLUMN_SEPARATOR = 14.toChar()
    }
}
