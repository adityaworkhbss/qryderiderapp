package com.qryde.qryderiderapp.data.repository

import com.qryde.qryderiderapp.core.common.AppResult
import com.qryde.qryderiderapp.core.logging.AppLogger
import com.qryde.qryderiderapp.data.datastore.ClientDataStore
import com.qryde.qryderiderapp.data.datastore.CommunityDataStore
import com.qryde.qryderiderapp.data.datastore.LoginSessionDataStore
import com.qryde.qryderiderapp.data.datastore.OeRegistryDataStore
import com.qryde.qryderiderapp.data.datastore.PreferredCommunityDataStore
import com.qryde.qryderiderapp.data.datastore.ServerConfigDataStore
import com.qryde.qryderiderapp.data.mapper.communitySiteConfig
import com.qryde.qryderiderapp.data.mapper.toNemtClientInfo
import com.qryde.qryderiderapp.data.remote.rest.QtipCommandClient
import com.qryde.qryderiderapp.domain.model.NemtClientInfo
import com.qryde.qryderiderapp.domain.repository.ClientDataRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/*
  req 5CMD :: <userId>(char14)<preferredCommunityId>
  https://reststg.qryde.net:443/QTIP_API/5CMD :: nt5555qryde

  res 5CMD~<clientId>^<name>^<phone>^<dob>^<username>^<medicaid>^...^<regionId>^...^<portalId>^<clientType>^<gp>^<communityId>^<tenantPrefix>-<tenantId>
  5CMD~CLIENT_ID^CLIENT_NAME^PHONE^DOB^USERNAME^MEDICAID^CLIENT_TYPE^REGION_ID^...^PORTAL_ID

*/

class ClientDataRepositoryImpl @Inject constructor(
    private val qtipCommandClient: QtipCommandClient,
    private val serverConfigDataStore: ServerConfigDataStore,
    private val loginSessionDataStore: LoginSessionDataStore,
    private val communityDataStore: CommunityDataStore,
    private val oeRegistryDataStore: OeRegistryDataStore,
    private val clientDataStore: ClientDataStore,
    private val preferredCommunityDataStore: PreferredCommunityDataStore
) : ClientDataRepository {

    override suspend fun fetchClientDataIfEligible(): AppResult<NemtClientInfo?> {
        val userId = loginSessionDataStore.current.first()?.userId
        if (userId.isNullOrBlank()) {
            AppLogger.w(TAG, "No logged-in user, cannot fetch client data")
            return AppResult.Error("Not logged in")
        }

        val communities = communityDataStore.current.first()
        val preferredCommunityId = preferredCommunityDataStore.current.first()?.takeIf { it.isNotBlank() }
            ?: communities.firstOrNull { it.isPreferred }?.id
            ?: communities.firstOrNull()?.id
            ?: ""

        val siteConfig = oeRegistryDataStore.current.first()?.communitySiteConfig(preferredCommunityId)
        val clientType = siteConfig?.valueFor(CLIENT_TYPE_KEY, DEFAULT_CLIENT_TYPE) ?: DEFAULT_CLIENT_TYPE
        if (clientType.equals(PT1_CLIENT_TYPE, ignoreCase = true)) {
            AppLogger.d(TAG, "ClientType is PT1, skipping $CLIENT_DATA_COMMAND")
            clientDataStore.save(null)
            return AppResult.Success(null)
        }

        val qtipRestBase = serverConfigDataStore.current.first()?.urlFor(QTIP_REST_ENDPOINT_KEY)
        if (qtipRestBase.isNullOrBlank()) {
            AppLogger.w(TAG, "No resolved $QTIP_REST_ENDPOINT_KEY endpoint available, cannot fetch client data")
            return AppResult.Error("QTIP REST endpoint is not available")
        }

        return try {
            val data = listOf(userId, preferredCommunityId).joinToString(COLUMN_SEPARATOR.toString())
            val response = qtipCommandClient.sendCommand(
                baseUrl = "$qtipRestBase/QTIP_API/",
                command = CLIENT_DATA_COMMAND,
                data = data
            )
            val clientInfo = response.toNemtClientInfo()
            clientDataStore.save(clientInfo)
            AppResult.Success(clientInfo)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            AppLogger.e(TAG, "Failed to fetch client data", e)
            AppResult.Error("Could not reach the server. Please try again.")
        }
    }

    override fun observeClientData(): Flow<NemtClientInfo?> = clientDataStore.current

    private companion object {
        const val TAG = "ClientData"
        const val QTIP_REST_ENDPOINT_KEY = "QREST2_TestServer_IPPORT"
        const val CLIENT_DATA_COMMAND = "5CMD"
        const val CLIENT_TYPE_KEY = "ClientType"
        const val DEFAULT_CLIENT_TYPE = "NONPT1"
        const val PT1_CLIENT_TYPE = "PT1"
        val COLUMN_SEPARATOR = 14.toChar()
    }
}
