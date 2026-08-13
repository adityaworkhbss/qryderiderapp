package com.qryde.qryderiderapp.domain.usecase

import com.qryde.qryderiderapp.core.common.AppResult
import com.qryde.qryderiderapp.domain.model.ServerConfig
import com.qryde.qryderiderapp.domain.repository.ServerConfigRepository
import javax.inject.Inject

class ResolveServerConfigUseCase @Inject constructor(
    private val serverConfigRepository: ServerConfigRepository
) {
    suspend operator fun invoke(): AppResult<ServerConfig> = serverConfigRepository.resolveServerConfig()
}
