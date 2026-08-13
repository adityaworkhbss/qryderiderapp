package com.qryde.qryderiderapp.domain.repository

import com.qryde.qryderiderapp.core.common.AppResult
import com.qryde.qryderiderapp.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun login(phoneNumber: String, password: String): AppResult<User>
    suspend fun logout()
    fun observeCurrentUser(): Flow<User?>
}
