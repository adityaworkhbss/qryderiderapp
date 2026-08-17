package com.qryde.qryderiderapp.domain.usecase

import com.qryde.qryderiderapp.domain.repository.BraintreeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveBraintreeClientTokenUseCase @Inject constructor(
    private val repository: BraintreeRepository
) {
    operator fun invoke(): Flow<String?> = repository.observeClientToken()
}
