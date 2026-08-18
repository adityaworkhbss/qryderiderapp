package com.qryde.qryderiderapp.data.remote.rest

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import javax.inject.Inject

/** Hits a per-community NEMT tenant endpoint directly (not the shared QTIP_API base). */
class NemtTenantApiClient @Inject constructor(
    private val okHttpClient: OkHttpClient
) {
    suspend fun post(url: String): String =
        withContext(Dispatchers.IO) {
            val request = Request.Builder()
                .url(url)
                .post(FormBody.Builder().build())
                .build()

            okHttpClient.newCall(request).execute().use { response ->
                response.body?.string().orEmpty()
            }
        }
}
