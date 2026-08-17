package com.qryde.qryderiderapp.domain.usecase

import com.qryde.qryderiderapp.core.common.AppResult
import com.qryde.qryderiderapp.domain.model.NemtClientInfo
import com.qryde.qryderiderapp.domain.repository.ClientDataRepository
import javax.inject.Inject

class FetchClientDataUseCase @Inject constructor(
    private val repository: ClientDataRepository
) {
    suspend operator fun invoke(): AppResult<NemtClientInfo?> = repository.fetchClientDataIfEligible()
}
