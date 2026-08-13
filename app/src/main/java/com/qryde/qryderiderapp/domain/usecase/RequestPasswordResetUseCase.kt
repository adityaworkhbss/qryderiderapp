package com.qryde.qryderiderapp.domain.usecase

import com.qryde.qryderiderapp.core.common.AppResult
import com.qryde.qryderiderapp.domain.repository.ForgotPasswordRepository
import javax.inject.Inject

class RequestPasswordResetUseCase @Inject constructor(
    private val repository: ForgotPasswordRepository
) {
    suspend operator fun invoke(userId: String, email: String): AppResult<String> =
        repository.requestPasswordReset(userId, email)
}
