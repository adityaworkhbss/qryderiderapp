package com.qryde.qryderiderapp.domain.usecase

import com.qryde.qryderiderapp.core.common.AppResult
import com.qryde.qryderiderapp.domain.repository.RegistrationRepository
import javax.inject.Inject

class CheckUserIdAvailableUseCase @Inject constructor(
    private val repository: RegistrationRepository
) {
    suspend operator fun invoke(userId: String): AppResult<Unit> =
        repository.checkUserIdAvailable(userId)
}
