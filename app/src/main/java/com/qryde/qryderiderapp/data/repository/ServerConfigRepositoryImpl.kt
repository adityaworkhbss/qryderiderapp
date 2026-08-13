package com.qryde.qryderiderapp.data.repository

import com.qryde.qryderiderapp.core.common.AppResult
import com.qryde.qryderiderapp.core.logging.AppLogger
import com.qryde.qryderiderapp.core.utils.AppConfig
import com.qryde.qryderiderapp.core.utils.Environment
import com.qryde.qryderiderapp.data.datastore.ServerConfigDataStore
import com.qryde.qryderiderapp.data.mapper.toServerConfig
import com.qryde.qryderiderapp.data.remote.socket.ServerConfigSocketClient
import com.qryde.qryderiderapp.domain.model.ServerConfig
import com.qryde.qryderiderapp.domain.repository.ServerConfigRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import javax.inject.Inject

class ServerConfigRepositoryImpl @Inject constructor(
    private val socketClient: ServerConfigSocketClient,
    private val serverConfigDataStore: ServerConfigDataStore,
    private val appConfig: AppConfig
) : ServerConfigRepository {

    override suspend fun resolveServerConfig(): AppResult<ServerConfig> {
        val message = environmentMessageCode(appConfig.environment)

        repeat(MAX_ATTEMPTS) { attempt ->
            AppLogger.d(TAG, "Resolving server config, attempt ${attempt + 1}/$MAX_ATTEMPTS, message=$message")
            try {
                val rawResponse = socketClient.fetchConfig(appConfig.discoveryWebSocketUrl, message)
                val config = rawResponse.toServerConfig()
                serverConfigDataStore.save(config)
                AppLogger.i(TAG, "Server config resolved: ${config.endpoints.keys}")
                return AppResult.Success(config)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                AppLogger.w(TAG, "Attempt ${attempt + 1}/$MAX_ATTEMPTS failed", e)
                if (attempt == MAX_ATTEMPTS - 1) {
                    AppLogger.e(TAG, "Giving up after $MAX_ATTEMPTS attempts", e)
                    return AppResult.Error("Could not reach the config server. Please check your connection and try again.")
                }
                delay(RETRY_DELAY_MILLIS)
            }
        }
        return AppResult.Error("Could not reach the config server.")
    }

    private fun environmentMessageCode(environment: Environment): String = when (environment) {
        Environment.DEV -> "QRALL2_B~Android"
        Environment.STAGING -> "QRALL2_T~Android"
        Environment.PROD -> "QRALL2_L~Android"
    }

    private companion object {
        const val TAG = "ServerConfig"
        const val MAX_ATTEMPTS = 3
        const val RETRY_DELAY_MILLIS = 1_500L
    }
}
