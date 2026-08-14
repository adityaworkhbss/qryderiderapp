package com.qryde.qryderiderapp.domain.repository

import com.qryde.qryderiderapp.core.common.AppResult

interface ForgotPasswordRepository {
    suspend fun requestPasswordReset(userId: String, email: String): AppResult<String>
}
