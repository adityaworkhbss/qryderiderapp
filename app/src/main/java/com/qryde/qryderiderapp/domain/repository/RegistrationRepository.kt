package com.qryde.qryderiderapp.domain.repository

import com.qryde.qryderiderapp.core.common.AppResult
import com.qryde.qryderiderapp.domain.model.NewAccountDetails

interface RegistrationRepository {
    /** Success means available; Error carries the server's "already taken" message. */
    suspend fun checkUserIdAvailable(userId: String): AppResult<Unit>

    /** Success means available; Error carries the server's "already registered" message. */
    suspend fun checkEmailAvailable(email: String): AppResult<Unit>

    suspend fun createAccount(details: NewAccountDetails): AppResult<Unit>
}
