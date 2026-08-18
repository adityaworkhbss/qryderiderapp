package com.qryde.qryderiderapp.core.network

import com.qryde.qryderiderapp.core.logging.AppLogger
import okhttp3.FormBody
import okhttp3.Interceptor
import okhttp3.Response
import okio.Buffer

class RestLoggingInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        if (!AppLogger.isEnabled) return chain.proceed(request)

        // QtipCommandClient always posts a single "data" form field - read it back
        // decoded, rather than logging the raw URL-encoded wire bytes. Anything
        // else (e.g. a JSON body) is peeked as plain text instead.
        val requestBody = request.body
        val dataValue = when {
            requestBody is FormBody && requestBody.size > 0 -> requestBody.value(0)
            requestBody != null -> Buffer().also { requestBody.writeTo(it) }.readUtf8()
            else -> null
        }
        val requestLine = "--> ${request.method} ${request.url}" +
            (dataValue?.let { "  data :: $it" } ?: "")
        AppLogger.rest(requestLine)

        val startNanos = System.nanoTime()
        val response = try {
            chain.proceed(request)
        } catch (e: Exception) {
            AppLogger.rest("<-- FAILED ${request.method} ${request.url}: ${e.message}")
            throw e
        }
        val tookMs = (System.nanoTime() - startNanos) / 1_000_000
        val responseBody = response.peekBody(MAX_LOGGED_BODY_BYTES).string()

        AppLogger.rest("<-- ${response.code} ${request.method} ${request.url} (${tookMs}ms)\n$responseBody")

        return response
    }

    private companion object {
        const val MAX_LOGGED_BODY_BYTES = 4096L
    }
}
