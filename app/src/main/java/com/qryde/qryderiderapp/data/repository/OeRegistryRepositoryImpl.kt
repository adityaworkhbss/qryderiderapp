package com.qryde.qryderiderapp.data.repository

import com.qryde.qryderiderapp.core.common.AppResult
import com.qryde.qryderiderapp.core.logging.AppLogger
import com.qryde.qryderiderapp.data.datastore.OeRegistryDataStore
import com.qryde.qryderiderapp.data.mapper.toOeRegistryValues
import com.qryde.qryderiderapp.data.remote.rest.QtipCommandClient
import com.qryde.qryderiderapp.domain.model.OeRegistryValues
import com.qryde.qryderiderapp.domain.model.ServerConfig
import com.qryde.qryderiderapp.domain.repository.OeRegistryRepository
import kotlinx.coroutines.CancellationException
import javax.inject.Inject

class OeRegistryRepositoryImpl @Inject constructor(
    private val qtipCommandClient: QtipCommandClient,
    private val oeRegistryDataStore: OeRegistryDataStore
) : OeRegistryRepository {

    override suspend fun fetchOeRegistryValues(serverConfig: ServerConfig): AppResult<OeRegistryValues> {
        val qtipRestBase = serverConfig.urlFor(QTIP_REST_ENDPOINT_KEY) // Hardcoded key for QTIP REST endpoint in server config

        // make sure we have a valid QTIP REST endpoint before proceeding



        if (qtipRestBase.isNullOrBlank()) {
            AppLogger.w(TAG, "Resolved server config has no $QTIP_REST_ENDPOINT_KEY entry, skipping OE registry fetch")
            return AppResult.Error("QTIP REST endpoint is not available")
        }

        return try {
            AppLogger.d(TAG, "Fetching OE registry values via $OE_REGISTRY_COMMAND")
            val rawResponse = qtipCommandClient.sendCommand(
                baseUrl = "$qtipRestBase/QTIP_API/",
                command = OE_REGISTRY_COMMAND,
                data = OE_REGISTRY_MESSAGE
            )
            val values = rawResponse.toOeRegistryValues()
            oeRegistryDataStore.save(values)
            AppLogger.i(TAG, "OE registry resolved ${values.values.size} entries")
            AppResult.Success(values)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            AppLogger.e(TAG, "Failed to fetch OE registry values", e)
            AppResult.Error("Could not fetch OE registry values")
        }
    }

    private companion object {
        const val TAG = "OeRegistry"
        const val QTIP_REST_ENDPOINT_KEY = "QREST2_TestServer_IPPORT"
        const val OE_REGISTRY_COMMAND = "17CV"
        const val OE_REGISTRY_MESSAGE = "QRyde"
    }
}
