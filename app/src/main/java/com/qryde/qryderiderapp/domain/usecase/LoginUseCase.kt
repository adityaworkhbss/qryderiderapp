package com.qryde.qryderiderapp.domain.usecase

import com.qryde.qryderiderapp.core.common.AppResult
import com.qryde.qryderiderapp.domain.model.User
import com.qryde.qryderiderapp.domain.repository.AuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(phoneNumber: String, password: String): AppResult<User> {
        if (phoneNumber.isBlank() || password.isBlank()) {
            return AppResult.Error("Phone number and password are required.")
        }
        return authRepository.login(phoneNumber, password)
    }
}
