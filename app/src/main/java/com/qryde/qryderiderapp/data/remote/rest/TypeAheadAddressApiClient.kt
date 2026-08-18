package com.qryde.qryderiderapp.data.remote.rest

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonObject
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

class TypeAheadAddressApiClient @Inject constructor(
    private val okHttpClient: OkHttpClient
) {
    suspend fun searchAddresses(baseUrl: String, token: String, deviceId: String, query: String): String =
        withContext(Dispatchers.IO) {
            val requestBody = buildJsonObject {
                putJsonObject("header") {
                    put("authorization", "bearer $token")
                    put("deviceID", deviceId)
                    put("origin", "MOBILE_APP")
                    put("commandId", COMMAND_ID)
                }
                putJsonObject("data") {
                    put("addressInputInfo", query)
                }
                put("commandId", COMMAND_ID)
            }

            val request = Request.Builder()
                .url(baseUrl)
                .post(requestBody.toString().toRequestBody(JSON_MEDIA_TYPE))
                .build()

            okHttpClient.newCall(request).execute().use { response ->
                response.body?.string().orEmpty()
            }
        }

    private companion object {
        const val COMMAND_ID = "TYPE_AHEAD_ADDRESS"
        val JSON_MEDIA_TYPE = "application/json".toMediaType()
    }
}
