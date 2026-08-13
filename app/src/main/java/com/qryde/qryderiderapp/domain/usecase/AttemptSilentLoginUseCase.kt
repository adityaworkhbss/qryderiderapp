package com.qryde.qryderiderapp.domain.usecase

import com.qryde.qryderiderapp.core.common.AppResult
import com.qryde.qryderiderapp.domain.model.LoginSession
import com.qryde.qryderiderapp.domain.repository.AuthRepository
import javax.inject.Inject

/**
 * Returns null when there are no stored credentials to try at all, so the
 * caller can tell "not attempted" apart from "attempted and failed".
 */
class AttemptSilentLoginUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(): AppResult<LoginSession>? {
        if (!repository.hasStoredCredentials()) return null
        return repository.silentLogin()
    }
}
