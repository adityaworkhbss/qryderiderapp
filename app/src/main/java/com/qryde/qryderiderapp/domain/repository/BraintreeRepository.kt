package com.qryde.qryderiderapp.domain.repository

import com.qryde.qryderiderapp.core.common.AppResult
import kotlinx.coroutines.flow.Flow

interface BraintreeRepository {
    suspend fun fetchAndPersistClientToken(): AppResult<String>
    fun observeClientToken(): Flow<String?>
}
