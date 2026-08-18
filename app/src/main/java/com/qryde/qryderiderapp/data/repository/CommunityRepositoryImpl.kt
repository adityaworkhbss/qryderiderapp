package com.qryde.qryderiderapp.data.repository

import com.qryde.qryderiderapp.core.common.AppResult
import com.qryde.qryderiderapp.core.logging.AppLogger
import com.qryde.qryderiderapp.data.datastore.CommunityDataStore
import com.qryde.qryderiderapp.data.datastore.PreferredCommunityDataStore
import com.qryde.qryderiderapp.data.datastore.ServerConfigDataStore
import com.qryde.qryderiderapp.data.mapper.toCommunities
import com.qryde.qryderiderapp.data.remote.rest.QtipCommandClient
import com.qryde.qryderiderapp.domain.model.Community
import com.qryde.qryderiderapp.domain.repository.CommunityRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.Json
import javax.inject.Inject


/*
*
* req : nt5656
* res : 20AUC~{"CommunityInfo":[{"CommunityId":"AlPoTransit","CommunityName":"All Points Transit","pref_comm":"N","result_combo":"L,D,R","address":"175 Merchant Dr, Montrose, CO 81401, USA","homeurl":"https://www.allpointstransit.com/","comm_access":"Public","request_type":"CA","Phone":"9999999999","email":"hjpprgteh@midiharmonica.com","logo":"","description":"","latitude":"38.491066","longitude":"-107.894071"},{"CommunityId":"BRATTLEBOROCOMM","CommunityName":"BRATTLEBOROCOMM","pref_comm":"N","result_combo":"D,R","address":"706 Rockingham Rd, Bellows Falls, VT 05101, USA","homeurl":"stgq.qryde.net","comm_access":"Public","request_type":"CA","Phone":"9999999999","email":"hokaga1813@glaslack.com","logo":"","description":"","latitude":"43.171006","longitude":"-72.459037"},{"CommunityId":"GADABOUTCOMM","CommunityName":"GADABOUT Transportation","pref_comm":"N","result_combo":"L,D,R","address":"Gadabout Transportation Services, 737 Willow Ave, Ithaca, NY 14850, USA","homeurl":"https://gadaboutbus.org/","comm_access":"Public","request_type":"CA","Phone":"6072731878","email":"jyxegodo@cyclelove.cc","logo":"","description":"...","latitude":"42.453232","longitude":"-76.505953"},{"CommunityId":"MICROMOOCOMM","CommunityName":"MicroMOO ","pref_comm":"N","result_combo":"D,R","address":"706 Rockingham Rd, Bellows Falls, VT 05101, USA","homeurl":"www.moover.com","comm_access":"Public","request_type":"CA","Phone":"8888696287","email":"beheke7769@razuz.com","logo":"data:image/png;base64,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
*
* */

class CommunityRepositoryImpl @Inject constructor(
    private val qtipCommandClient: QtipCommandClient,
    private val serverConfigDataStore: ServerConfigDataStore,
    private val communityDataStore: CommunityDataStore,
    private val preferredCommunityDataStore: PreferredCommunityDataStore,
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
            resolvePreferredCommunity(communities)
            AppLogger.i(TAG, "Resolved ${communities.size} joined communities")
            AppResult.Success(communities)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            AppLogger.e(TAG, "Failed to fetch joined communities", e)
            AppResult.Error("Could not fetch community data")
        }
    }

    /**
     * Mirrors the legacy client's processCommunityData(): a community the
     * backend already flags pref_comm="Y", or the sole joined community, is
     * the rider's preferred one - no need to make them pick it again via the
     * 20SC flow. Doesn't override an id the rider already chose explicitly.
     */
    private suspend fun resolvePreferredCommunity(communities: List<Community>) {
        if (!preferredCommunityDataStore.current.first().isNullOrBlank()) return

        val resolvedId = communities.firstOrNull { it.isPreferred }?.id
            ?: communities.singleOrNull()?.id
            ?: return
        preferredCommunityDataStore.save(resolvedId)
    }

    private companion object {
        const val TAG = "Community"
        const val QTIP_REST_ENDPOINT_KEY = "QREST2_TestServer_IPPORT"
        const val COMMUNITY_COMMAND = "20AUC"
    }
}
