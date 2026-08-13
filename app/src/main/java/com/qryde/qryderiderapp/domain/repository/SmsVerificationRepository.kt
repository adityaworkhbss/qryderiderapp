package com.qryde.qryderiderapp.domain.repository

import com.qryde.qryderiderapp.core.common.AppResult

interface SmsVerificationRepository {
    suspend fun sendVerificationCode(
        userId: String,
        isoCode: String,
        phone: String,
        code: String
    ): AppResult<Unit>
}
