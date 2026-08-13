package com.qryde.qryderiderapp.data.remote.rest

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import javax.inject.Inject

class QtipCommandClient @Inject constructor(
    private val okHttpClient: OkHttpClient
) {
    suspend fun sendCommand(baseUrl: String, command: String, data: String): String =
        withContext(Dispatchers.IO) {
            val requestBody = FormBody.Builder()
                .add("data", data)
                .build()
            val request = Request.Builder()
                .url(baseUrl + command)
                .post(requestBody)
                .build()

            okHttpClient.newCall(request).execute().use { response ->
                response.body?.string().orEmpty()
            }
        }
}
