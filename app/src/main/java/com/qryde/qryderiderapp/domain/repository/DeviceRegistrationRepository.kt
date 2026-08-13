package com.qryde.qryderiderapp.domain.repository

import com.qryde.qryderiderapp.core.common.AppResult

interface DeviceRegistrationRepository {
    suspend fun registerDevice(): AppResult<Unit>
}
