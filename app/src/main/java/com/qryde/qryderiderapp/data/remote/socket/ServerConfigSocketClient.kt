package com.qryde.qryderiderapp.data.remote.socket

import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeout
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class ServerConfigSocketClient @Inject constructor(
    private val okHttpClient: OkHttpClient
) {
    suspend fun fetchConfig(
        socketUrl: String,
        message: String,
        timeoutMillis: Long = DEFAULT_TIMEOUT_MILLIS
    ): String = withTimeout(timeoutMillis) {
        suspendCancellableCoroutine { continuation ->
            val request = Request.Builder().url(socketUrl).build()
            val webSocket = okHttpClient.newWebSocket(
                request,
                object : WebSocketListener() {
                    override fun onOpen(webSocket: WebSocket, response: Response) {
                        webSocket.send(message)
                    }

                    override fun onMessage(webSocket: WebSocket, text: String) {
                        webSocket.close(NORMAL_CLOSURE_CODE, null)
                        if (!continuation.isActive) return
                        if (text.isBlank() || text.equals(UNKNOWN_REQUEST, ignoreCase = true)) {
                            continuation.resumeWithException(
                                IllegalStateException("Config server returned an unusable response.")
                            )
                        } else {
                            continuation.resume(text)
                        }
                    }

                    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                        if (continuation.isActive) continuation.resumeWithException(t)
                    }

                    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                        if (continuation.isActive) {
                            continuation.resumeWithException(
                                IllegalStateException("Config server closed the connection before responding.")
                            )
                        }
                    }
                }
            )
            continuation.invokeOnCancellation { webSocket.cancel() }
        }
    }

    private companion object {
        const val DEFAULT_TIMEOUT_MILLIS = 30_000L
        const val NORMAL_CLOSURE_CODE = 1000
        const val UNKNOWN_REQUEST = "Unknown Request"
    }
}
