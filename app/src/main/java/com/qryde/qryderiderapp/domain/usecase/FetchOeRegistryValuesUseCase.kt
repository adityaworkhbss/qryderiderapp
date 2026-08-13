package com.qryde.qryderiderapp.domain.usecase

import com.qryde.qryderiderapp.core.common.AppResult
import com.qryde.qryderiderapp.domain.model.OeRegistryValues
import com.qryde.qryderiderapp.domain.model.ServerConfig
import com.qryde.qryderiderapp.domain.repository.OeRegistryRepository
import javax.inject.Inject

class FetchOeRegistryValuesUseCase @Inject constructor(
    private val repository: OeRegistryRepository
) {
    suspend operator fun invoke(serverConfig: ServerConfig): AppResult<OeRegistryValues> =
        repository.fetchOeRegistryValues(serverConfig)
}
