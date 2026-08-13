package com.qryde.qryderiderapp.domain.usecase

import com.qryde.qryderiderapp.core.common.AppResult
import com.qryde.qryderiderapp.domain.repository.SmsVerificationRepository
import javax.inject.Inject

class SendVerificationCodeUseCase @Inject constructor(
    private val repository: SmsVerificationRepository
) {
    suspend operator fun invoke(
        userId: String,
        isoCode: String,
        phone: String,
        code: String
    ): AppResult<Unit> = repository.sendVerificationCode(userId, isoCode, phone, code)
}
