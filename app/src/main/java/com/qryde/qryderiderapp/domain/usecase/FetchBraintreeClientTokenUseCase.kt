package com.qryde.qryderiderapp.domain.usecase

import com.qryde.qryderiderapp.core.common.AppResult
import com.qryde.qryderiderapp.domain.repository.BraintreeRepository
import javax.inject.Inject

class FetchBraintreeClientTokenUseCase @Inject constructor(
    private val repository: BraintreeRepository
) {
    suspend operator fun invoke(): AppResult<String> = repository.fetchAndPersistClientToken()
}
