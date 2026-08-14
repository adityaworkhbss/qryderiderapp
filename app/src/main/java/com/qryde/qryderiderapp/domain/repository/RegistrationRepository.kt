package com.qryde.qryderiderapp.domain.repository

import com.qryde.qryderiderapp.core.common.AppResult
import com.qryde.qryderiderapp.domain.model.NewAccountDetails

interface RegistrationRepository {

    suspend fun checkUserIdAvailable(userId: String): AppResult<Unit>

    suspend fun checkEmailAvailable(email: String): AppResult<Unit>

    suspend fun createAccount(details: NewAccountDetails): AppResult<Unit>
}
