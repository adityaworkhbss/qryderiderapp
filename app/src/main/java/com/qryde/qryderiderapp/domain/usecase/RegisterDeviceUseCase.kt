package com.qryde.qryderiderapp.domain.usecase

import com.qryde.qryderiderapp.core.common.AppResult
import com.qryde.qryderiderapp.domain.repository.DeviceRegistrationRepository
import javax.inject.Inject

class RegisterDeviceUseCase @Inject constructor(
    private val repository: DeviceRegistrationRepository
) {
    suspend operator fun invoke(): AppResult<Unit> = repository.registerDevice()
}
