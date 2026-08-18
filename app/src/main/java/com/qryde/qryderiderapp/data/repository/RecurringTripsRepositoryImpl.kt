package com.qryde.qryderiderapp.data.repository

import com.qryde.qryderiderapp.core.common.AppResult
import com.qryde.qryderiderapp.core.logging.AppLogger
import com.qryde.qryderiderapp.data.datastore.ClientDataStore
import com.qryde.qryderiderapp.data.datastore.CommunityDataStore
import com.qryde.qryderiderapp.data.datastore.OeRegistryDataStore
import com.qryde.qryderiderapp.data.datastore.PreferredCommunityDataStore
import com.qryde.qryderiderapp.data.datastore.RecurringTripsDataStore
import com.qryde.qryderiderapp.data.datastore.ServerConfigDataStore
import com.qryde.qryderiderapp.data.datastore.LoginSessionDataStore
import com.qryde.qryderiderapp.data.mapper.communitySiteConfig
import com.qryde.qryderiderapp.data.mapper.toRecurringTripsFrom7GM
import com.qryde.qryderiderapp.data.mapper.toRecurringTripsFromTenantApi
import com.qryde.qryderiderapp.data.remote.rest.NemtTenantApiClient
import com.qryde.qryderiderapp.data.remote.rest.QtipCommandClient
import com.qryde.qryderiderapp.domain.model.CommunitySiteConfig
import com.qryde.qryderiderapp.domain.model.RecurringTrip
import com.qryde.qryderiderapp.domain.repository.RecurringTripsRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/*
* req 7GM :: <userId>(char14)"QR"(char14)"ALL"
* res 7GM~<record>(char15)<record>...  or  7GM~NO_DATA_FOUND
*
* NEMT tenant endpoint (PT1 / direct-booking communities only):
* req  : GET/POST <TenantUrl>NEMT?cmd=getsoinfobymmis&userid=hbss-cc&tenantid=<TenantId>&nemtparam=<medicaid>^<clientType>
* res  : <record>~<record>...  or  NOK^<message>
*/

