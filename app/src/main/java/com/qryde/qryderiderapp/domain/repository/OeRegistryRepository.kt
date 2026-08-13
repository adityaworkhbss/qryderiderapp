package com.qryde.qryderiderapp.domain.repository

import com.qryde.qryderiderapp.core.common.AppResult
import com.qryde.qryderiderapp.domain.model.OeRegistryValues
import com.qryde.qryderiderapp.domain.model.ServerConfig

interface OeRegistryRepository {
    suspend fun fetchOeRegistryValues(serverConfig: ServerConfig): AppResult<OeRegistryValues>
}
