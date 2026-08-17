package com.qryde.qryderiderapp.domain.usecase

import com.qryde.qryderiderapp.domain.model.NemtClientInfo
import com.qryde.qryderiderapp.domain.repository.ClientDataRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveClientDataUseCase @Inject constructor(
    private val repository: ClientDataRepository
) {
    operator fun invoke(): Flow<NemtClientInfo?> = repository.observeClientData()
}
