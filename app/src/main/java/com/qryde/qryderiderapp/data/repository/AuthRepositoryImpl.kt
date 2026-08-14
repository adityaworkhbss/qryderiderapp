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


/*
   req : nt5656Hbss@2004......EN
   res : 5G~OKnt56568307544913QRyde...1Northend Testtolop99333@dwarkm.com......US-180007E3C757A-599B-4E1F-9529-4BC1B0CD1E2F...AIzaSyDfSZu84eOT0pX8shLmaj2-Jii4w3fHLH8......N......US........................N...Ryderv2863E37943365331633453366376E328E327E321E37EE7331573E55214582475966446695772E521E52EE72EN1516704NNNNNNNNNNNNNNNNN{"CommunityInfo":[{"CommunityId":"AlPoTransit","CommunityName":"All Points Transit","pref_comm":"N","result_combo":"L,D,R","address":"175 Merchant Dr, Montrose, CO 81401, USA","homeurl":"...","comm_access":"Public","request_type":"CA","Phone":null,"email":null,"logo":null,"description":null,"latitude":null,"longitude":null},{"CommunityId":"BRATTLEBOROCOMM","CommunityName":"BRATTLEBOROCOMM","pref_comm":"N","result_combo":"D,R","address":"706 Rockingham Rd, Bellows Falls, VT 05101, USA","homeurl":"...","comm_access":"Public","request_type":"CA","Phone":null,"email":null,"logo":null,"description":null,"latitude":null,"longitude":null},{"CommunityId":"GADABOUTCOMM","CommunityName":"GADABOUT Transportation","pref_comm":"N","result_combo":"L,D,R","address":"Gadabout Transportation Services, 737 Willow Ave, Ithaca, NY 14850, USA","homeurl":"...","comm_access":"Public","request_type":"CA","Phone":null,"email":null,"logo":null,"description":null,"latitude":null,"longitude":null},{"CommunityId":"MICROMOOCOMM","CommunityName":"MicroMOO ","pref_comm":"N","result_combo":"D,R","address":"706 Rockingham Rd, Bellows Falls, VT 05101, USA","homeurl":"...","comm_access":"Public","request_type":"CA","Phone":null,"email":null,"logo":null,"description":null,"latitude":null,"longitude":null},{"CommunityId":"MYNKYRIDECOMM","CommunityName":"MYNKYRIDE ","pref_comm":"N","result_combo":"D,R","address":"210 E Blanton St, Owenton, KY 40359, USA","homeurl":"...","comm_access":"Public","request_type":"CA","Phone":null,"email":null,"logo":null,"description":null,"latitude":null,"longitude":null},{"CommunityId":"NORTHENDCOMM","CommunityName":"NORTHENDCOMM","pref_comm":"N","result_combo":"L,D,R","address":"1500 North 24th Street ste 111, Omaha, Nebraska 68110, USA","homeurl":"...","comm_access":"Public","request_type":"CA","Phone":null,"email":null,"logo":null,"description":null,"latitude":null,"longitude":null},{"CommunityId":"OPCCOMM","CommunityName":"OPC","pref_comm":"Y","result_combo":"L,D,R","address":"650 Letica Drive, Rochester, MI, USA","homeurl":"...","comm_access":"Public","request_type":"CA","Phone":null,"email":null,"logo":null,"description":null,"latitude":null,"longitude":null},{"CommunityId":"qryde","CommunityName":"de rere","pref_comm":"N","result_combo":"D,L,R","address":"...","homeurl":"qryde.com","comm_access":"","request_type":"CA","Phone":null,"email":null,"logo":null,"description":null,"latitude":null,"longitude":null},{"CommunityId":"SPARTANMTCOMM","CommunityName":"SPARTAN MT Community ","pref_comm":"N","result_combo":"L,D,R","address":"1105 TX-114, Levelland, TX 79336, United States","homeurl":"...","comm_access":"Public","request_type":"CA","Phone":null,"email":null,"logo":null,"description":null,"latitude":null,"longitude":null},{"CommunityId":"SPARTANPTCOMM","CommunityName":"SPARTAN PT Community ","pref_comm":"N","result_combo":"L,D,R","address":"1105 TX-114, Levelland, TX 79336, United States","homeurl":"...","comm_access":"Public","request_type":"CA","Phone":null,"email":null,"logo":null,"description":null,"latitude":null,"longitude":null}]}...?response_type=code&client_id=RU9Pd64ovn9bfFNfwyTNFrQOXh1hMOFE&&redirect_uri=...N...0X+ (X * 10/100)+(((X+ X * 10/100)*2.9/100)+ 0.3)NN............N
 */

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
