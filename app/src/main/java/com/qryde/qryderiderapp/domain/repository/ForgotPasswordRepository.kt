package com.qryde.qryderiderapp.domain.repository

import com.qryde.qryderiderapp.core.common.AppResult

interface ForgotPasswordRepository {
    /** Success carries the server's message to show (e.g. "temporary password sent to..."). */
    suspend fun requestPasswordReset(userId: String, email: String): AppResult<String>
}
