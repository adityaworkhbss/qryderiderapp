package com.qryde.qryderiderapp.core.logging

import android.util.Log

object AppLogger {
    const val TAG_REST = "REST"
    private const val DEFAULT_TAG = "QrydeRider"

    @Volatile
    var isEnabled: Boolean = false
        private set

    fun init(isEnabled: Boolean) {
        this.isEnabled = isEnabled
    }

    fun d(tag: String = DEFAULT_TAG, message: String) {
        if (isEnabled) Log.d(tag, message)
    }

    fun i(tag: String = DEFAULT_TAG, message: String) {
        if (isEnabled) Log.i(tag, message)
    }

    fun w(tag: String = DEFAULT_TAG, message: String, throwable: Throwable? = null) {
        if (isEnabled) Log.w(tag, message, throwable)
    }

    fun e(tag: String = DEFAULT_TAG, message: String, throwable: Throwable? = null) {
        if (isEnabled) Log.e(tag, message, throwable)
    }

    fun rest(message: String) {
        if (isEnabled) Log.d(TAG_REST, message)
    }
}
