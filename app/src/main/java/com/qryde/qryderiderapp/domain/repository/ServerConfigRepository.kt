package com.qryde.qryderiderapp.domain.repository

import com.qryde.qryderiderapp.core.common.AppResult
import com.qryde.qryderiderapp.domain.model.ServerConfig

interface ServerConfigRepository {
    suspend fun resolveServerConfig(): AppResult<ServerConfig>
}
