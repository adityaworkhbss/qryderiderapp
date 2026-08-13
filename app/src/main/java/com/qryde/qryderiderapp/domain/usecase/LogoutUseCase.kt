package com.qryde.qryderiderapp.domain.usecase

import com.qryde.qryderiderapp.domain.repository.AuthRepository
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke() = authRepository.logout()
}
