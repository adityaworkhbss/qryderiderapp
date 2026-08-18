package com.qryde.qryderiderapp.data.repository

import android.content.Context
import com.qryde.qryderiderapp.core.common.AppResult
import com.qryde.qryderiderapp.core.logging.AppLogger
import com.qryde.qryderiderapp.data.datastore.CommunityDataStore
import com.qryde.qryderiderapp.data.datastore.OeRegistryDataStore
import com.qryde.qryderiderapp.data.datastore.PreferredCommunityDataStore
import com.qryde.qryderiderapp.data.mapper.communitySiteConfig
import com.qryde.qryderiderapp.data.mapper.resolveDeviceId
import com.qryde.qryderiderapp.data.mapper.rlAddressSearchConfig
import com.qryde.qryderiderapp.data.mapper.toAddressSuggestions
import com.qryde.qryderiderapp.data.remote.rest.TypeAheadAddressApiClient
import com.qryde.qryderiderapp.domain.model.AddressSuggestion
import com.qryde.qryderiderapp.domain.repository.AddressSearchRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.Json
import javax.inject.Inject

/*
* Per-community "FetchAddressesFromRL" site config ("Y"^<baseUrl>^<token>)
* gates a backend type-ahead address search - deliberately not Google Places,
* consistent with this app avoiding Play Services dependencies elsewhere.
*
* req  : POST <baseUrl> {"header":{"authorization":"bearer <token>","deviceID":
*        "<id>","origin":"MOBILE_APP","commandId":"TYPE_AHEAD_ADDRESS"},
*        "data":{"addressInputInfo":"<query>"},"commandId":"TYPE_AHEAD_ADDRESS"}
* res  : {"data":{"addressData":[{ALIAS,ADDRESS1,ADDRESS2,CITYTOWN,STATEPRO,
*        ZIP,GRIDLAT,GRIDLONG,ADDR_LOC}, ...]}}
*/

class AddressSearchRepositoryImpl @Inject constructor(
    private val typeAheadAddressApiClient: TypeAheadAddressApiClient,
    private val communityDataStore: CommunityDataStore,
    private val preferredCommunityDataStore: PreferredCommunityDataStore,
    private val oeRegistryDataStore: OeRegistryDataStore,
    private val json: Json,
    @ApplicationContext private val context: Context
) : AddressSearchRepository {

    override suspend fun searchAddresses(query: String): AppResult<List<AddressSuggestion>> {
        val communities = communityDataStore.current.first()
        val preferredCommunityId = preferredCommunityDataStore.current.first()?.takeIf { it.isNotBlank() }
            ?: communities.firstOrNull { it.isPreferred }?.id
            ?: communities.firstOrNull()?.id
            ?: ""

        val oeRegistryValues = oeRegistryDataStore.current.first()
        val siteConfig = oeRegistryValues?.communitySiteConfig(preferredCommunityId)
        val rlConfig = siteConfig?.rlAddressSearchConfig()
        if (rlConfig == null) {
            AppLogger.w(
                TAG,
                "Type-ahead address search not enabled - communityId='$preferredCommunityId', " +
                    "hasOeRegistryValues=${oeRegistryValues != null}, " +
                    "FetchAddressesFromRL='${siteConfig?.valueFor("FetchAddressesFromRL")}'"
            )
            return AppResult.Error("Type-ahead address search is not enabled for this community")
        }

        return try {
            val response = typeAheadAddressApiClient.searchAddresses(
                baseUrl = rlConfig.baseUrl,
                token = rlConfig.token,
                deviceId = resolveDeviceId(context),
                query = query
            )
            AppResult.Success(response.toAddressSuggestions(json))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            AppLogger.e(TAG, "Failed to search addresses", e)
            AppResult.Error("Could not reach the server. Please try again.")
        }
    }

    private companion object {
        const val TAG = "AddressSearch"
    }
}
