package com.qryde.qryderiderapp.domain.usecase

import com.qryde.qryderiderapp.core.common.AppResult
import com.qryde.qryderiderapp.domain.repository.RegistrationRepository
import javax.inject.Inject

class CheckEmailAvailableUseCase @Inject constructor(
    private val repository: RegistrationRepository
) {
    suspend operator fun invoke(email: String): AppResult<Unit> =
        repository.checkEmailAvailable(email)
}