class RecurringTripsRepositoryImpl @Inject constructor(
    private val qtipCommandClient: QtipCommandClient,
    private val nemtTenantApiClient: NemtTenantApiClient,
    private val serverConfigDataStore: ServerConfigDataStore,
    private val loginSessionDataStore: LoginSessionDataStore,
    private val communityDataStore: CommunityDataStore,
    private val preferredCommunityDataStore: PreferredCommunityDataStore,
    private val oeRegistryDataStore: OeRegistryDataStore,
    private val clientDataStore: ClientDataStore,
    private val recurringTripsDataStore: RecurringTripsDataStore
) : RecurringTripsRepository {

    override suspend fun fetchRecurringTrips(): AppResult<List<RecurringTrip>> {
        val userId = loginSessionDataStore.current.first()?.userId
        if (userId.isNullOrBlank()) {
            AppLogger.w(TAG, "No logged-in user, cannot fetch recurring trips")
            return AppResult.Error("Not logged in")
        }

        val communities = communityDataStore.current.first()
        val preferredCommunityId = preferredCommunityDataStore.current.first()?.takeIf { it.isNotBlank() }
            ?: communities.firstOrNull { it.isPreferred }?.id
            ?: communities.firstOrNull()?.id
            ?: ""

        val oeRegistryValues = oeRegistryDataStore.current.first()
        val siteConfig = oeRegistryValues?.communitySiteConfig(preferredCommunityId)
        val clientType = siteConfig?.valueFor(CLIENT_TYPE_KEY, DEFAULT_CLIENT_TYPE) ?: DEFAULT_CLIENT_TYPE
        val directBookingCommunityIds = oeRegistryValues?.valueFor(DIRECT_BOOKING_COMMUNITIES_KEY)
            ?.split(",")
            ?.map { it.trim() }
            ?: emptyList()
        val usesDirectTripBooking = preferredCommunityId.isNotBlank() &&
            directBookingCommunityIds.any { it.equals(preferredCommunityId, ignoreCase = true) }

        val result = if (usesDirectTripBooking || clientType.equals(PT1_CLIENT_TYPE, ignoreCase = true)) {
            fetchViaTenantApi(siteConfig, clientType)
//            fetchVia7GM(userId)
        } else {
            fetchViaTenantApi(siteConfig, clientType)
//            fetchVia7GM(userId)
        }

        if (result is AppResult.Success) {
            recurringTripsDataStore.save(result.data)
        }
        return result
    }

    private suspend fun fetchVia7GM(userId: String): AppResult<List<RecurringTrip>> {
        val qtipRestBase = serverConfigDataStore.current.first()?.urlFor(QTIP_REST_ENDPOINT_KEY)
        if (qtipRestBase.isNullOrBlank()) {
            AppLogger.w(TAG, "No resolved $QTIP_REST_ENDPOINT_KEY endpoint available, cannot fetch recurring trips")
            return AppResult.Error("QTIP REST endpoint is not available")
        }

        return try {
            val data = listOf(userId, REQUEST_TYPE, REQUEST_FIELD).joinToString(COLUMN_SEPARATOR.toString())
            val response = qtipCommandClient.sendCommand(
                baseUrl = "$qtipRestBase/QTIP_API/",
                command = RECURRING_TRIPS_COMMAND,
                data = data
            )
            AppResult.Success(response.toRecurringTripsFrom7GM())
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            AppLogger.e(TAG, "Failed to fetch recurring trips via $RECURRING_TRIPS_COMMAND", e)
            AppResult.Error("Could not reach the server. Please try again.")
        }
    }

    private suspend fun fetchViaTenantApi(
        siteConfig: CommunitySiteConfig?,
        clientType: String
    ): AppResult<List<RecurringTrip>> {
        val tenantUrl = siteConfig?.valueFor(TENANT_URL_KEY)
        val tenantId = siteConfig?.valueFor(TENANT_ID_KEY)
        if (tenantUrl.isNullOrBlank() || tenantId.isNullOrBlank()) {
            AppLogger.w(TAG, "No tenant configuration available for this community, cannot fetch recurring trips")
            return AppResult.Error("Trip booking is not available for this community")
        }

        return try {
            val medicaidNumber = clientDataStore.current.first()?.medicaidNumber.orEmpty()
            val url = "${tenantUrl}NEMT?cmd=getsoinfobymmis&userid=$TENANT_API_USER_ID&tenantid=$tenantId" +
                "&nemtparam=$medicaidNumber$NEMT_COLUMN_SEPARATOR$clientType"
            val response = nemtTenantApiClient.post(url)
            AppResult.Success(response.toRecurringTripsFromTenantApi())
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            AppLogger.e(TAG, "Failed to fetch recurring trips via tenant API", e)
            AppResult.Error("Could not reach the server. Please try again.")
        }
    }

    private companion object {
        const val TAG = "RecurringTrips"
        const val QTIP_REST_ENDPOINT_KEY = "QREST2_TestServer_IPPORT"
        const val RECURRING_TRIPS_COMMAND = "7GM"
        const val REQUEST_TYPE = "QR"
        const val REQUEST_FIELD = "ALL"
        const val CLIENT_TYPE_KEY = "ClientType"
        const val DEFAULT_CLIENT_TYPE = "NONPT1"
        const val PT1_CLIENT_TYPE = "PT1"
        const val DIRECT_BOOKING_COMMUNITIES_KEY = "RLClientDirectTripBooking"
        const val TENANT_URL_KEY = "TenantUrl"
        const val TENANT_ID_KEY = "TenantId"
        const val TENANT_API_USER_ID = "hbss-cc"
        const val NEMT_COLUMN_SEPARATOR = "^"
        val COLUMN_SEPARATOR = 14.toChar()
    }
}
