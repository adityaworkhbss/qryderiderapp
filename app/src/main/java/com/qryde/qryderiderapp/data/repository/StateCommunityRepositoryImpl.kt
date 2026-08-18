package com.qryde.qryderiderapp.data.repository

import com.qryde.qryderiderapp.core.common.AppResult
import com.qryde.qryderiderapp.core.logging.AppLogger
import com.qryde.qryderiderapp.data.datastore.LoginSessionDataStore
import com.qryde.qryderiderapp.data.datastore.ServerConfigDataStore
import com.qryde.qryderiderapp.data.mapper.toStateCommunities
import com.qryde.qryderiderapp.data.remote.rest.QtipCommandClient
import com.qryde.qryderiderapp.domain.model.StateCommunity
import com.qryde.qryderiderapp.domain.repository.StateCommunityRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/*
* req 20SC :: <stateCode>(char14)<userId>
* res 20SC~<community>(char15)<community>...
*/

class StateCommunityRepositoryImpl @Inject constructor(
    private val qtipCommandClient: QtipCommandClient,
    private val serverConfigDataStore: ServerConfigDataStore,
    private val loginSessionDataStore: LoginSessionDataStore
) : StateCommunityRepository {

    override suspend fun fetchCommunitiesForState(stateCode: String): AppResult<List<StateCommunity>> {
        val userId = loginSessionDataStore.current.first()?.userId
        if (userId.isNullOrBlank()) {
            AppLogger.w(TAG, "No logged-in user, cannot fetch state communities")
            return AppResult.Error("Not logged in")
        }

        val qtipRestBase = serverConfigDataStore.current.first()?.urlFor(QTIP_REST_ENDPOINT_KEY)
        if (qtipRestBase.isNullOrBlank()) {
            AppLogger.w(TAG, "No resolved $QTIP_REST_ENDPOINT_KEY endpoint available, cannot fetch state communities")
            return AppResult.Error("QTIP REST endpoint is not available")
        }

        return try {
            val data = listOf(stateCode, userId).joinToString(COLUMN_SEPARATOR.toString())
            val response = qtipCommandClient.sendCommand(
                baseUrl = "$qtipRestBase/QTIP_API/",
                command = STATE_COMMUNITY_COMMAND,
                data = data
            )
            AppResult.Success(response.toStateCommunities())
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            AppLogger.e(TAG, "Failed to fetch state communities", e)
            AppResult.Error("Could not reach the server. Please try again.")
        }
    }

    private companion object {
        const val TAG = "StateCommunity"
        const val QTIP_REST_ENDPOINT_KEY = "QREST2_TestServer_IPPORT"
        const val STATE_COMMUNITY_COMMAND = "20SC"
        val COLUMN_SEPARATOR = 14.toChar()
    }
}
