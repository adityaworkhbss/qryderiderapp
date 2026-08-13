package com.qryde.qryderiderapp.domain.usecase

import com.qryde.qryderiderapp.domain.model.User
import com.qryde.qryderiderapp.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveCurrentUserUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    operator fun invoke(): Flow<User?> = authRepository.observeCurrentUser()
}
