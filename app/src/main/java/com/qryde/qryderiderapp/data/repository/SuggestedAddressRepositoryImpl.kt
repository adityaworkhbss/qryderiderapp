package com.qryde.qryderiderapp.data.repository

import com.qryde.qryderiderapp.core.common.AppResult
import com.qryde.qryderiderapp.core.logging.AppLogger
import com.qryde.qryderiderapp.data.datastore.CommunityDataStore
import com.qryde.qryderiderapp.data.datastore.LoginSessionDataStore
import com.qryde.qryderiderapp.data.datastore.ServerConfigDataStore
import com.qryde.qryderiderapp.data.mapper.toSuggestedAddresses
import com.qryde.qryderiderapp.data.remote.rest.QtipCommandClient
import com.qryde.qryderiderapp.domain.model.SuggestedAddresses
import com.qryde.qryderiderapp.domain.repository.SuggestedAddressRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/*
* req 5FAT :: <userId>(char14)<communityId>
      https://reststg.qryde.net:443/QTIP_API/5FAT  data ::  nt5555qryde

* res 5FAT~<recentAddresses>(char12)<savedTripAddresses>
      5FAT~123 Main StreetNew York10001NYUS40.7128-74.0060456 Oak AvenueBrooklyn11201NYUS40.6782-73.9442OP001EST001SUP001COMM0015.21525.50BUSWork1None123 Main Street123 Main StreetNew York10001NYUS40.7128-74.0060456 Oak AvenueBrooklyn11201NYUS40.6782-73.9442TRIP001OP002EST002SUP002COMM0028.72542.75TRAINHome2Child passenger789 Park Avenue789 Park AvenueNew York10018NYUS40.7549-73.9840100 BroadwayNew York10005NYUS40.7060-74.0086TRIP002
*/

class SuggestedAddressRepositoryImpl @Inject constructor(
    private val qtipCommandClient: QtipCommandClient,
    private val serverConfigDataStore: ServerConfigDataStore,
    private val loginSessionDataStore: LoginSessionDataStore,
    private val communityDataStore: CommunityDataStore
) : SuggestedAddressRepository {

    override suspend fun fetchSuggestedAddresses(): AppResult<SuggestedAddresses> {
        val userId = loginSessionDataStore.current.first()?.userId
        if (userId.isNullOrBlank()) {
            AppLogger.w(TAG, "No logged-in user, cannot fetch suggested addresses")
            return AppResult.Error("Not logged in")
        }

        val qtipRestBase = serverConfigDataStore.current.first()?.urlFor(QTIP_REST_ENDPOINT_KEY)
        if (qtipRestBase.isNullOrBlank()) {
            AppLogger.w(TAG, "No resolved $QTIP_REST_ENDPOINT_KEY endpoint available, cannot fetch suggested addresses")
            return AppResult.Error("QTIP REST endpoint is not available")
        }

        val communities = communityDataStore.current.first()
        val communityId = communities.firstOrNull { it.isPreferred }?.id
            ?: communities.firstOrNull()?.id
            ?: ""

        return try {
            val data = listOf(userId, communityId).joinToString(COLUMN_SEPARATOR.toString())
            val response = qtipCommandClient.sendCommand(
                baseUrl = "$qtipRestBase/QTIP_API/",
                command = SUGGESTED_ADDRESSES_COMMAND,
                data = data
            )
            AppResult.Success(response.toSuggestedAddresses())
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            AppLogger.e(TAG, "Failed to fetch suggested addresses", e)
            AppResult.Error("Could not reach the server. Please try again.")
        }
    }

    private companion object {
        const val TAG = "SuggestedAddress"
        const val QTIP_REST_ENDPOINT_KEY = "QREST2_TestServer_IPPORT"
        const val SUGGESTED_ADDRESSES_COMMAND = "5FAT"
        val COLUMN_SEPARATOR = 14.toChar()
    }
}
