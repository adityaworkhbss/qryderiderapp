package com.qryde.qryderiderapp.domain.usecase

import com.qryde.qryderiderapp.core.common.AppResult
import com.qryde.qryderiderapp.domain.model.NewAccountDetails
import com.qryde.qryderiderapp.domain.repository.RegistrationRepository
import javax.inject.Inject

class CreateAccountUseCase @Inject constructor(
    private val repository: RegistrationRepository
) {
    suspend operator fun invoke(details: NewAccountDetails): AppResult<Unit> =
        repository.createAccount(details)
}
