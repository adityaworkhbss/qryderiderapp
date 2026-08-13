package com.qryde.qryderiderapp.data.repository

import com.qryde.qryderiderapp.core.common.AppResult
import com.qryde.qryderiderapp.core.logging.AppLogger
import com.qryde.qryderiderapp.data.datastore.CommunityDataStore
import com.qryde.qryderiderapp.data.datastore.ServerConfigDataStore
import com.qryde.qryderiderapp.data.mapper.toCommunities
import com.qryde.qryderiderapp.data.remote.rest.QtipCommandClient
import com.qryde.qryderiderapp.domain.model.Community
import com.qryde.qryderiderapp.domain.repository.CommunityRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.Json
import javax.inject.Inject

class CommunityRepositoryImpl @Inject constructor(
    private val qtipCommandClient: QtipCommandClient,
    private val serverConfigDataStore: ServerConfigDataStore,
    private val communityDataStore: CommunityDataStore,
    private val json: Json
) : CommunityRepository {

    override suspend fun fetchJoinedCommunities(userId: String): AppResult<List<Community>> {
        val qtipRestBase = serverConfigDataStore.current.first()?.urlFor(QTIP_REST_ENDPOINT_KEY)
        if (qtipRestBase.isNullOrBlank()) {
            AppLogger.w(TAG, "No resolved $QTIP_REST_ENDPOINT_KEY endpoint available, cannot fetch communities")
            return AppResult.Error("QTIP REST endpoint is not available")
        }

        return try {
            AppLogger.d(TAG, "Fetching joined communities via $COMMUNITY_COMMAND")
            val rawResponse = qtipCommandClient.sendCommand(
                baseUrl = "$qtipRestBase/QTIP_API/",
                command = COMMUNITY_COMMAND,
                data = userId
            )
            val communities = rawResponse.toCommunities(json)
            communityDataStore.save(communities)
            AppLogger.i(TAG, "Resolved ${communities.size} joined communities")
            AppResult.Success(communities)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            AppLogger.e(TAG, "Failed to fetch joined communities", e)
            AppResult.Error("Could not fetch community data")
        }
    }

    private companion object {
        const val TAG = "Community"
        const val QTIP_REST_ENDPOINT_KEY = "QREST2_TestServer_IPPORT"
        const val COMMUNITY_COMMAND = "20AUC"
    }
}
