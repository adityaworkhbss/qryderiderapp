package com.qryde.qryderiderapp.core.network

import com.qryde.qryderiderapp.core.logging.AppLogger
import okhttp3.Interceptor
import okhttp3.Response

class RestLoggingInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        if (!AppLogger.isEnabled) return chain.proceed(request)

        AppLogger.rest("--> ${request.method} ${request.url}")

        val startNanos = System.nanoTime()
        val response = try {
            chain.proceed(request)
        } catch (e: Exception) {
            AppLogger.rest("<-- FAILED ${request.method} ${request.url}: ${e.message}")
            throw e
        }
        val tookMs = (System.nanoTime() - startNanos) / 1_000_000
        val body = response.peekBody(MAX_LOGGED_BODY_BYTES).string()

        AppLogger.rest("<-- ${response.code} ${request.method} ${request.url} (${tookMs}ms)\n$body")

        return response
    }

    private companion object {
        const val MAX_LOGGED_BODY_BYTES = 4096L
    }
}
