package com.qryde.qryderiderapp.domain.repository

import com.qryde.qryderiderapp.core.common.AppResult
import com.qryde.qryderiderapp.domain.model.LoginSession

interface AuthRepository {
    suspend fun hasStoredCredentials(): Boolean
    suspend fun login(userId: String, password: String): AppResult<LoginSession>
    suspend fun silentLogin(): AppResult<LoginSession>
}
