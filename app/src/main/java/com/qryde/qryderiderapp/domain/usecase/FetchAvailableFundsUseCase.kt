package com.qryde.qryderiderapp.domain.usecase

import com.qryde.qryderiderapp.core.common.AppResult
import com.qryde.qryderiderapp.domain.repository.AvailableFundsRepository
import javax.inject.Inject

class FetchAvailableFundsUseCase @Inject constructor(
    private val repository: AvailableFundsRepository
) {
    suspend operator fun invoke(): AppResult<Double> = repository.fetchAvailableFunds()
}
