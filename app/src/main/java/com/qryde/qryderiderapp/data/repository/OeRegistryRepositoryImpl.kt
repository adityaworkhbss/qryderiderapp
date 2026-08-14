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


/*
*
* req : QRyde
* res : 17CV~-https://reststg.qryde.com:10100/SMS_REST/ABTR_TRIPSFILTERNew Trips^ALL|Recurring Trips^QR|Accepted Daily Trips^ACaccesstokenYdSx3AKTA4SJ9rcKYqjyKNJ1tlwOfM2idLjZAYvZ8UAIBAuD9mAgency_FareAndFee{"CollierCounty":{"County_Fee":16,"ADA_Fee":4},"XYZ":{"County_Fee":0,"ADA_Fee":0}}AN_SaveCCEnabledCommunitiesMARTMICRO,MARTPARA,QRYDEApps_UpdateConfig{"Apps_UpdateConfig":{"IOS":{"com.QRyde.Rider":"1.25,N","com.QRyde.QRydeDriver":"1.21,N","com.QRyde.Supplier":"1.6,N","com.QRyde.GDOT":"1.9,N","com.QRyde.Feonix":"2.0,N","com.QRyde.OxfordTC":"1.1,N","com.QRyde.SACRAMENTOSTG":"1.11,Y"},"ANDROID":{"com.QRyde.Marketplace":"1.0.51,N","com.QRyde.QRydeDriver":"1.0.22,N","com.QRyde.QRydeSupplier":"1.0.8,N","com.QRyde.GDOT":"1.0.4,N","com.QRyde.OxfordTC":"1.0.4,N","com.QRyde.FeonixRide":"1.0.15,N","com.QRyde.SACRAMENTO":"2.0.20,Y"}}}AuthNetAcceptJsUrlhttps://jstest.authorize.net/v1/Accept.jsAuthNetApiLoginId68t2jS5CpAuthNetPublicClientKey9wjr9fC99GdNbGLVx88bhyepwtTECR529cS4f7CrKJgJg4Sqe7V2U93kLZuL7CrUAutoCallOutFromDriverAppUCARE^2^10AvlGroupEmailIdajay@hbssweb.com;BaggageCharges10BPTC_CP_URLhttps://cp-bptcstg.qryde.com/cp/NemtBookRide.htmlBrainTreeMerchantId...BROKERAGE_FILTERNew Trips^ALL|Recurring Trips^QR|Accepted Daily Trips^ACbrokerage_grid_config{"BROKERAGE":{"FARE_SHOW":false,"SERVICE_SHOW":true},"QRYDE":{"FARE_SHOW":true,"SERVICE_SHOW":true}}BROKERAGEDRIVERACTIVATIONEMAILajay.337386@gmail.comcall_for_detail_comms{"call_for_details_communities": {"MARTMICRO":{"Fare":"00.00", "Label":"Fare Free"}, "XYZ":{"Fare":"00.09", "Label":"..."}}}CancellationCharge5CancelRLUnpaidTrip["MARTAOPPCOMM","PHTCOMM" ]CCTXNSettlementDays5CICOA_RISTRICTED_COUNTY[MARION,MORGAN]CLIENTACTIVATIONINQRRL["MARTAOPPCOMM","PHTCOMM" ]com.QRyde.FeonixRideL,D,Ucom.QRyde.MarketPlaceL,Dcom.QRyde.OxfordTCD,Gcom.QRyde.PhhealthcareD,Gcom.QrydeConsumer.DCTBD,Gcom.QrydeConsumer.GdotD,Gcom.QrydeConsumer.PHTD,Gcom.QrydeConsumer.SCCTDD,GContentValidationErrMsgPlease do not use (&,*,@,^,'') characters.COVID19_NOTIFICATION{"feonixride":{"Message":"NOTICE: The AARP Ride@50+ Program has suspended all service through June 30, 2020. No new rides by either new or existing users may be booked via the Program at this time. At AARP, our top priority is the health and well-being of staff, volunteers, members and the community at large. This situation is dynamic and AARP will monitor it closely for needed changes to this timeline.","IsMsgShow":"N","CommunityMessage":"All participants in the AARP Ride@50+ Program must complete a COVID Waiver before booking rides.  If this is the first time booking a ride for this client, please instruct them to call {0}1-888-808-3977{1} and complete a waiver over the phone.  If they do not have a waiver on file, we will be unable to authorize the trip.","CommunityMessage_ES":"“Responda ‘Sí’ para confirmar que comprende y acepta el riesgo de que podría exponerse a una infección de COVID-19 al recibir servicios de transporte a través del programa AARP Ride@50+, y que por este medio exime y libera a AARP, sus afiliadas y sus respectivos empleados, directores, funcionarios, agentes y proveedores de toda responsabilidad, demanda o reclamo de cualquier naturaleza con respecto a, entre otros y sin carácter limitativo, toda enfermedad, lesión, muerte o cualquier otro daño que pueda surgir de su participación en el programa AARP Ride@50+”.","IsMsgShowAtCommunity":"Y","RyderMessage":"By clicking the box below, I confirm that I understand and acknowledge the risk that I may be exposed to COVID-19 in the receipt of transportation services made available through the AARP Ride@50+ Program. I hereby release, discharge, and hold harmless AARP and its affiliates and their respective employees, directors, officers, agents, and vendors from any and all liability, claims, and demands of every kind with respect to, including without limitation, any illness, injury, death, or any other harm that may arise from my participation in the Ride@50+ P
*
* */

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
