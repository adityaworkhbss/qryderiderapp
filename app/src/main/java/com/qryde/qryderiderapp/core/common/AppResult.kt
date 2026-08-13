package com.qryde.qryderiderapp.core.common

/** Outcome of a use case / repository call, kept out of Presentation's raw exception handling. */
sealed interface AppResult<out T> {
    data class Success<T>(val data: T) : AppResult<T>
    data class Error(val message: String) : AppResult<Nothing>
}
